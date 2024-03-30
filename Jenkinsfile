node {
  git branch: 'main', url: 'https://gitea.familyhainz.de/Miggi/movie-archive.git'
  withEnv(['ROOT_IMAGE= gitea.familyhainz.de/miggi/movie']) {
    stage('Test API') {
      dir("api") {
        sh './mvnw clean test'
      }
    }
    stage('Build GUI') {
      dir("gui") {
        sh "docker build . -t $ROOT_IMAGE-gui -t $ROOT_IMAGE-gui:build-$BUILD_ID"
      }
    }
    stage ('Push and clean') {
      sh 'docker image prune -f'
      sh "docker push $ROOT_IMAGE-gui && docker push $ROOT_IMAGE-gui:build-$BUILD_ID"
    }    
  }
  withCredentials([string(credentialsId: 'webhook-moviearchive', variable: 'WEBHOOK')]) {    
    stage ('Deploy GUI') {
      sh '''
        curl -v -X POST $WEBHOOK
      '''
    }
  }
}
