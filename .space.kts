/**
* JetBrains Space Automation
* This Kotlin-script file lets you automate build activities
* For more info, see https://www.jetbrains.com/help/space/automation.html
*/

job("Publish Docker image") {
    startOn {
        gitPush {
            branchFilter {
                +Regex("php")
            }
        }
    }

    docker("Docker build and push") {
        env["DOCKERHUB_USER"] = Secrets("dockerhub_user")
        env["DOCKERHUB_TOKEN"] = Secrets("dockerhub_token")

        beforeBuildScript {
            content = """
                B64_AUTH=${'$'}(echo -n ${'$'}DOCKERHUB_USER:${'$'}DOCKERHUB_TOKEN | base64 -w 0)
                echo "{\"auths\":{\"https://index.docker.io/v1/\":{\"auth\":\"${'$'}B64_AUTH\"}}}" > ${'$'}DOCKER_CONFIG/config.json
                export BRANCH=${'$'}(echo ${'$'}JB_SPACE_GIT_BRANCH | cut -d'/' -f 3)
            """
        }

        build {
            file = "./docker/Dockerfile"
            labels["vendor"] = "mistermarlu"
        }

        push("deployment") {
            tags("\$BRANCH", "\$BRANCH.\$JB_SPACE_EXECUTION_NUMBER", "latest")
        }
    }
}