package idv.liucheyu.pomreader.model;

import java.nio.file.Path;

public class PomModel {
    private String projectName = "";
    private Path projectPath;
    private String groupId = "";
    private String artifactId = "";
    private String version = "";
    private Dependency dependency = new Dependency();

    public PomModel() {
    }

    public PomModel(String projectName, Path projectPath, String groupId, String artifactId, String version, Dependency dependency) {
        this.projectName = projectName;
        this.projectPath = projectPath;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.dependency = dependency;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Path getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(Path projectPath) {
        this.projectPath = projectPath;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public void setDependency(Dependency dependency) {
        this.dependency = dependency;
    }
}
