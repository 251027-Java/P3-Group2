def gdata = [
    allSuccessful: true,
    attributes: [:],
]

pipeline {
    agent any

    tools {
        jdk 'java25'
        maven 'maven3'
        nodejs 'node-lts'
    }

    stages {
        stage('setup') {
            steps {
                script {
                    library "primary@${env.CHANGE_BRANCH ?: env.GIT_BRANCH}"
                    util.showEnv()
                    util.updateDisplayName()

                    gdata.attributes.putAll(gitUtil.getChanges())
                    gdata.attributes['frontend'] = gdata.changes.any { it.startsWith('frontend') }
                    gdata.attributes['backend'] = gdata.changes.any { it.startsWith('backend') }
                    gdata.attributes['pr:default'] = gitUtil.isPrToDefaultBranch()
                    gdata.attributes['default'] = gitUtil.isDefaultBranch()
                    gdata.attributes.putAll(pipelineUtil.parseAttributes([content: gitUtil.getCommitMessage()]))

                    util.printMap(gdata)
                }
            }
        }

        stage('frontend') {
            when {
                expression { gdata.attributes['frontend'] }
            }

            steps {
                script {
                    gdata.allSuccessful &= staging.execute dockerCredentialsId: 'docker-hub-cred',
                        attributes: gdata.attributes, baseDirectory: 'frontend'
                }
            }

            post {
                always {
                    script {
                        pipelineUtil.postStage()
                    }
                }
            }
        }

        stage('backend') {
            when {
                expression { gdata.attributes['backend'] }
            }

            steps {
                script {
                    gdata.allSuccessful &= staging.execute dockerCredentialsId: 'docker-hub-cred',
                        attributes: gdata.attributes, baseDirectory: 'backend'
                }
            }

            post {
                always {
                    script {
                        pipelineUtil.postStage()
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
                    error 'Some failed'
                }
            }
        }
    }
}
