#Rob

##Usage

Add this to your pom

    <properties>
        <rob.repo>bitbucket-repository-name</rob.repo>
        <rob.prefix>jira-prefix</rob.prefix>
        <timestamp>${maven.build.timestamp}</timestamp>
        <maven.build.timestamp.format>yyyy-MM-dd</maven.build.timestamp.format>
    </properties>

    <profile>
        <id>rob</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>be.billington</groupId>
                    <artifactId>rob-maven-plugin</artifactId>
                    <version>1.0.1</version>
                    <executions>
                        <execution>
                            <phase>generate-resources</phase>
                            <goals>
                                <goal>rob</goal>
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
        </properties>
    </profile>


Generate a Bitbucket key and secret by following the instructions here: https://confluence.atlassian.com/display/BITBUCKET/OAuth+on+Bitbucket
