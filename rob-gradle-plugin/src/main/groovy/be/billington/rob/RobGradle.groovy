package be.billington.rob

import org.gradle.api.Plugin
import org.gradle.api.Project

import be.billington.rob.bitbucket.RobLogBitbucketManager
import be.billington.rob.github.RobLogGithubManager
import be.billington.rob.RobLogManager
import be.billington.rob.ConfigSections

import java.time.LocalDate


class RobGradle implements Plugin<Project> {
    void apply(Project project) {

        ConfigSections config = ConfigSections.createConfigSections(null, project.robPrefix)

        RobLogManager manager
        if (project.robApi.equals("bitbucket")) {
            manager = new RobLogBitbucketManager(project.logger, config, project.bitbucketKey, project.bitbucketSecret, project.robOwner, project.robRepository, project.robBranch, LocalDate.now().minusDays(14), LocalDate.now())
        } else {
            manager = new RobLogGithubManager(project.logger, config, project.githubToken, project.robOwner, project.robRepository, LocalDate.now().minusDays(14), LocalDate.now())
        }


        manager.fetchAndProcessCommitMessages()

        manager.generateFile(null, project.robFile)

    }
}