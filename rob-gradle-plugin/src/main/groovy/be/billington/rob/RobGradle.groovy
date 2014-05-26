package be.billington.rob

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.StopExecutionException

import be.billington.rob.bitbucket.RobLogBitbucketManager
import be.billington.rob.github.RobLogGithubManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class RobGradle implements Plugin<Project> {
    void apply(Project project) {
        Logger slf4jLogger = LoggerFactory.getLogger('some-logger')
        //StaticLoggerBinder.getSingleton().getLoggerFactory().getLogger("")
        if (true) {
            manager = new RobLogBitbucketManager(slf4jLogger, config, key, secret, owner, repository, branch, startDate, endDate);
        } else {
            manager = new RobLogGithubManager(slf4jLogger, config, token, owner, repository, startDate, endDate);
        }


    }
}