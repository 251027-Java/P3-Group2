def javaLint(Map params = [:]) {
    def path = params.path

    dir(path) {
        sh 'mvn -B spotless:check checkstyle:check'
    }
}

def javaTest(Map params = [:]) {
    def path = params.path

    dir(path) {
        sh 'mvn -B test'
    }
}

def javaBuild(Map params = [:]) {
    def path = params.path

    dir(path) {
        sh 'mvn -B package -DskipTests'
    }
}
