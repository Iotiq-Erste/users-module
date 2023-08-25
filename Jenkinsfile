pipeline {
    agent any
    
    environment {
        DEPLOYMENT_BRANCH = "master"
        PARENT_PROJECT_DEPLOYMENT_JOB = "../culturati-backend/master"
    }

    stages {
        stage("Env Params") {
            steps {
                scripts{
                    env.COMMIT_MESSAGE = sh(script: "git log --pretty=short -1 | cat", returnStdout: true).trim()
                }
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
                build job: env.PARENT_PROJECT_DEPLOYMENT_JOB,
                        parameters:[
                                string(name: 'COMMIT_MESSAGE', value: "${env.COMMIT_MESSAGE}")
                        ],
                        wait: false
            }
        }
    }
}