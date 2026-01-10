def cleanup(Map params = [:]) {
    def tag = params.tag ?: ''
    def path = util.loadScript name: 'docker-cleanup.sh'
    sh "${path} ${tag}"
}

def image(Map params = [:]) {
    echo 'building and pushing image'
}