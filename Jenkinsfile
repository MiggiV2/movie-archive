node {
  git branch: 'master', url: 'https://gitea.familyhainz.de/Miggi/movie-archive.git'
  withEnv(['ROOT_IMAGE= gitea.familyhainz.de/miggi/movie-']) {
    stage('Build API') {
      /* Skipped, RAM Probelm
      dir("api") {
        sh 'export JAVA_HOME="/root/mandrel-java11-21.3.1.1-Final" && export GRAALVM_HOME="${JAVA_HOME}" && export PATH="${JAVA_HOME}/bin:${PATH}"'
        sh './mvnw clean package -Dnative -DskipTests'
        sh 'docker build -f src/main/docker/Dockerfile.native -t $ROOT_IMAGE/api .'  
      }
      */
      dir("api") {
        sh 'ls -la'
        sh './mvnw clean package -DskipTests'
        sh 'docker build -f src/main/docker/Dockerfile.jvm -t $ROOT_IMAGE-api .'
      }    
    }
    stage('Build GUI') {
      dir("gui") {
        sh 'docker build . -t $ROOT_IMAGE-gui'   
      }         
    }
    stage ('Manage tags & remove untagged') {
      sh "docker tag $ROOT_IMAGE-api $ROOT_IMAGE-api:build-$BUILD_ID"
      sh "docker tag $ROOT_IMAGE-gui $ROOT_IMAGE-gui:build-$BUILD_ID"
      sh 'docker image prune -f'
    }
  }
}