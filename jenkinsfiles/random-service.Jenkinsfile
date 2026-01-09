pipeline {
    agent any

    tools {
        jdk 'java25'
        maven 'maven3'
    }

    environment {
        GITHUB_DEFAULT_BRANCH = 'main'
        TARGET_DIRECTORY = 'backend/random-service'
    }

    stages {
        stage('check run requirements') {
            steps {
                script {
                    sh 'printenv | sort'

                    currentBuild.displayName = "${currentBuild.displayName} ${shortSha()}"
                }
            }
        }

        stage('lint frontend') {
            steps {
                dir('frontend') {
                    script {
                        def chName = 'lint / frontend'
                        def res = null

                        publishChecks name: chName, title: 'Pending', status: 'IN_PROGRESS'

                        try {
                            sh 'biome ci --colors=off --reporter=summary > frontend-code-quality.txt'
                            res = [con: 'SUCCESS', title: 'Success']
                        } catch (err) {
                            markStageFailure()
                            echo "${err}"
                            res = [con: 'FAILURE', title: 'Failed']
                        }

                        def output = readFile file: 'frontend-code-quality.txt'
                        echo output

                        publishChecks name: chName, conclusion: res.con, title: res.title,
                            summary: limitText(output)
                    }
                }
            }
        }

        stage('build frontend') {
            when {
                not { expression { fbfm.run.skip } }
                anyOf {
                    expression { fbfm.run.force }
                    allOf {
                        anyOf {
                            expression { fbfm.isPrToDefault }
                            expression { fbfm.isDefault }
                        }
                        anyOf {
                            expression { fbfm.changes.frontend }
                            expression { fbfm.changes.jenkinsfile }
                        }
                    }
                }
                beforeAgent true
            }

            agent {
                docker {
                    image 'node:lts-alpine'
                }
            }

            steps {
                script {
                    def chName = 'build / frontend'
                    publishChecks name: chName, title: 'Pending', status: 'IN_PROGRESS'

                    dir('frontend') {
                        try {
                            sh 'npm ci && npm run build'
                            publishChecks name: chName, conclusion: 'SUCCESS', title: 'Success'
                            fbfm.build.frontend = true
                        } catch (err) {
                            markStageFailure()
                            echo "${err}"
                            publishChecks name: chName, conclusion: 'FAILURE', title: 'Failed'
                        }
                    }
                }
            }

            post {
                always {
                    // need to clean the workspace here because it's a separate workspace from the workspace that gets
                    // cleaned at the end of this file. this installs node_modules, so it can take up a large amount of
                    // disk
                    cleanWs()
                }
            }
        }

        stage('image frontend') {
            when {
                expression { fbfm.build.frontend && fbfm.canBuild }
            }

            steps {
                script {
                    fbfmBuildImage(name: 'frontend', directory: 'frontend', tagSeries: 'fe',
                        dockerRepo: 'minidomo/feedbackfm', pushLatest: fbfm.isDefault
                    )
                }
            }
        }

        stage('test build image microservices') {
            steps {
                script {
                    fbfm.microservices.values().each { service ->
                        def shouldRun = !fbfm.run.skip &&
                            (
                                fbfm.run.force ||
                                (
                                    (fbfm.isPrToDefault || fbfm.isDefault) &&
                                    (fbfm.changes.jenkinsfile || fbfm.changes[service.name])
                                )
                            )

                        if (shouldRun) {
                            if (service.test) {
                                stage("test ${service.name}") {
                                    fbfmTestMicroservice(name: service.name, directory: service.directory)
                                }
                            }

                            stage("build ${service.name}") {
                                fbfmBuildMicroservice(name: service.name, directory: service.directory)
                            }
                        }

                        if (fbfm.build[service.name] && fbfm.canBuild) {
                            stage("image ${service.name}") {
                                fbfmBuildImage(directory: service.directory, tagSeries: "be-${service.name}",
                                    dockerRepo: 'minidomo/feedbackfm', pushLatest: fbfm.isDefault
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            sh '.jenkins/scripts/docker-cleanup.sh'
            // delete the workspace after to prevent large disk usage
            cleanWs()

            script {
                if (!fbfm.allSuccessful) {
                    error 'Not all stages were successful'
                }
            }
        }
    }
}
