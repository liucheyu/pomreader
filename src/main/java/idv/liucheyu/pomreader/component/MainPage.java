package idv.liucheyu.pomreader.component;

import idv.liucheyu.pomreader.service.ConfigService;
import idv.liucheyu.pomreader.service.FileService;
import javafx.scene.layout.GridPane;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class MainPage {

    private FileService fileService = new FileService();
    private ConfigService configService = new ConfigService();

    public void renderMain(GridPane pane, String folderPath, List<Path> projectPath, Map<String, String> originVersionMap, Map<String, String> pomVersionMap, String depGoupid) {
        if(folderPath.isEmpty()){
            configService.getConfigFolderByFile().get("baseFolder");
        }
        if(projectPath == null){
            projectPath = fileService.getPomPath(Paths.get(folderPath));
        }

        //讀取releaseNote的版號,並加為Map(projectName, version)
        originVersionMap = fileService.getLastReleaseVersion(projectPath);
        //讀取pom並收集成Map(projectName, version)
        pomVersionMap = fileService.getPomVersion(projectPath);

        IndividualPomVersionComponent idvComponent = new IndividualPomVersionComponent(pane, projectPath, originVersionMap, pomVersionMap, depGoupid);
        idvComponent.render();
    }

}
