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
        string name: 'INTL_ATTRIBUTES_LINE', description: 'Comma-separated attributes.', trim: true
        text name: 'INTL_ATTRIBUTES', description: 'Attributes separated by a newline. Overrides `INTL_ATTRIBUTES_LINE` if both are provided.'
        booleanParam name: 'INTL_ATTRIBUTES_INCLUDE_GIT', defaultValue: true, description: 'If enabled, uses the git commit message to obtain attributes and merges with any provided attributes through params. The order in which attributes are inserted: Git, Parameters.'
    }

    stages {
        stage('setup') {
            steps {
                script {
                    library "primary@${env.CHANGE_BRANCH ?: env.GIT_BRANCH}"
                    util.showEnv()
                    util.updateDisplayName()

                    gdata.attributes.putAll(pipelineUtil.initAttributes([
                        attrSource: params.INTL_ATTRIBUTES ?: params.INTL_ATTRIBUTES_LINE,
                        attrIncludeGit: params.INTL_ATTRIBUTES_INCLUDE_GIT
                    ]))

                    util.printMap(params)
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
                        pipelineUtil.postStage path: 'backend'
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
