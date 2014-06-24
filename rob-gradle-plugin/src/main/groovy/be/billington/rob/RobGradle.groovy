package be.billington.rob

import org.gradle.api.Plugin
import org.gradle.api.Project

import javax.security.auth.login.Configuration


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


        def keyStr = ""
        if ( project.hasProperty('bitbucketKey') ) {
            keyStr = project.bitbucketKey
        }
        def secretStr = ""
        if ( project.hasProperty('bitbucketSecret') ) {
            secretStr = project.bitbucketSecret
        }
        def usernameStr = ""
        if ( project.hasProperty('bitbucketUsername') ) {
            usernameStr = project.bitbucketUsername
        }
        def passwordStr = ""
        if ( project.hasProperty('bitbucketPassword') ) {
            passwordStr = project.bitbucketPassword
        }

        //TODO pass also the custom config file

        project.logger.info( "Robbing..." );

        try {
            Configuration conf = new Configuration.ConfigurationBuilder(logger, api, project.robRepository, project.robOwner)
                    .branch(branch).prefix(prefix).filePath(project.robFile).fromDate(startDateStr).toDate(endDateStr)
                    .token(project.githubToken).key(keyStr).secret(secretStr).username(usernameStr).password(passwordStr).build();

            Rob.logs(conf);

        } catch (Exception e) {
            project.logger.error( "Error: " + e.getMessage(), e);
        }

        project.logger.info( "Robbed." );
    }

}