package be.billington.rob;

import org.slf4j.Logger;

import java.io.File;

public class Configuration extends Profile {

    private final String key;
    private final String secret;
    private final String username;
    private final String password;
    private final String token;
    private final Logger logger;
    private final File outputDir;

    private Configuration(ConfigurationBuilder builder) {
        super(builder.api, builder.owner, builder.repository, builder.prefix, builder.branch,
                builder.configPath, builder.filePath, builder.fromDate, builder.toDate);
        this.key = builder.key;
        this.secret = builder.secret;
        this.username = builder.username;
        this.password = builder.password;
        this.token = builder.token;
        this.logger = builder.logger;
        this.outputDir = builder.outputDir;
    }

    public String getKey() {
        return key;
    }

    public String getSecret() {
        return secret;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public Logger getLogger() {
        return logger;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public boolean hasKeySecret() {
        return key != null && key.length() > 0 && secret != null && secret.length() > 0;
    }

    public boolean hasUsernamePassword() {
        return username != null && username.length() > 0 && password != null && password.length() > 0;
    }

    public boolean hasToken() {
        return token != null && token.length() > 0;
    }

    public static class ConfigurationBuilder implements Builder<Configuration> {
        private String api;
        private String owner;
        private String repository;
        private String prefix;
        private String branch;
        private String configPath;
        private String filePath;
        private String fromDate;
        private String toDate;
        private String key;
        private String secret;
        private String username;
        private String password;
        private String token;
        private Logger logger;
        private File outputDir;

        public ConfigurationBuilder(Logger logger, String api, String repository, String owner) {
            this.logger = logger;
            this.api = api;
            this.repository = repository;
            this.owner = owner;
        }

        public ConfigurationBuilder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public ConfigurationBuilder branch(String branch) {
            this.branch = branch;
            return this;
        }

        public ConfigurationBuilder configPath(String configPath) {
            this.configPath = configPath;
            return this;
        }

        public ConfigurationBuilder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public ConfigurationBuilder fromDate(String fromDate) {
            this.fromDate = fromDate;
            return this;
        }

        public ConfigurationBuilder toDate(String toDate) {
            this.toDate = toDate;
            return this;
        }

        public ConfigurationBuilder key(String key) {
            this.key = key;
            return this;
        }

        public ConfigurationBuilder secret(String secret) {
            this.secret = secret;
            return this;
        }

        public ConfigurationBuilder username(String username) {
            this.username = username;
            return this;
        }

        public ConfigurationBuilder password(String password) {
            this.password = password;
            return this;
        }

        public ConfigurationBuilder token(String token) {
            this.token = token;
            return this;
        }

        public ConfigurationBuilder outputDir(File outputDir) {
            this.outputDir = outputDir;
            return this;
        }

        @Override
        public Configuration build() {
            return new Configuration(this);
        }

    }

    public interface Builder<T> {
        T build();
    }

}
