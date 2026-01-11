def gdata = [
    frontend: false,
    backend: false,
    isPrToDefault: false,
    isDefault: false,
    changes: [] as Set,
    build: [:],
    allSuccessful: true,
]

pipeline {
    agent any

    tools {
        jdk 'java25'
        maven 'maven3'
    }

    environment {
        INT_DOCKER_REPO = 'minidomo/gemdeck'
    }

    stages {
        stage('setup') {
            steps {
                script {
                    library "primary@${env.CHANGE_BRANCH ?: env.GIT_BRANCH}"
                    util.showEnv()
                    util.updateDisplayName()

                    gdata.changes.addAll(gitUtil.getChanges())
                    gdata.frontend = gdata.changes.any { it.startsWith('frontend') }
                    gdata.backend = gdata.changes.any { it.startsWith('backend') }
                    gdata.isPrToDefault = gitUtil.isPrToDefaultBranch()
                    gdata.isDefault = gitUtil.isDefaultBranch()

                    echo "${pipelineUtil.getQualifyingDirs()}"

                    util.printMap(gdata)
                }
            }
        }

        stage('frontend') {
            when {
                expression { gdata.frontend }
                beforeAgent true
            }

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
            when {
                expression { gdata.backend }
            }

            steps {
                script {
                    gdata.changes.findAll { it.startsWith('backend') }.each { path ->
                        def settings = pipelineUtil.getSettings path: path

                        if (settings.lint.enabled) {
                            stage("lint ${path}") {
                                def success = backend.lint path: path, settings: settings
                                gdata.allSuccessful &= success
                            }
                        }

                        if (settings.test.enabled) {
                            stage("test ${path}") {
                                def success = backend.test path: path, settings: settings
                                gdata.allSuccessful &= success
                            }
                        }

                        if (settings.build.enabled) {
                            stage("build ${path}") {
                                def success = backend.build path: path, settings: settings
                                gdata.allSuccessful &= success
                                gdata.build[path] = success
                            }
                        }

                        if (settings.image.enabled && gdata.build[path]) {
                            stage("image ${path}") {
                                def success = dockerUtil.image path: path, settings: settings,
                                    credId: 'docker-hub-cred', latest: gdata.isDefault
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
