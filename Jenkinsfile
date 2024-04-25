pipeline {
    agent any

    environment {
        DEPLOYMENT_BRANCH = "kimono"
        PARENT_PROJECT_DEPLOYMENT_JOB = "../culturati-kimono/master"
    }

    stages {
        stage("Env Params") {
            steps {
                script{
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
                        wait: false
            }
        }
    }
}
