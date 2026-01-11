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

def runPipeline(Map params = [:]) {
    def dockerCredentialsId = params.dockerCredentialsId
    def attributes = params.attributes
    def allSuccessful = true

    pipelineUtil.getQualifyingDirs().each { path ->
        if (!path.startsWith('backend')) { return }

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
    }

    return allSuccessful
}
