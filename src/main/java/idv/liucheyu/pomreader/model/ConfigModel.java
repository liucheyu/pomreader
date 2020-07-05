package idv.liucheyu.pomreader.model;

public class ConfigModel {
    private String baseFolder;
    private String dependencyGroupId;
    private String mavenDir;
    private String mavenSetting;

    public ConfigModel(String baseFolder, String dependencyGroupId, String mavenDir, String mavenSetting) {
        this.baseFolder = baseFolder;
        this.dependencyGroupId = dependencyGroupId;
        this.mavenDir = mavenDir;
        this.mavenSetting = mavenSetting;
    }

    public ConfigModel() {
    }

    public String getBaseFolder() {
        return baseFolder;
    }

    public void setBaseFolder(String baseFolder) {
        this.baseFolder = baseFolder;
    }

    public String getDependencyGroupId() {
        return dependencyGroupId;
    }

    public void setDependencyGroupId(String dependencyGroupId) {
        this.dependencyGroupId = dependencyGroupId;
    }

    public String getMavenDir() {
        return mavenDir;
    }

    public void setMavenDir(String mavenDir) {
        this.mavenDir = mavenDir;
    }

    public String getMavenSetting() {
        return mavenSetting;
    }

    public void setMavenSetting(String mavenSetting) {
        this.mavenSetting = mavenSetting;
    }
}
