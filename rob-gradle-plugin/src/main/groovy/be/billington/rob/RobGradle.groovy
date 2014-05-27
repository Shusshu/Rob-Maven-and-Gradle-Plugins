package be.billington.rob

import org.gradle.api.Plugin
import org.gradle.api.Project

import be.billington.rob.bitbucket.BitbucketCredentials
import be.billington.rob.github.GithubCredentials
import be.billington.rob.Credentials


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

        def endDateStr = ""
        if ( project.hasProperty('robToDate') ) {
            endDate = project.robToDate
        }
        def startDateStr = ""
        if ( project.hasProperty('robFromDate') ) {
            startDateStr = project.robFromDate
        }

        //TODO pass also the custom config file

        project.logger.info( "Robbing..." );

        try {
            Credentials credentials;
            if (api.toLowerCase().equals(Rob.API_BITBUCKET)){
                credentials = new BitbucketCredentials(project.bitbucketKey, project.bitbucketSecret);
            } else {
                credentials = new GithubCredentials(project.githubToken);
            }
            Rob.logs(project.logger, api, project.robOwner, project.robRepository, prefix, branch, "", project.robFile, startDateStr, endDateStr, credentials);

        } catch (Exception e) {
            project.logger.error( "Error: " + e.getMessage(), e);
        }

        project.logger.info( "Robbed." );
    }

}