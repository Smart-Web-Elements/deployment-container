/**
* JetBrains Space Automation
* This Kotlin-script file lets you automate build activities
* For more info, see https://www.jetbrains.com/help/space/automation.html
*/

job("Build Docker image") {
    docker {
        build {
            context = "docker"
            file = "./docker/Dockerfile"
            labels["vendor"] = "swe"
        }

        push("swe.registry.jetbrains.space/p/swe/deployment-container/deployment") {
            tags("0.0.1")
        }
    }
}
