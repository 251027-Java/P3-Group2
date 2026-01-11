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

def getRecommendedRevspec() {
    if (isPrToDefaultBranch() && (isPrCreated() || currentBuild.previousBuild?.result == 'FAILURE')) {
        def path = util.loadScript name: 'fetch-latest.sh'
        def name = getDefaultBranchName()

        sh "${path} ${name}"

        return "origin/${name}"
    }

    return isPrCreated() ? 'HEAD~1' : "HEAD~${currentBuild.changeSets.first().items.size()}"
}

def getChanges() {
    def path = util.loadScript name: 'git-changes.sh'
    def revspec = getRecommendedRevspec()

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
        .collectEntries { [(it): true ] }

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
