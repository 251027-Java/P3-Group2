def cleanup(Map params = [:]) {
    def tag = params.tag ?: ''
    def path = util.loadScript name: 'docker-cleanup.sh'

    echo "ret path: ${path}"
    echo "cur workspace: ${env.WORKSPACE} | pwd: ${pwd()} | exists: ${fileExists(path)}"
    sh "${path} ${tag}"
}

def image(Map params = [:]) {
    def path = params.path
    def credId = params.credId
    def latest = params.latest ?: false
    def settings = params.settings

    def branch = gitUtil.getCurrentBranch().replaceAll('/', '-')
    def tag = "${settings.image.tagSeries}-${branch}-${gitUtil.shortSha()}"

    def name = "image / ${settings.image.tagSeries}"
    checksUtil.pending name: name

    def successRet = false

    dir(path) {
        try {
            def image = docker.build(settings.image.repository)

            docker.withRegistry('', credId) {
                image.push(tag)

                if (latest) {
                    image.push("${name}-latest")
                }
            }

            checksUtil.success name: name
            successRet = true
        } catch (err) {
            echo "${err}"
            pipelineUtil.failStage()
            checksUtil.failed name: name
        }
    }

    cleanup tag: tag

    return successRet
}
