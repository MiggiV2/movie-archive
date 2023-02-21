node {
  git branch: 'master', url: 'https://gitea.familyhainz.de/Miggi/movie-archive.git'

  withEnv(['ROOT_IMAGE=gitea.familyhainz.de/miggi/movie']) {
      stage('Build API') {
        dir("api") {
          sh 'ls -la'
          sh './mvnw clean package -DskipTests'

          docker.withRegistry('https://gitea.familyhainz.de', 'jenkins-docker') {
            def apiImage = docker.build("$ROOT_IMAGE-api:jdk17-build-$BUILD_ID", "-f src/main/docker/Dockerfile.jvm .")
            apiImage.push()
          }
        }
      }

      stage('Build Auth') {
        dir("auth") {
          sh 'ls -la'
          sh './mvnw clean package -DskipTests'

          def authImage = docker.build("$ROOT_IMAGE-auth:jdk17-build-$BUILD_ID", "-f src/main/docker/Dockerfile.jvm .")
          authImage.push()
        }
      }

      stage('Build GUI') {
        dir("gui") {
          def guiImage = docker.build("$ROOT_IMAGE-gui")
          guiImage.push()
        }
      }
  }
}