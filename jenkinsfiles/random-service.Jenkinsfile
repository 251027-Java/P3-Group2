def customLint = { directory ->
    dir(directory) {
        sh 'mvn -B spotless:check checkstyle:check'
    }
}

def someRandomTest = { directory ->
    dir(directory) {
        sh 'mvn -B test'
    }
}

def customBuild = { directory ->
    dir(directory) {
        sh 'mvn -B package -DskipTests'
    }
}

def customImage = { directory ->
    dir(directory) {
        // sh 'mvn -B test'
    }
}

pipeline {
    agent any

    tools {
        jdk 'java25'
        maven 'maven3'
    }

    environment {
        TARGET_DIRECTORY = 'backend/random-service'
    }

    stages {
        stage('check run requirements') {
            steps {
                script {
                    library "primary@${env.CHANGE_BRANCH ?: env.GIT_BRANCH}"
                    util.showEnv()
                    util.updateDisplayName()

                    customTest()
                }
            }
        }

        stage('lint') {
            steps {
                script {
                    customTest()
                    backend.test name: 'asdasd', something: 'this is something'
                    customLint(env.TARGET_DIRECTORY)
                }
            }
        }

        stage('test') {
            steps {
                script {
                    backend.hello()
                    someRandomTest(env.TARGET_DIRECTORY)
                }
            }
        }

        stage('build') {
            steps {
                script {
                    customBuild(env.TARGET_DIRECTORY)
                }
            }
        }

        stage('image') {
            steps {
                script {
                    customImage(env.TARGET_DIRECTORY)
                }
            }
        }
    }

    post {
        always {
            dockerUtil.cleanup()
            // delete the workspace after to prevent large disk usage
            cleanWs()

            // script {
            //     if (!fbfm.allSuccessful) {
            //         error 'Not all stages were successful'
            //     }
            // }
        }
    }
}
