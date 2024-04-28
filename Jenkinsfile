pipeline {
    agent any

    //THIS IS TEMPLATE, REAL Jenkinsfile IMPLEMENTATIONS MOVED TO PROJECT BRANCHES. Please check branches like culturati, kimono ..etc
    environment {
        DEPLOYMENT_BRANCH = "deployment_branch"
        PARENT_PROJECT_DEPLOYMENT_JOB = "../job_name/branch_name"
        COMMIT_MESSAGE = sh(script: "git log --pretty=short -1 | cat", returnStdout: true).trim()
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
                /*
                build job: env.PARENT_PROJECT_DEPLOYMENT_JOB,parameters: [
                    string(name: 'SUBMODULE_JOB_NAME', value: env.JOB_NAME),
                    string(name: 'SUBMODULE_COMMIT_MESSAGE', value: env.COMMIT_MESSAGE),
                    string(name: 'SUBMODULE_BUILD_DISPLAY_NAME', value: BUILD_DISPLAY_NAME),
                    string(name: 'SUBMODULE_RUN_DISPLAY_URL', value: RUN_DISPLAY_URL)
                    ], wait: false
                */
            }
        }
    }
}
