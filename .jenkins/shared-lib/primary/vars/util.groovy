import groovy.json.JsonOutput

def updateDisplayName() {
    currentBuild.displayName = "${currentBuild.displayName} ${gitUtil.shortSha()}"
}

def showEnv() {
    def path = loadScript name: 'print-env.sh'
    sh "${path}"
}

def printMap(Map map) {
    echo "${JsonOutput.prettyPrint(JsonOutput.toJson(map))}"
}

def loadScript(Map params = [:]) {
    def name = params.name
    def path = "./${name}"
    def safeName = path.replaceAll(/[\/@]/, '-')

    echo "load script path: ${path}"

    if (!fileExists(path)) {
        writeFile file: name, text: libraryResource(name)
        sh "chmod +x ${path}"
        stash includes: name, name: safeName
    }

    unstash name: safeName

    sh 'ls -la'

    return path
}

def getLastDir(Map params = [:]) {
    def path = params.path
    return path.replaceAll('/+$', '').tokenize('/').last()
}
