#Rob

##Usage

Add this to your pom

    <rob.repo>bitbucket-repository-name</rob.repo>
    <rob.prefix>jira-prefix</rob.prefix>
    <timestamp>${maven.build.timestamp}</timestamp>
    <maven.build.timestamp.format>yyyy-MM-dd</maven.build.timestamp.format>

    <profile>
        <id>rob</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>be.billington</groupId>
                    <artifactId>rob-maven-plugin</artifactId>
                    <version>1.0.0</version>
                    <executions>
                        <execution>
                            <phase>initialize</phase>
                            <goals>
                                <goal>rob</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </profile>


