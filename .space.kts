/**
* JetBrains Space Automation
* This Kotlin-script file lets you automate build activities
* For more info, see https://www.jetbrains.com/help/space/automation.html
*/

job("Publish Docker image") {
    docker("Docker build and push") {
        env["DOCKERHUB_USER"] = Secrets("dockerhub_user")
        env["DOCKERHUB_TOKEN"] = Secrets("dockerhub_token")

        beforeBuildScript {
            content = """
                B64_AUTH=${'$'}(echo -n ${'$'}DOCKERHUB_USER:${'$'}DOCKERHUB_TOKEN | base64 -w 0)
                echo "{\"auths\":{\"https://index.docker.io/v1/\":{\"auth\":\"${'$'}B64_AUTH\"}}}" > ${'$'}DOCKER_CONFIG/config.json
            """
        }

        build {
            context = "docker"
            file = "./docker/Dockerfile"
            labels["vendor"] = "mistermarlu"
        }

        push("deployment/automation") {
            tags("1.0.\$JB_SPACE_EXECUTION_NUMBER", "latest")
        }
    }
}
