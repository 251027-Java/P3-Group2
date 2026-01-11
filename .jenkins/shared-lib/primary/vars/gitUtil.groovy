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
    // def revspec = getRecommendedRevspec()
    def revspec = '8e8756a'

    def output = sh(script: "${path} ${revspec}", returnStdout: true).trim()

    def dirs = output.readLines()
        .collect { filepath ->
            echo "${filepath}"
            def match = filepath.trim().find(/^((?:backend|frontend)\/[^\/]+)\//) { m, dir -> dir }
            echo "| ${match} ${fileExists(match)}"
            return match && fileExists(match) ? match : null
        }
        .findAll { it } as Set

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
