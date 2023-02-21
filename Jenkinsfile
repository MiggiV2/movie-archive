node {
  git branch: 'master', url: 'https://gitea.familyhainz.de/Miggi/movie-archive.git'

  environment {
    DOCKER_LOGIN = credentials('jenkins-docker')
  }

  withEnv(['ROOT_IMAGE= gitea.familyhainz.de/miggi/movie']) {
    docker.withRegistry('https://gitea.familyhainz.de', 'credentials-id') {

      stage('Build API') {
        dir("api") {
          sh 'ls -la'
          sh './mvnw clean package -DskipTests'
          sh "docker build -f src/main/docker/Dockerfile.jvm -t $ROOT_IMAGE-api:jdk17_build-$BUILD_ID ."
        }
      }

      stage('Build Auth') {
        dir("auth") {
          sh 'ls -la'
          sh './mvnw clean package -DskipTests'
          sh "docker build -f src/main/docker/Dockerfile.jvm -t $ROOT_IMAGE-auth:jdk17_build-$BUILD_ID ."
        }
      }

      stage('Build GUI') {
        dir("gui") {
          sh "docker build . -t $ROOT_IMAGE-gui"
        }
      }

      stage ('Manage tags & remove untagged') {
        sh "docker tag $ROOT_IMAGE-gui $ROOT_IMAGE-gui:build-$BUILD_ID"
        sh "docker push $ROOT_IMAGE-auth:jdk17_build-$BUILD_ID"
        sh "docker push $ROOT_IMAGE-api:jdk17_build-$BUILD_ID"
        sh "docker push $ROOT_IMAGE-gui && docker push $ROOT_IMAGE-gui:build-$BUILD_ID"
      }
    }
  }
}