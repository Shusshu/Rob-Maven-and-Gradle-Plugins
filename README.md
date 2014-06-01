# Rob

## Goal

Rob's goal is to generate change log reports for your project by using bitbucket or github commits log by following a set of rules to generate the report.


## Usage

### Maven

#### Without a project pom.xml

mvn rob:logs -Prob -Drob.repo=Rob-Maven-Plugin -Drob.api=github -Drob.owner=Shusshu



#### With a project pom.xml

Add this to your pom.xml

    <properties>
        <!-- REQUIRED Properties -->
        <rob.repo>bitbucket-repository-name</rob.repo>
        <rob.owner>Git repository owner</rob.owner>

        <!-- Optional properties -->
        <rob.prefix>jira-prefix</rob.prefix>
        <rob.api>bitbucket|github</rob.api> <!-- default bitbucket -->

        <rob.from.date>Commits from this date</rob.from.date> <!-- default 2 weeks ago -->
        <rob.to.date>Commits to this date</rob.to.date> <!-- default to today -->
        <rob.branch>bitbucket branch to scan</rob.branch> <!-- default development -->
        <rob.file>Output path for the generated change log</rob.file> <!-- default ./changelog.txt -->

        <rob.rules>path to a custom config</rob.rules> <!-- default internal json rules  See config section for more info-->

        <rob.key>bitbucket key</rob.key>
        <rob.secret>bitbucket secret</rob.secret>
        <rob.github.token>github token</rob.github.token>
    </properties>

    <profile>
        <id>rob</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>be.billington</groupId>
                    <artifactId>rob-maven-plugin</artifactId>
                    <version>2.3.0</version>
                    <executions>
                        <execution>
                            <phase>generate-resources</phase>
                            <goals>
                                <goal>logs</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </profile>

And this to your settings.xml

    <profile>
        <id>rob</id>
        <activation>
            <activeByDefault>false</activeByDefault>
        </activation>
        <properties>
            <rob.key>Your bitbucket key</rob.key>
            <rob.secret>Your bitbucket secret</rob.secret>
            <rob.github.token>Your github token</rob.secret>
        </properties>
    </profile>


### Gradle

Add the following to the build.gradle

    buildscript {
      repositories {
        mavenLocal()
        mavenCentral()
      }
      dependencies {
        classpath 'be.billington.rob:rob-gradle-plugin:3.0.0'
      }
    }

    apply plugin: 'rob-plugin'


Add the following to the project gradle.properties

    robApi=bitbucket or github
    robOwner=The git repository owner
    robRepository=The git repository name
    robBranch=The branch
    robPrefix=The jira prefix
    robFile=The output file e.g. "./build/changelog.txt"


Add the following to the user gradle.properties

    bitbucketKey=Your bitbucket key
    bitbucketSecret=Your bitbucket secret
    githubToken=Your github token


### Rob CLI

java -jar rob-cli.jar --repo REPONAME -o OWNER -a API -f OUTPUT

### Rob SWT

Run rob-swt.jar and play with rob.

Adding profile, switching profile, generating the report and view it directly in the UI.


### Rob JavaFX 8

Run rob-javafx and play with rob.

Generating the report and view it directly in the UI.


## OAuth

### Bitbucket oauth 1 key & secret
Generate a Bitbucket key and secret by following the instructions here: https://confluence.atlassian.com/display/BITBUCKET/OAuth+on+Bitbucket


### Github oauth 2 token
Generate a github token: https://github.com/settings/applications


## Config JSON

### default config
    {
        "sections": [
            {
                "title": "\nJira Tickets:\n",
                "match": "${rob.prefix}"
            },
            {
                "title": "\n\nCrash fixes from Crashlytics:\n",
                "match": "crash #"
            },
            {
                "title": "\n\nBitbucket Tickets:\n",
                "match": "issue #"
            },
            {
                "title": "\n\nCode quality improvement (Sonar):\n",
                "match": "sonar"
            }
        ],
        "exclusive_sections": [
            {
                "title": "\n\nUncategorised messages:\n",
                "excludes": [
                    "merge remote-tracking branch",
                    "merge branch"
                ]
            }
        ]
    }