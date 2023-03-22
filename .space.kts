/**
* JetBrains Space Automation
* This Kotlin-script file lets you automate build activities
* For more info, see https://www.jetbrains.com/help/space/automation.html
*/

job("Publish Docker image") {
    startOn {
        gitPush {
            branchFilter {
                +"refs/heads/php-*"
            }
        }
    }

    parameters {
        text("docker-user", value = "{{ project:dockerhub_user }}", description = "Docker Username", allowCustomRunOverride = false)
        secret("docker-token", value = "{{ project:dockerhub_token }}", description = "Docker Auth Token", allowCustomRunOverride = false)
    }

    host("Docker build and push") {
        env["HUB_USER"] = "{{ docker-user }}"
        env["HUB_TOKEN"] = "{{ docker-token }}"

        shellScript {
            content = """
                docker login --username ${'$'}HUB_USER --password "${'$'}HUB_TOKEN"
            """
        }

        dockerBuildPush {
            file = "./docker/Dockerfile"
            labels["vendor"] = "mistermarlu"
            tags {
                +"mistermarlu/build:${'$'}(echo ${'$'}JB_SPACE_GIT_BRANCH | cut -d'/' -f 3)"
            }
        }
    }
}