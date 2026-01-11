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

    parameters {
        string description: 'Comma-separated attributes.', name: 'INTL_ATTRIBUTES_LINE', trim: true
        text description: 'Attributes separated by a newline. Overrides `INTL_ATTRIBUTES_LINE` if both are provided.',
            name: 'INTL_ATTRIBUTES'
        booleanParam description: [
            'If enabled, uses the git commit message to obtain attributes and merges with any provided attributes',
            'through params. The order in which attributes are inserted: Git, Parameters.'
        ].join(' '),
            name: 'INTL_ATTRIBUTES_INCLUDE_GIT'
    }

    stages {
        stage('setup') {
            steps {
                script {
                    library "primary@${env.CHANGE_BRANCH ?: env.GIT_BRANCH}"
                    util.showEnv()
                    util.updateDisplayName()

                    gdata.attributes.putAll(pipelineUtil.initAttributes())

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
