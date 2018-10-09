// Variable to change
// AWS credentials ID: credentials configured on Jenkins with AWS plugin
JENKINS_AWS_CREDENTIALS_NAME = "aws_credentials"
// Kubernetes: Namespace
K8s_namespace = "flask"
// Kubernetes: Name of the key
K8s_keyname = "regsecretecr"
// Kubernetes: Docker email
K8s_docker_email = "admin@email.com"
// AWS ECR : Repository email
ECR_repository_url = "https://??????????.dkr.ecr.ap-southeast-1.amazonaws.com"

properties([pipelineTriggers([cron('* */6 * * *')])])

def label = "worker-${UUID.randomUUID().toString()}"

podTemplate(label: label, containers: [
//  containerTemplate(name: 'docker', image: 'docker:latest', command: 'cat', ttyEnabled: true),
  containerTemplate(name: 'aws', image: 'mesosphere/aws-cli', command: 'cat', ttyEnabled: true),
  containerTemplate(name: 'kubectl', image: 'roffe/kubectl:latest', ttyEnabled: true, command: 'cat')
],
volumes: [
  hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')
]){


  node(label) {



    // Stage: Connect AWS - Get Password
    // Connect to AWS using AWS credentials of Jenkins AWS Plugin
    // Collect the password and assign a variable to store it: var: AWS_password
    stage('Connect AWS - Get Password'){
      // Use AWS credentials stored in AWS_key
      withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: "${JENKINS_AWS_CREDENTIALS_NAME}", secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]){
        // Use of AWS container
        container('aws'){
          // Login to AWS and store the password to password.txt
          sh 'aws ecr get-login --no-include-email --region ap-southeast-1 | cut -d " " -s -f6 > password.txt'
          script {
            // trim removes leading and trailing whitespace from the string
            // collect
            AWS_password = readFile('password.txt').trim()
          }
        }
      }
    }

    // Stage: Kubectl create secret key
    // With the password generated earlier, create secret key into kubernetes
    stage ('Kubectl create secret key'){
      container('kubectl'){
        try {
          sh "kubectl -n ${K8s_namespace} delete secret ${K8s_keyname}"
        } catch (Exception e) {}
        sh "kubectl -n ${K8s_namespace} create secret docker-registry ${K8s_keyname} \
            --docker-server=${ECR_repository_url} \
            --docker-username=AWS \
            --docker-password=${AWS_password} \
            --docker-email=${K8s_docker_email}"
      }
    }
  }
}
