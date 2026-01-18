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

def test(Map params = [:]) {
    def path = params.path
    def settings = params.settings

    def name = "test / ${checksUtil.nameFromDirectory([path: path])}"
    checksUtil.pending name: name

    def successRet = false
    def summary = """
```sh
${settings.test.command}
```
""".trim()

    dir(path) {
        try {
            sh "${settings.test.command}"
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

def build(Map params = [:]) {
    def path = params.path
    def settings = params.settings

    def name = "build / ${checksUtil.nameFromDirectory([path: path])}"
    checksUtil.pending name: name

    def successRet = false
    def summary = """
```sh
${settings.build.command}
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

def dependencies(Map params = [:]) {
    def path = params.path
    def settings = params.settings

    def name = "dependencies / ${checksUtil.nameFromDirectory([path: path])}"
    checksUtil.pending name: name

    def successRet = false
    def summary = """
```sh
${settings.dependencies.command}
```
""".trim()

    dir(path) {
        try {
            sh "${settings.dependencies.command}"
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
        allSuccessful &= executeDir dockerCredentialsId: dockerCredentialsId, attributes: attributes, path: path
    }

    return allSuccessful
}

def executeDir(Map params = [:]) {
    def dockerCredentialsId = params.dockerCredentialsId
    def attributes = params.attributes
    def path = params.path
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

    // read the .ci.json file for the service
    def settings = pipelineUtil.getSettings path: path

    if (settings.dependencies?.enabled && shouldRun) {
        stage("dependencies ${path}") {
            def success = dependencies path: path, settings: settings
            allSuccessful &= success
        }
    }

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

    def pushLatest = attributes['default'] || attributes['latest']

    if (settings.image.enabled && shouldBuildImage) {
        stage("image ${path}") {
            def success = dockerUtil.image path: path, settings: settings,
                credId: dockerCredentialsId, latest: pushLatest
            allSuccessful &= success
        }
    }

    return allSuccessful
}
