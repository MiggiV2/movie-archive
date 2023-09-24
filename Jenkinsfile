node {
  git branch: 'main', url: 'https://gitea.familyhainz.de/Miggi/movie-archive.git'
  withEnv(['ROOT_IMAGE= gitea.familyhainz.de/miggi/movie']) {
    /*
    stage('Build API') {
      dir("api") {
        sh 'ls -la'
        sh './mvnw clean package -Dquarkus.container-image.push=false'
        sh "docker build -f src/main/docker/Dockerfile.jvm -t $ROOT_IMAGE-api:jdk17_build-$BUILD_ID ."
      }
    }
    */
    stage('Build GUI') {
      dir("gui") {
        sh "docker build . -t $ROOT_IMAGE-gui"
      }
    }
    stage ('Manage tags & remove untagged') {
      sh "docker tag $ROOT_IMAGE-gui $ROOT_IMAGE-gui:build-$BUILD_ID"
      sh 'docker image prune -f'
      /*push images*/
      sh "docker push $ROOT_IMAGE-api:jdk17_build-$BUILD_ID"
      sh "docker push $ROOT_IMAGE-gui && docker push $ROOT_IMAGE-gui:build-$BUILD_ID"
    }
  }
}
