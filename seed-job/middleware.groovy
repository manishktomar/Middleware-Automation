folder('Middleware-deployment')

// Shared reactive scripts reused across all jobs
awsProfileScript = """
def map = [
    'Dev'    : 'dev-ecr',
    'Prod-DC'    : 'prod-dc',
    'Prod-DR'    : 'prod-dr'
]
return [map[CLUSTER_NAME]]
"""

clusterContextScript = """
def map = [
    'Dev'    : 'arn:aws:eks:ap-south-1:xxxxxxxxxxxxx:cluster/dev-eks',
    'Prod-DC'    : 'arn:aws:eks:ap-south-1:xxxxxxxxxxxxx:cluster/prod-dc-eks',
    'Prod-DR'    : 'arn:aws:eks:ap-south-2:xxxxxxxxxxxxx:cluster/prod-dr-eks'
]

return [map[CLUSTER_NAME]]
"""

namespaceScript = """
def map = [
    'Dev'    : 'dev',
    'Prod-DC'    : 'prod-dc',
    'Prod-DR'    : 'prod-dr'
]
return [map[CLUSTER_NAME]]
"""

branchScript = """
def map = [
    'Dev'    : 'main',
    'Prod-DC'    : 'prod',
    'Prod-DR'    : 'prod'
]
return [map[CLUSTER_NAME]]
"""

rollbackVersionScript = """
if (DEPLOYMENT_ACTION == 'ROLLBACK') {
    return ['']
} else {
    return []
}
"""

/*********************** MIDDLEWARE CONFIG ************************/

def middlewares = [

    keycloak: [
        script : 'jenkins/middleware-deployment/keycloak.jenkinsfile',
        versions : ['26.5.7 (Latest)','26.4.4 (Stable)','24.1.1 (Old)'],
        registry : 'public.ecr.aws/h7c5e5a9/keycloak'
    ],

    postgresql: [
        script : 'jenkins/middleware-deployment/postgresql.jenkinsfile',
        versions : ['18.1 (Latest)','17.5 (Stable)','15.0 (Old-No)'],
        registry : 'public.ecr.aws/xxxxxx/postgres'
    ],

    mongodb: [
        script : 'jenkins/middleware-deployment/mongodb.jenkinsfile',
        versions : ['8.0.20 (Latest)','7.0.14 (Stable)','5.0.31 (Old-No)'],
        registry : 'public.ecr.aws/xxxxxx/mongo'
    ],

    'redis-master': [
        script : 'jenkins/middleware-deployment/redis-master.jenkinsfile',
        versions : ['8.2.5 (Latest)','7.4.2 (Stable)','7.4.1 (Old)'],
        registry : 'public.ecr.aws/xxxxxx/redis'
    ],

    'redis-replicas': [
        script : 'jenkins/middleware-deployment/redis-replicas.jenkinsfile',
        versions : ['8.2.5 (Latest)','7.4.2 (Stable)','7.4.1 (Old)'],
        registry : 'public.ecr.aws/xxxxxx/redis'
    ],

    kafka: [
        script : 'jenkins/middleware-deployment/kafka.jenkinsfile',
        versions : ['4.2.0 (Latest)','3.9.2 (Stable)','3.7.2 (Old-No)'],
        registry : 'public.ecr.aws/xxxxxx/kafka',
        extraParam : [
            paramName : 'PURPOSE',
            values : ['Kafka-SASL','Kafka-SASL-SSL']
        ]
    ],

    vault: [
        script : 'jenkins/middleware-deployment/vault.jenkinsfile',
        versions : ['1.21.2 (Latest)','1.20.4 (Stable)','1.18.0 (Old)'],
        registry : 'public.ecr.aws/xxxxxx/vault'
    ],

    sftpgo: [
        script : 'jenkins/middleware-deployment/sftpgo.jenkinsfile',
        versions : ['2.7.1 (Latest)','2.5.1 (Stable)','2.4.0 (Old)'],
        registry : 'public.ecr.aws/xxxxxx/sftpgo'
    ],

    'aws-lb-controller': [
        script : 'jenkins/middleware-deployment/aws-lb-controller.jenkinsfile',
        versions : ['3.2.2 (Latest)','3.1.0 (Stable)','3.0.0 (Old)'],
        registry : 'eks/aws-load-balancer-controller'
    ]
]

/*********************** JOB GENERATOR ************************/

middlewares.each { appName, config ->

    pipelineJob("Middleware-deployment/${appName}") {
        definition {
            cpsScm {
                scm {
                    git {
                        remote {
                            name('origin')
                            url('https://git.smartbox.in/cicd/cicd.git')
                            credentials('GitLab_santosh_access_token_readwrite_master')
                            branch('main')
                        }
                    }
                }
                scriptPath(config.script)
            }
        }

    parameters {
        choiceParam('CLUSTER_NAME', ['Dev', 'Prod-DC', 'Prod-DR'], 'Target cluster name')
        activeChoiceReactiveParam('GIT_BRANCH') {
            description('Git branch to deploy from - auto-selected based on CLUSTER_NAME')
            filterable(false)
            choiceType('SINGLE_SELECT')
            referencedParameter('CLUSTER_NAME')
            groovyScript {
                script(branchScript)
            }
        }
        activeChoiceReactiveParam('AWS_PROFILE') {
            description('AWS Profile to assume - auto-selected based on CLUSTER_NAME')
            filterable(false)
            choiceType('SINGLE_SELECT')
            referencedParameter('CLUSTER_NAME')
            groovyScript {
                script(awsProfileScript)
            }
        }
        activeChoiceReactiveParam('CLUSTER_CONTEXT') {
            description('Cluster context - auto-selected based on CLUSTER_NAME')
            filterable(false)
            choiceType('SINGLE_SELECT')
            referencedParameter('CLUSTER_NAME')
            groovyScript {
                script(clusterContextScript)
            }
        }
        if (appName == 'aws-lb-controller') {
            stringParam('NAMESPACE', '', 'Namespace for AWS LB Controller (manual input required)')
            stringParam('AWS_ACCOUNT_ID', '', 'AWS Account ID to use for controller IAM role ARN (required)')
        } else {
            activeChoiceReactiveParam('NAMESPACE') {
                description('Namespace - auto-selected based on CLUSTER_NAME')
                filterable(false)
                choiceType('SINGLE_SELECT')
                referencedParameter('CLUSTER_NAME')
                groovyScript {
                    script(namespaceScript)
                }
            }
        }

        if (config.extraParam) {
            choiceParam(config.extraParam.paramName, config.extraParam.values, "${appName} deployment mode")
        }

        choiceParam('VERSION', config.versions, "${appName} image tag")
        choiceParam('ECR_REGISTRY', [config.registry], 'ECR registry URL')
        booleanParam('DEPLOY',     true, 'Check to Deploy the Image on Cluster')
        booleanParam('DRY_RUN',    false,  'Check to dry-run Helm Deployment')
        booleanParam('UPGRADE',    false, 'Check to upgrade the Image on Cluster')
        booleanParam('UNINSTALL',  false, 'Check to UNINSTALL deployment')
        booleanParam('ROLLBACK',   false, 'Check to ROLLBACK to a previous Helm revision')
        stringParam('ROLLBACK_VERSION', '0', 'Helm revision number to rollback to (0 = previous revision)')
        }

        properties {
            disableConcurrentBuilds()
        }

        logRotator {
            numToKeep(5)
            artifactNumToKeep(5)
        }
    }
}
