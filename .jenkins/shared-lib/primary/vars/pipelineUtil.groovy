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
    try {
        dockerUtil.cleanup()
    } finally {
        cleanWs()
    }
}

def getSettings(Map params = [:]) {
    def path = params.path
    def filepath = "${path}/.ci.json"

    def settings = readJSON file: filepath
    return settings
}

def getQualifyingDirs() {
    return findFiles(glob: '*/*/.ci.json').collect { it.path.replace('/.ci.json', '') }
}

def getAttributes() {
    def path = util.loadScript name: 'commit-message.sh'
    def message = sh(script: path, returnStdout: true).trim()

    // allow user to specify attributes for this run by checking the commit message for
    // [<attribute1>,<attribute2>,...]
    def match = message =~ /\[([^\[]+)\]/

    def groups = match.collect { it[1] }
    def last = groups ? groups.last() : null

    if (last) {
        def attributes = last.split(',').collectEntries { [(it.trim()): true ] }
        return attributes
    }

    return [:]
}
