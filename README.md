# spring-boot-container-cicd
## Introduction
CI/CD stands as a crucial concept in modern software development, dismantling barriers between development and operations teams. It fosters collaborative responsibilities, bringing synergy to the goals of both development and operations. In this article, I will guide you through a brief POC on integrating Git and Jenkins. While this article may not encompass an exhaustive list of other DevOps tools like Prometheus, Grafana, Jira, etc., it aims to provide a foundational understanding of Git-Jenkins integration

## Scenario
<p align="center">
  <img src="images/github-jenkins-scenario.jpg" alt="image description" width="550" height="400">
</p>
As depicted in the illustration above, the steps are outlined below:

`1.`Create a Spring Boot application and commit the source code to GitHub.\
`2.`Upon a new commit, the GitHub webhook will initiate a trigger to Jenkins.\
`3.`Jenkins, upon receiving the trigger, will pull the latest source code from GitHub.\
`4.1.` Execute the Maven command "mvn clean install" to build the JAR artifact.\
`4.2.` Utilize a predefined Dockerfile to build a Docker image. This process requires the JAR artifacts generated in step 4.1 to be included in the image packaging.\
`5.`Run container from Docker image created in step 4.2 within the Docker engine.\
`6.`The anticipated outcome is the ability to access Swagger UI, indicating the successful functioning of our application!\

## Prerequisite for Host 
1.Github account\
3.Docker engine and install Docker Compose 

## Docker Compose 
This POC's going to setup everything based on Docker Compose, making it easy to start up all the components and integrate all of them to collaborate with each other.\
The components that will start up with Docker Compose are the following:
>1.docker:dind\
>2.jenkins\
>3.ngrok

<p align="center">
  <img src="images/compose-component.jpg" alt="image description" width="750" height="300">
</p>

### Docker in Docker (DinD) 
In an environment where all components are containerized, it is necessary to provide Jenkins with a Docker engine to facilitate image building and container execution.\
In this scenario, the use of `docker:dind` is crucial. The `docker:dind` container encompasses both clients, daemons, and a registry. Consequently, after Jenkins completes the image building process, it pushes the images to the `docker:dind` container and initiates the application as a container. This approach is commonly referred to as `Docker in Docker`.

DinD configuration, there are a few more things that we should focus on, including the following:
 ```yaml
  docker:
    image: docker:dind
    container_name: docker
    restart: always
    privileged: true
    networks:
      - docker-daemon
    environment:
      - DOCKER_TLS_CERTDIR=/certs
    volumes:
      - ../cert:/certs/server
    ports:
      - "2376:2376"
      - "8081:8081"
 ```
> `../cert:/certs/server` Export cerificate for setting up Docker to use TLS for secure communication between the Docker client and the Docker daemon.\
> `DOCKER_TLS_CERTDIR` ndicates that Docker should look for TLS certificates in the /certs directory.

Refer document :\
[How To Run Docker in Docker Container](https://devopscube.com/run-docker-in-docker/)\
[Protect the Docker daemon socket](https://docs.docker.com/engine/security/protect-access/#use-tls-https-to-protect-the-docker-daemon-socket)\
[Docker-in-Docker with TLS enabled ](https://docs.gitlab.com/ee/ci/docker/using_docker_build.html#docker-in-docker-with-tls-enabled-in-the-docker-executor)
### Jenkins custom images
When utilizing Jenkins in a container, relying on the official `jenkins/jenkins` image may not be sufficient. This is because the Jenkins container needs to install a `Docker client` to connect to `docker:dind` for tasks like pushing application images and running application containers. As a result, it becomes necessary to create custom Jenkins images that include the `Docker client`. The Docker client will then be available within the Jenkins container, enabling seamless interaction with Docker functionalities.\

To build custom Jenkins images with the Docker client, do the following:
 ```Dockerfile
FROM jenkins/jenkins:2.426.1-jdk17
USER root
RUN apt-get update && apt-get install -y lsb-release
RUN curl -fsSLo /usr/share/keyrings/docker-archive-keyring.asc \
  https://download.docker.com/linux/debian/gpg
RUN echo "deb [arch=$(dpkg --print-architecture) \
  signed-by=/usr/share/keyrings/docker-archive-keyring.asc] \
  https://download.docker.com/linux/debian \
  $(lsb_release -cs) stable" > /etc/apt/sources.list.d/docker.list
RUN apt-get update && apt-get install -y docker-ce-cli
USER jenkins
RUN jenkins-plugin-cli --plugins "blueocean docker-workflow"
 ```
Then run the Docker command to build images from the script above.
 ```docker
docker build -f ./path/of/Dockerfile -t name-of-image .
 ```
Jenkins in docker compose's going to configure at following:
 ```yaml
  jenkins:
    image: myjenkins-blueocean:2.426.1-1
    container_name: jenkins
    restart: on-failure
    networks:
      - docker-daemon
    environment:
      - DOCKER_HOST=tcp://docker:2376
      - DOCKER_CERT_PATH=/certs/client # Update path to client certificate
      - DOCKER_TLS_VERIFY=1
    volumes:
      - ../cert:/certs/client:ro # Update volume path
      - ../jinkens_home:/var/jenkins_home
    ports:
      - "8080:8080"
      - "50000:50000"
    depends_on:
      - docker
 ```
> `DOCKER_HOST=tcp://docker:2376` Specifies the Docker daemon's host and port.\
> `DOCKER_CERT_PATH=/certs/client` Defines the path to the client certificate within the container.\
> `DOCKER_TLS_VERIFY=1` Enables TLS verification for Docker.\
> `../cert:/certs/client:ro` Mounts the local TLS certificate directory into the container (../cert, which is the certificate that exports from the DinD configuration above)

Refer document : [Jenkins Docker](https://www.jenkins.io/doc/book/installing/docker/)


### Ngrok
In our specific scenario, the triggering of Jenkins by GitHub upon a new commit requires the setup of a webhook on GitHub, with the inclusion of a callback URL. However, given that Jenkins is running as a container locally, GitHub cannot directly reach our local environment.\
To bridge this gap between the internet and our local setup, `Ngrok` serves as a proxy, facilitating the communication needed for GitHub to interact with Jenkins in our local containerized environment.

To configure Ngrok you need to do at the following\
1.Create Ngrok account Login -> Getting Started -> Your Authtoken\
2.Configure the ngrok Agent Configuration File

 ```yaml
version: '2'
authtoken: <-- Your Authtoken -->
log_level: debug
log_format: logfmt
log: /var/log/ngrok.log
connect_timeout: 30s
tunnels:
  github-webhook:
    addr: host.docker.internal:8080
    proto: http
 ```
Refer document :\
[Creating webhooks](https://docs.github.com/en/webhooks/using-webhooks/creating-webhooks)\
[Testing webhooks](https://docs.github.com/en/webhooks/testing-and-troubleshooting-webhooks/testing-webhooks)\
[Using ngrok with Docker Compose](https://ngrok.com/docs/using-ngrok-with/docker/)\
[Ngrok Agent Configuration](https://ngrok.com/docs/agent/config/#full-example)



>`host.docker.internal` is a special DNS name provided by Docker for containers to communicate with services running on the host machine. It resolves to the internal IP address of the host from within a Docker container.\

Refer document : [Connect from a container to a service on the host](https://docs.docker.com/desktop/networking/#i-want-to-connect-from-a-container-to-a-service-on-the-host)


## How to run this project

1. Clone project
2. Build Jenkins custom images
 ```sh
docker build -f ./path/of/Dockerfile -t name-of-image .
 ```
3. Login to Ngrok copy authtoken to ./ngrok_home -> ngrok.yml -> authtoken

4. Start docker compose
 ```sh
docker compose -f ./container/docker-compose/cicd-container.yaml up -d
 ```
5. Access to Jenkins dashboard
 ```sh
http://localhost:8080/
 ```
6. Install Github plugin  Manage Jenkins -> Plugins -> Available plugin -> github
```sh
https://plugins.jenkins.io/github/
```
7. Select This project is parameterized -> String Parameter\
Name: DOCKER_IMAGE_VERSION\
Default Value: 0.0\
Description : Docker image version

8. Create pipeline New Item -> Pipline -> Pipeline Script -> then provide the script below
 ```yaml
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
                    sh "docker build -f ./container/docker-file/spring-boot-app -t app/spring-boot-cicd:${params.DOCKER_IMAGE_VERSION} ."
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

9. Select Build Triggers -> GitHub hook trigger for GITScm polling

10. Access to Ngrok portal and copy tunnel URLs
```sh
http://localhost:4040/inspect/http
 ```
11. Login to Github -> your repository -> setting 
