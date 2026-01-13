def cleanup(Map params = [:]) {
    def tag = params.tag ?: ''
    def path = util.loadScript name: 'docker-cleanup.sh'
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

    try {
        docker.withRegistry('', credId) {
            def flags = [
                "--repo \"${settings.image.repository}\"",
                "--series \"${settings.image.tagSeries}\"",
                "--meta \"${branch}-${gitUtil.shortSha()}\"",
            ]

            if (!settings.image.platform.single) {
                def platforms = settings.image.platform.multi.join(',')
                flags.add("--platforms \"${platforms}\"")
            }

            if (latest) {
                flags.add('--latest')
            }

            def imageScript = util.loadScript name: 'docker-image.sh'
            dir(path) {
                sh "${imageScript} ${flags.join(' ')}"
            }
        }

        checksUtil.success name: name
        successRet = true
    } catch (err) {
        echo "${err}"
        pipelineUtil.failStage()
        checksUtil.failed name: name
    }

    cleanup tag: tag

    return successRet
}
