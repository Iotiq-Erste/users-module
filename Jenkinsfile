pipeline {
    agent any
    
    environment {
        DEPLOYMENT_BRANCH = "master"
        PARENT_PROJECT_DEPLOYMENT_JOB = "../culturati-backend/master"
    }

    stages {
        stage("Env Params") {
            steps {
                sh 'printenv'
            }
        }
        stage("Deploy") {
            when {
                allOf {
                    branch env.DEPLOYMENT_BRANCH
                }
            }
            steps {
                echo 'Run parent project build/deployment job'
                build job: env.PARENT_PROJECT_DEPLOYMENT_JOB
            }
        }
    }
}