def failStage(Map params = [:]) {
    markStage([
        code: params.code ?: 1,
        result: 'FAILURE'
    ])
}

def abortStage(Map params = [:]) {
    markStage([
        code: params.code ?: 1,
        result: 'ABORTED'
    ])
}

private def markStage(Map params = [:]) {
    def code = params.code ?: 1
    def result = params.result ?: 'FAILURE'

    catchError(buildResult: 'SUCCESS', stageResult: result) {
        sh "exit ${code}"
    }
}

def cleanup() {
    dockerUtil.cleanup()
    cleanWs()
}

def getSettings(Map params = [:]) {
    def path = params.path
    def filepath = "${path}/.ci.json"

    def settings = readJSON file: filepath
    return settings
}
