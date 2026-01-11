def lint(Map params = [:]) {
    def path = params.path
    def settings = params.settings

    def name = "lint / ${checksUtil.nameFromDirectory([path: path])}"
    checksUtil.pending name: name

    def successRet = false

    dir(path) {
        try {
            sh "${settings.lint.command}"
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
    def settings = params.settings

    def name = "test / ${checksUtil.nameFromDirectory([path: path])}"
    def successRet = false

    withChecks(name: name) {
        dir(path) {
            try {
                sh "${settings.test.command}"
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
    def settings = params.settings

    def name = "build / ${checksUtil.nameFromDirectory([path: path])}"
    checksUtil.pending name: name

    def successRet = false

    dir(path) {
        try {
            sh "${settings.build.command}"
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
