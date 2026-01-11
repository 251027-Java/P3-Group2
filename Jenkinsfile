def gdata = [
    allSuccessful: true,
    attributes: [:],
]

pipeline {
    agent any

    tools {
        jdk 'java25'
        maven 'maven3'
    }

    stages {
        stage('setup') {
            steps {
                script {
                    library "primary@${env.CHANGE_BRANCH ?: env.GIT_BRANCH}"
                    util.showEnv()
                    util.updateDisplayName()

                    gdata.attributes.putAll(gitUtil.getChanges())
                    gdata.attributes['pr:default'] = gitUtil.isPrToDefaultBranch()
                    gdata.attributes['default'] = gitUtil.isDefaultBranch()
                    gdata.attributes.putAll(pipelineUtil.getAttributes())

                    util.printMap(gdata)
                }
            }
        }

        stage('frontend') {
            agent {
                docker {
                    image 'node:lts-alpine'
                }
            }

            steps {
                script {
                    echo 'empty'
                }
            }
        }

        stage('backend') {
            steps {
                script {
                    pipelineUtil.getQualifyingDirs().each { path ->
                        if (!path.startsWith('backend')) { return }

                        def shouldRun = (
                            !gdata.attributes['skip']
                            && (
                                gdata.attributes['run']
                                || (
                                    (gdata.attributes['pr:default'] || gdata.attributes['default'])
                                    && (gdata.attributes["change:${path}".toString()])
                                )
                            )
                        )

                        def settings = pipelineUtil.getSettings path: path

                        if (settings.lint.enabled && shouldRun) {
                            stage("lint ${path}") {
                                def success = backend.lint path: path, settings: settings
                                gdata.allSuccessful &= success
                            }
                        }

                        if (settings.test.enabled && shouldRun) {
                            stage("test ${path}") {
                                def success = backend.test path: path, settings: settings
                                gdata.allSuccessful &= success
                            }
                        }

                        if (settings.build.enabled && shouldRun) {
                            stage("build ${path}") {
                                def success = backend.build path: path, settings: settings
                                gdata.allSuccessful &= success
                                gdata.attributes["build:${path}".toString()] = success
                            }
                        }

                        def shouldBuildImage = (
                            gdata.attributes["build:${path}".toString()]
                            || gdata.attributes['imageall']
                            || gdata.attributes["image:${path}".toString()]
                        )

                        if (settings.image.enabled && shouldBuildImage) {
                            stage("image ${path}") {
                                def success = dockerUtil.image path: path, settings: settings,
                                    credId: 'docker-hub-cred', latest: gdata.attributes['default']
                                gdata.allSuccessful &= success
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                pipelineUtil.cleanup()

                if (!gdata.allSuccessful) {
                    error 'Failed'
                }
            }
        }
    }
}
