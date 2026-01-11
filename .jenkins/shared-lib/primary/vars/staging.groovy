def lint(Map params = [:]) {
    def path = params.path
    def settings = params.settings

    def name = "lint / ${checksUtil.nameFromDirectory([path: path])}"
    checksUtil.pending name: name

    def successRet = false
    def summary = """
```sh
${settings.lint.command}
```
""".trim()

    dir(path) {
        try {
            sh "${settings.lint.command}"
            checksUtil.success name: name, summary: summary
            successRet = true
        } catch (err) {
            echo "${err}"
            pipelineUtil.failStage()
            checksUtil.failed name: name, summary: summary
        }
    }

    return successRet
}

// assumes java. not expecting tests for frontend
def test(Map params = [:]) {
    def path = params.path
    def settings = params.settings

    def name = "test / ${checksUtil.nameFromDirectory([path: path])}"
    def successRet = false
    def summary = """
```sh
${settings.lint.command}
```
""".trim()

    withChecks(name: name) {
        dir(path) {
            try {
                sh "${settings.test.command}"
                junit '**/target/surefire-reports/TEST-*.xml'
                successRet = true
            } catch (err) {
                echo "${err}"
                pipelineUtil.failStage()
                checksUtil.failed name: name, summary: summary
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
    def summary = """
```sh
${settings.lint.command}
```
""".trim()

    dir(path) {
        try {
            sh "${settings.build.command}"
            checksUtil.success name: name, summary: summary
            successRet = true
        } catch (err) {
            echo "${err}"
            pipelineUtil.failStage()
            checksUtil.failed name: name, summary: summary
        }
    }

    return successRet
}

def execute(Map params = [:]) {
    def dockerCredentialsId = params.dockerCredentialsId
    def attributes = params.attributes
    def baseDirectory = params.baseDirectory
    def allSuccessful = true

    pipelineUtil.getQualifyingDirs().each { path ->
        if (!path.startsWith(baseDirectory)) { return }
        allSuccessful &= executeDir dockerCredentialsId: dockerCredentialsId, attributes: attributes
    }

    return allSuccessful
}

def executeDir(Map params = [:]) {
    def dockerCredentialsId = params.dockerCredentialsId
    def attributes = params.attributes
    def allSuccessful = true

    def shouldRun = (
        !attributes['skip']
        && (
            attributes['run']
            || (
                (attributes['pr:default'] || attributes['default'])
                && (attributes["change:${path}".toString()])
            )
        )
    )

    def settings = pipelineUtil.getSettings path: path

    if (settings.lint.enabled && shouldRun) {
        stage("lint ${path}") {
            def success = lint path: path, settings: settings
            allSuccessful &= success
        }
    }

    if (settings.test.enabled && shouldRun) {
        stage("test ${path}") {
            def success = test path: path, settings: settings
            allSuccessful &= success
        }
    }

    if (settings.build.enabled && shouldRun) {
        stage("build ${path}") {
            def success = build path: path, settings: settings
            allSuccessful &= success
            attributes["build:${path}".toString()] = success
        }
    }

    def shouldBuildImage = (
        attributes["build:${path}".toString()]
        || attributes['imageall']
        || attributes["image:${path}".toString()]
    )

    if (settings.image.enabled && shouldBuildImage) {
        stage("image ${path}") {
            def success = dockerUtil.image path: path, settings: settings,
                credId: dockerCredentialsId, latest: attributes['default']
            allSuccessful &= success
        }
    }

    return allSuccessful
}
