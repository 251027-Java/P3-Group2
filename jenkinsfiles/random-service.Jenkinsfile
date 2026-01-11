pipeline {
    agent any

    tools {
        jdk 'java25'
        maven 'maven3'
    }

    environment {
        TARGET_DIRECTORY = 'backend/random-service'
        DOCKER_REPO = 'minidomo/gemdeck'
    }

    stages {
        stage('check run requirements') {
            steps {
                script {
                    library "primary@${env.CHANGE_BRANCH ?: env.GIT_BRANCH}"
                    util.showEnv()
                    util.updateDisplayName()
                }
            }
        }

        stage('lint') {
            steps {
                script {
                    backend.javaLint path: env.TARGET_DIRECTORY
                }
            }
        }

        stage('test') {
            steps {
                script {
                    backend.javaTest path: env.TARGET_DIRECTORY
                }
            }
        }

        stage('build') {
            steps {
                script {
                    backend.javaBuild path: env.TARGET_DIRECTORY
                }
            }
        }

        stage('image') {
            steps {
                script {
                    dockerUtil.image path: env.TARGET_DIRECTORY, repo: env.DOCKER_REPO,
                        credId: 'docker-hub-cred', latest: true
                }
            }
        }
    }

    post {
        always {
            script {
                pipelineUtil.cleanup()
            }
        }
    }
}
