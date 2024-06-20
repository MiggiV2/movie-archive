node {
  git branch: 'main', url: 'https://gitea.familyhainz.de/Miggi/movie-archive.git'
  withEnv(['ROOT_IMAGE=code.mymiggi.de/miggi/movie']) {
    stage('Test API') {
      dir("api") {
        sh './mvnw test'
      }
    }
    stage('Build GUI') {
      withCredentials([file(credentialsId: 'movie-archive-env-file', variable: 'CONFIG')]) {
        dir("gui") {
          sh 'cp $CONFIG .env'
          sh "docker build . -t $ROOT_IMAGE-gui -t $ROOT_IMAGE-gui:build-$BUILD_ID"
          sh "rm .env"
        }
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
