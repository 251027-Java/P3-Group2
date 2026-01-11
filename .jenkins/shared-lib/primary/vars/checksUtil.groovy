def pending(Map params = [:]) {
    def name = params.name

    publishChecks name: name, title: 'Pending', status: 'IN_PROGRESS'
}

def success(Map params = [:]) {
    def name = params.name
    def summary = params.summary ?: ''

    publishChecks name: name, title: 'Success', conclusion: 'SUCCESS', summary: limitText(summary)
}

def failed(Map params = [:]) {
    def name = params.name
    def summary = params.summary ?: ''

    publishChecks name: name, title: 'Failed', conclusion: 'FAILURE', summary: limitText(summary)
}

def skipped(Map params = [:]) {
    def name = params.name
    def summary = params.summary ?: ''

    publishChecks name: name, title: 'Skipped', conclusion: 'SKIPPED', summary: limitText(summary)
}

def limitText(Map params = [:]) {
    def end = params.end ?: true
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

def nameFromDirectory(Map params = [:]) {
    def path = params.path
    def parts = path
        .tokenize('/')
        .findAll { it && it != '.' && it != '..' }
    return parts.join(' / ')
}
