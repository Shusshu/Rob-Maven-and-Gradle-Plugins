# Rob

## Goal

Rob's goal is to generate change log reports for your project by using bitbucket or github commits log by following a set of rules to generate the report.


## Usage

###Without a project pom.xml

mvn rob:logs -Prob -Drob.repo=Rob-Maven-Plugin -Drob.api=github -Drob.owner=Shusshu



##With a project pom.xml

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
            <rob.key>your bitbucket key</rob.key>
            <rob.secret>your bitbucket secret</rob.secret>
            <rob.github.token>your github token</rob.secret>
        </properties>
    </profile>

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