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
        viewDirSizes()
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
    return findFiles(glob: '**/.ci.json').collect { it.path.replace('/.ci.json', '') }
}

def parseAttributes(Map params = [:]) {
    def content = params.content

    // allow user to specify attributes for this run by checking the commit message for
    // [<attribute1>,<attribute2>,...]
    def match = content =~ /\[([^\[]+)\]/

    def groups = match.collect { it[1] }
    def last = groups ? groups.last() : null

    if (last) {
        def attributes = last.split(',').collectEntries { [(it.trim()): true ] }
        return attributes
    }

    return [:]
}

def initAttributes(Map params = [:]) {
    def attrSource = params.attrSource ?: ''
    def attrIncludeGit = params.attrIncludeGit
    def adjustedSrc = attrSource.contains(',') ? attrSource : attrSource.split('\n').join(',')

    def attributes = [:]

    attributes.putAll(gitUtil.getChanges())
    attributes['pr:default'] = gitUtil.isPrToDefaultBranch()
    attributes['default'] = gitUtil.isDefaultBranch()

    if (attrIncludeGit) {
        def message = gitUtil.getCommitMessage()
        echo "INIT_ATTRIBUTES: git message: ${message}"
        attributes.putAll(parseAttributes([content: message]))
    }

    echo "INIT_ATTRIBUTES: passed in: ${adjustedSrc}"
    attributes.putAll(parseAttributes([content: "[${adjustedSrc}]"]))

    attributes['frontend'] = attributes['frontend'] || attributes.keySet().any { it.startsWith('change:frontend') }
    attributes['backend'] = attributes['backend'] || attributes.keySet().any { it.startsWith('change:backend') }

    return attributes
}

def viewDirSizes() {
    def path = util.loadScript name: 'display-size.sh'
    def output = sh(script: path, returnStdout: true).trim()
    echo "${output}"
}

def postStage(Map params = [:]) {
    def path = params.path

    viewDirSizes()
    dir(path) {
        deleteDir()
    }
}
