def lint(Map params = [:]) {
    def path = params.path
    def name = "lint / ${checksUtil.nameFromDirectory([path: path])}"
    checksUtil.pending name: name

    dir(path) {
        try {
            sh 'mvn -B spotless:check checkstyle:check'
            checksUtil.success name: name
        } catch (err) {
            echo "${err}"
            pipelineUtil.failStage()
            checksUtil.failed name: name
        }
    }
}

def test(Map params = [:]) {
    def path = params.path
    def name = "test / ${checksUtil.nameFromDirectory([path: path])}"

    withChecks(name: name) {
        dir(path) {
            try {
                sh 'mvn -B test'
                junit '**/target/surefire-reports/TEST-*.xml'
            } catch (err) {
                echo "${err}"
                pipelineUtil.failStage()
                checksUtil.failed name: name
            }
        }
    }
}

def build(Map params = [:]) {
    def path = params.path
    def name = "build / ${checksUtil.nameFromDirectory([path: path])}"
    checksUtil.pending name: name

    dir(path) {
        try {
            sh 'mvn -B package -DskipTests'
            checksUtil.success name: name
        } catch (err) {
            echo "${err}"
            pipelineUtil.failStage()
            checksUtil.failed name: name
        }
    }
}
