def cleanup(Map params = [:]) {
    def tag = params.tag ?: ''
    def path = util.loadScript name: 'docker-cleanup.sh'
    sh "${path} ${tag}"
}

def image(Map params = [:]) {
    def path = params.path
    def repo = params.repo
    def credId = params.credId
    def latest = params.latest ?: false

    def name = util.getLastDir(path)
    def branch = gitUtil.getCurrentBranch().replaceAll('/', '-')
    def tag = "${name}-${branch}-${gitUtil.shortSha()}"

    dir(path) {
        def image = docker.build(repo)

        docker.withRegistry('', credId) {
            image.push(tag)

            if (latest) {
                image.push("${tag}-latest")
            }
        }
    }

    def cleanUpScript = util.loadScript name: 'docker-cleanup.sh'
    sh "${cleanUpScript}"
}
