def markStageFailure = { ->
    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
        sh 'exit 1'
    }
    fbfm.allSuccessful = false
}

def customLint = { directory ->
    dir(directory) {
        sh 'mvn -B spotless:check checkstyle:check'
    }
}

def customTest = { directory ->
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

        stage('lint') {
            steps {
                script {
                    customLint(env.TARGET_DIRECTORY)
                }
            }
        }

        stage('test') {
            steps {
                script {
                    customTest(env.TARGET_DIRECTORY)
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
            sh '.jenkins/scripts/docker-cleanup.sh'
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
