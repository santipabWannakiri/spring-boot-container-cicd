# spring-boot-container-cicd
## Introduction
CI/CD stands as a crucial concept in modern software development, dismantling barriers between development and operations teams. It fosters collaborative responsibilities, bringing synergy to the goals of both development and operations. In this article, I will guide you through a brief POC on integrating Git and Jenkins. While this article may not encompass an exhaustive list of other DevOps tools like Prometheus, Grafana, Jira, etc., it aims to provide a foundational understanding of Git-Jenkins integration

## Scenario
<p align="center">
  <img src="images/github-jenkins-scenario.jpg" alt="image description" width="550" height="400">
</p>

[Creating webhooks](https://docs.github.com/en/webhooks/using-webhooks/creating-webhooks)
[Testing webhooks](https://docs.github.com/en/webhooks/testing-and-troubleshooting-webhooks/testing-webhooks)

[How To Run Docker in Docker Container](https://devopscube.com/run-docker-in-docker/)


[Jenkins Docker](https://www.jenkins.io/doc/book/installing/docker/)


[Protect the Docker daemon socket](https://docs.docker.com/engine/security/protect-access/)

[I want to connect from a container to a service on the host](https://docs.docker.com/desktop/networking/#i-want-to-connect-from-a-container-to-a-service-on-the-host)

test commit1 01

Remote Docker Daemon

 ```
properties([parameters([string(defaultValue: '0.0', description: 'Docker image version', name: 'DOCKER_IMAGE_VERSION')])])

pipeline {
    agent any
    
    tools {
        maven 'maven01'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/santipabWannakiri/spring-boot-container-cicd.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    echo 'Building with Maven:'
                    withMaven(maven: 'maven01') {
                        sh 'mvn clean install'
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    echo "Building Docker image with version: ${params.DOCKER_IMAGE_VERSION}"
                    sh "docker build -f ./container/dockerFile/springAppContainer -t app/spring-boot-cicd:${params.DOCKER_IMAGE_VERSION} ."
  
                }
            }
        }
        
                stage('Start container') {
            steps {
                script {
                    echo "Starting container: app/spring-boot-cicd:${params.DOCKER_IMAGE_VERSION}"
                    sh "docker run -p 8081:8081 -d app/spring-boot-cicd:${params.DOCKER_IMAGE_VERSION}"
  
                }
            }
        }
        
    }

    post {
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}

 ```
