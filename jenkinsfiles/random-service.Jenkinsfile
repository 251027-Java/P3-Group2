def gdata = [
    changes: [:],
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
        INT_DIR = 'backend/random-service'
        INT_DOCKER_REPO = 'minidomo/gemdeck'
    }

    stages {
        stage('setup') {
            steps {
                script {
                    library "primary@${env.CHANGE_BRANCH ?: env.GIT_BRANCH}"
                    util.showEnv()
                    util.updateDisplayName()
                    gdata.changes[env.INT_DIR] = gitUtil.hasChanges path: env.INT_DIR

                    def temp = gitUtil.getChanges()
                    echo "${temp}"

                    util.printMap(gdata)
                }
            }
        }

        // stage('lint') {
        //     when {
        //         expression { gdata.changes[env.INT_DIR] }
        //     }

        //     steps {
        //         script {
        //             def ret = backend.lint path: env.INT_DIR
        //             gdata.allSuccessful &= ret
        //         }
        //     }
        // }

        // stage('test') {
        //     when {
        //         expression { gdata.changes[env.INT_DIR] }
        //     }

        //     steps {
        //         script {
        //             def ret = backend.test path: env.INT_DIR
        //             gdata.allSuccessful &= ret
        //         }
        //     }
        // }

        // stage('build') {
        //     when {
        //         expression { gdata.changes[env.INT_DIR] }
        //     }

        //     steps {
        //         script {
        //             def ret = backend.build path: env.INT_DIR
        //             gdata.allSuccessful &= ret
        //             gdata.build[env.INT_DIR] = ret
        //         }
        //     }
        // }

        // stage('image') {
        //     when {
        //         expression { gdata.build[env.INT_DIR] }
        //     }

        //     steps {
        //         script {
        //             def ret = dockerUtil.image path: env.INT_DIR, repo: env.INT_DOCKER_REPO,
        //                 credId: 'docker-hub-cred', latest: true
        //             gdata.allSuccessful &= ret
        //         }
        //     }
        // }
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
