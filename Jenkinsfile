node {
  git branch: 'master', url: 'https://gitea.familyhainz.de/Miggi/movie-archive.git'
  environment {
    DOCKER_LOGIN = credentials('jenkins-docker')
  }
  withEnv(['ROOT_IMAGE= gitea.familyhainz.de/miggi/movie']) {
    /* Skipped, RAM Probelm
    stage('Build API native') {      
      dir("api") {
        sh 'export JAVA_HOME="/root/mandrel-java11-21.3.1.1-Final" && export GRAALVM_HOME="${JAVA_HOME}" && export PATH="${JAVA_HOME}/bin:${PATH}"'
        sh './mvnw clean package -Dnative -DskipTests'
        sh 'docker build -f src/main/docker/Dockerfile.native -t $ROOT_IMAGE/api .'  
      }
    }*/
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
      sh 'docker login gitea.familyhainz.de -u $DOCKER_LOGIN_USR -p $DOCKER_LOGIN_PSW'
      sh "docker tag $ROOT_IMAGE-gui $ROOT_IMAGE-gui:build-$BUILD_ID"
      sh 'docker image prune -f'
      /*push images*/
      sh "docker push $ROOT_IMAGE-auth:jdk17_build-$BUILD_ID"
      sh "docker push $ROOT_IMAGE-api:jdk17_build-$BUILD_ID"
      sh "docker push $ROOT_IMAGE-gui && docker push $ROOT_IMAGE-gui:build-$BUILD_ID"
    }
  }
}