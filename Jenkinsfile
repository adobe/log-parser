pipeline {
    agent any
    triggers { githubPush() }

    stages {
        
        stage('Run Tests') {
                    steps {
                        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'ARTIFACTORY_API_TOKEN',
					        usernameVariable: 'ARTIFACTORY_USER', passwordVariable: 'ARTIFACTORY_API_TOKEN']]) {

                                sh """
                                    mvn clean test --settings .mvn/settings.xml \
                                    -Dsurefire.suiteXmlFiles=src/test/resources/testng.xml \
                                """
                            }
                    }                
        }
        stage('Publish') {
            steps {
                //cobertura autoUpdateHealth: false, autoUpdateStability: false, coberturaReportFile: 'target/site/cobertura/coverage.xml', conditionalCoverageTargets: '82, 0, 0', failUnhealthy: false, failUnstable: false, lineCoverageTargets: '91, 0, 0', maxNumberOfBuilds: 0, methodCoverageTargets: '80, 0, 0', onlyStable: false, sourceEncoding: 'ASCII', zoomCoverageChart: false
                
                step([$class: 'Publisher', reportFilenamePattern: 'target/surefire-reports/testng-results.xml'])
            }
        }
    }
}
