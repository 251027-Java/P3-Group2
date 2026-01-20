def isDefaultBranch() {
    return env.BRANCH_IS_PRIMARY == 'true'
}

def isPrCreated() {
    return currentBuild.changeSets.size() == 0
}

def getDefaultBranchName() {
    def path = util.loadScript name: 'default-branch.sh'
    def name = sh(script: path, returnStdout: true).trim()
    return name
}

def isPrToDefaultBranch() {
    return env.CHANGE_TARGET == getDefaultBranchName()
}

def getRecommendedRevspec(isPrToDefaultAttribute) {
    echo "isPRTODEFAULT: ${isPrToDefaultAttribute}"
    def prDefault = isPrToDefaultAttribute || isPrToDefaultBranch()
    def prCreated = isPrCreated()
    def prevResult = "${currentBuild.previousBuild?.result}".toString()

    echo "PrDefault: ${prDefault} | PrCreate: ${prCreated} | PrevResult: ${prevResult}"

    if (prDefault && (prCreated || prevResult == 'FAILURE')) {
        def path = util.loadScript name: 'fetch-latest.sh'
        def name = getDefaultBranchName()

        sh "${path} ${name}"

        return 'FETCH_HEAD'
    }

    return prCreated ? 'HEAD~1' : "HEAD~${currentBuild.changeSets.first().items.size()}"
}

def getChanges(isPrToDefaultAttribute) {
    def path = util.loadScript name: 'git-changes.sh'
    echo "GETCHANGES_ PR TO DEFAULT: ${isPrToDefaultAttribute}"
    def revspec = getRecommendedRevspec(isPrToDefaultAttribute)

    def output = sh(script: "${path} ${revspec}", returnStdout: true).trim()

    def dirs = output.readLines()
        .collect { filepath ->
            def match = filepath.trim() =~ /^((?:backend|frontend)\/[^\/]+)\//
            def ret = null
            def inter = null

            if (match.find()) {
                inter = match.group(1)

                // https://stackoverflow.com/a/68937527
                // get rid of the matcher object because it's not serializable
                // otherwise, error thrown when fileExists is called
                match = null

                // directory could've been deleted so ensure it exists
                // also only include directories that have .ci.json
                if (fileExists("${inter}/.ci.json")) {
                    ret = inter
                }
            }

            echo "    GET_CHANGES: ${filepath} | matched: ${inter} | final: ${ret}"
            return ret
        }
        .findAll { it }
        .collectEntries { [("change:${it}".toString()): true ] }

    return dirs
}

def hasChanges(Map params = [:]) {
    def gitPath = params.path

    def path = util.loadScript name: 'changes-count.sh'
    def revspec = getRecommendedRevspec()

    def changes = sh(script: "${path} ${revspec} '^${gitPath}'", returnStdout: true).trim()
    return changes != '0'
}

def shortSha() {
    return env.GIT_COMMIT.take(7)
}

def getCurrentBranch() {
    return env.CHANGE_BRANCH ?: env.GIT_BRANCH
}

def getCommitMessage() {
    def path = util.loadScript name: 'commit-message.sh'
    def message = sh(script: path, returnStdout: true).trim()
    return message
}
