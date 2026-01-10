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

    return isPrCreated() ? 'HEAD~1' : "HEAD~${changeSet.items.size()}"
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

def checkPending(Map params = [:]) {
    def name = params.name

    publishChecks name: name, title: 'Pending', status: 'IN_PROGRESS'
}

def limitText(Map params = [:]) {
    def end = params.end
    def text = params.text

    // https://github.com/jenkinsci/junit-plugin/blob/6c6699fb25df1b7bae005581d9af2ed698c47a4c/src/main/java/io/jenkins/plugins/junit/checks/JUnitChecksPublisher.java#L72
    // stay within limits of check api for summaries
    def MAX_MSG_SIZE_TO_CHECKS_API = 65535
    def limit = MAX_MSG_SIZE_TO_CHECKS_API - 1024

    if (end) {
        return text.substring(Math.max(0, text.length() - limit))
    }

    return text.take(limit)
}
