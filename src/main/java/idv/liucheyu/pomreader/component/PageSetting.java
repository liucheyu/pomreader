package idv.liucheyu.pomreader.component;

import idv.liucheyu.pomreader.controller.MainController;
import idv.liucheyu.pomreader.model.ConfigModel;
import idv.liucheyu.pomreader.model.PomModel;
import idv.liucheyu.pomreader.service.ConfigService;
import idv.liucheyu.pomreader.service.FileService;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class PageSetting {

    private FileService fileService = new FileService();
    private ConfigService configService = new ConfigService();

    public void setUpMainPage(ConfigModel configModel, Stage stage, MainController mainController) {
//        //取得並儲存baseFolder
//        while (configModel.getBaseFolder().equals("")) {
//            configModel.setBaseFolder(configService.getBaseFolderDialog(stage));
//        }
//        configService.setBaseFolderPath(configModel.getBaseFolder());
//        //取得並儲存dependency
//        while (configModel.getDependencyGroupId().equals("")) {
//            configModel.setDependencyGroupId(configService.getInputDependencyDialog());
//        }
//        configService.setBaseDependency(configModel.getDependencyGroupId());

        if(!configModel.getBaseFolder().equals("") && !configModel.getDependencyGroupId().equals("")){
            List<Path> projectPath  = fileService.getPomPath(Paths.get(configModel.getBaseFolder()));

            //讀取releaseNote的版號,並加為Map(projectName, version)
            Map<String, String> originVersionMap = fileService.getLastReleaseVersion(projectPath);
            //讀取pom並收集成Map(projectName, version)
            List<PomModel> pomModels = fileService.getPomVersion(projectPath, configModel.getDependencyGroupId());

            renderMain(mainController, projectPath, originVersionMap, pomModels);

        }

    }

    public void renderMain(MainController mainController, List<Path> projectPath, Map<String, String> originVersionMap, List<PomModel> pomModels) {
        IndividualPomVersionComponent idvComponent = new IndividualPomVersionComponent(mainController, projectPath, originVersionMap, pomModels);
        idvComponent.render();
    }

}
