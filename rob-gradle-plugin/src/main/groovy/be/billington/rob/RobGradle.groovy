package be.billington.rob

import org.gradle.api.Plugin
import org.gradle.api.Project

import be.billington.rob.bitbucket.RobLogBitbucketManager
import be.billington.rob.github.RobLogGithubManager
import be.billington.rob.RobLogManager
import be.billington.rob.ConfigSections

import java.time.LocalDate
import java.time.format.DateTimeFormatter


class RobGradle implements Plugin<Project> {
    void apply(Project project) {

        //Mandatory params checks
        if ( !project.hasProperty('robOwner') || !project.hasProperty('robRepository')) {
            throw new IllegalArgumentException("Missing params: owner or repository")
        }

        //Defaults
        def prefix = ""
        if ( project.hasProperty('robPrefix') ) {
            prefix = project.robPrefix
        }
        def branch = "development"
        if ( project.hasProperty('robBranch') ) {
            branch = project.robBranch
        }
        def api = "bitbucket"
        if ( project.hasProperty('robApi') ) {
            api = project.robApi
        }

        def endDate = LocalDate.now()
        if ( project.hasProperty('robToDate') ) {
            endDate = LocalDate.parse(project.robToDate, DateTimeFormatter.ISO_LOCAL_DATE)
        }
        def startDate = endDate.minusDays(14)
        if ( project.hasProperty('robFromDate') ) {
            startDate = LocalDate.parse(project.robFromDate, DateTimeFormatter.ISO_LOCAL_DATE)
        }

        if (startDate.isAfter(endDate)){
            throw new IllegalArgumentException("'From date' must be before 'to date'")
        }

        //TODO pass also the custom config file

        ConfigSections config = ConfigSections.createConfigSections(null, prefix)

        RobLogManager manager
        if (api.equals("bitbucket")) {
            manager = new RobLogBitbucketManager(project.logger, config, project.bitbucketKey, project.bitbucketSecret, project.robOwner, project.robRepository, branch, startDate, endDate)
        } else {
            manager = new RobLogGithubManager(project.logger, config, project.githubToken, project.robOwner, project.robRepository, startDate, endDate)
        }

        manager.fetchAndProcessCommitMessages()

        manager.generateFile(null, project.robFile)

    }

}