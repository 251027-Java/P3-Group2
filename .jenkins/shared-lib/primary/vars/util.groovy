def updateDisplayName() {
    currentBuild.displayName = "${currentBuild.displayName} ${gitUtil.shortSha()}"
}

def showEnv() {
    def path = loadScript name: 'print-env.sh'
    sh "${path}"
}

def printMap(Map map) {
    echo "${groovy.json.JsonOutput.prettyPrint(groovy.json.JsonOutput.toJson(map))}"
}

def loadScript(Map params = [:]) {
    def name = params.name
    def path = "${env.WORKSPACE}/${name}"

    if (!fileExists(path)) {
        writeFile file: "${name}", text: libraryResource("${name}")
        sh "chmod +x ${path}"
    }

    return path
}

def getLastDir(Map params = [:]) {
    def path = params.path
    return path.replaceAll('/+$', '').tokenize('/').last()
}
