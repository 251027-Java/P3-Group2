def lint(Map params = [:]) {
    def path = params.path
    def name = "lint / ${checksUtil.nameFromDirectory([path: path])}"
    checksUtil.pending name: name

    def successRet = false

    dir(path) {
        try {
            sh 'mvn -B spotless:check checkstyle:check'
            checksUtil.success name: name
            successRet = true
        } catch (err) {
            echo "${err}"
            pipelineUtil.failStage()
            checksUtil.failed name: name
        }
    }

    return successRet
}

def test(Map params = [:]) {
    def path = params.path
    def name = "test / ${checksUtil.nameFromDirectory([path: path])}"
    def successRet = false

    withChecks(name: name) {
        dir(path) {
            try {
                sh 'mvn -B test'
                junit '**/target/surefire-reports/TEST-*.xml'
                successRet = true
            } catch (err) {
                echo "${err}"
                pipelineUtil.failStage()
                checksUtil.failed name: name
            }
        }
    }

    return successRet
}

def build(Map params = [:]) {
    def path = params.path
    def name = "build / ${checksUtil.nameFromDirectory([path: path])}"
    checksUtil.pending name: name

    def successRet = false

    dir(path) {
        try {
            sh 'mvn -B package -DskipTests'
            checksUtil.success name: name
            successRet = true
        } catch (err) {
            echo "${err}"
            pipelineUtil.failStage()
            checksUtil.failed name: name
        }
    }

    return successRet
}
