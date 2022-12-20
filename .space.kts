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

    host("Docker build and push") {
    host("Docker build and push") {
        env["HUB_USER"] = Secrets("dockerhub_user")
        env["HUB_TOKEN"] = Secrets("dockerhub_token")

        shellScript {
            content = """
                docker login --username ${'$'}HUB_USER --password "${'$'}HUB_TOKEN"
            """
        }

        dockerBuildPush {
            file = "./docker/Dockerfile"
            labels["vendor"] = "mistermarlu"
            tags {
                +"build:\$BRANCH"
            }
        }
    }
}