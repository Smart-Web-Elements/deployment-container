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
            labels["vendor"] = "SWE"
        }

        push("s-w-e.registry.jetbrains.space/swe/deployment-container/deployment") {
            tags("0.0.\$JB_SPACE_EXECUTION_NUMBER")
        }
    }
}
