package be.billington.rob

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.StopExecutionException

import be.billington.rob.bitbucket.RobLogBitbucketManager
import be.billington.rob.github.RobLogGithubManager

class RobGradle implements Plugin<Project> {
    void apply(Project project) {

        //StaticLoggerBinder.getSingleton().getLoggerFactory().getLogger("")
        if (true) {
            manager = new RobLogBitbucketManager(log, config, key, secret, owner, repository, branch, startDate, endDate);
        } else {
            manager = new RobLogGithubManager(log, config, token, owner, repository, startDate, endDate);
        }

        project.extensions.create("greeting", GreetingPluginExtension)
        project.task('hello') << {
            println "${project.greeting.message} from ${project.greeting.greeter}"
        }
    }
}