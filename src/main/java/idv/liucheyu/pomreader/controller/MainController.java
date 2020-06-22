package idv.liucheyu.pomreader.controller;

import idv.liucheyu.pomreader.component.IndividualPomVersionComponent;
import idv.liucheyu.pomreader.component.MainPage;
import idv.liucheyu.pomreader.event.SamePomVersionEvent;
import idv.liucheyu.pomreader.service.ConfigService;
import idv.liucheyu.pomreader.service.FileService;
import idv.liucheyu.pomreader.service.GitService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.eclipse.jgit.lib.Repository;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainController {

    @FXML
    private StackPane mainPane;
    @FXML
    private MenuBar menuBar1;
    @FXML
    private MenuItem MenuItemImport;
    @FXML
    private GridPane singleModifyGrid;
    @FXML
    private TextField sameVersionTField;

    private FileService fileService = new FileService();
    private ConfigService configService = new ConfigService();

    private Stage stage;
    private List<Path> projectPath;
    private IndividualPomVersionComponent idvComponent;
    private List<Text> pomVersionText = new ArrayList<>();
    private MainPage mainPage;

    private Map<String, String> originVersionMap;
    private Map<String, String> pomVersionMap;
    private String depGoupid = "";
    private String baseFolder = "";
    private GitService gitService = new GitService();

//    @FXML
//    protected void importOnClickAction(ActionEvent event) {
//        setProjectPath(new ArrayList<>());
//        MenuItem menuItem = (MenuItem) event.getSource();
//        String menuItemId = menuItem.getId();
//        if (MenuItemImport.getId().equals(menuItemId)) {
//
//            DirectoryChooser chooser = fileService.initDirectoryChooser("請選擇專案的父層資料夾");
//            File file = chooser.showDialog(stage);
//            if(file.exists()){
//                this.baseFolder = fileService.getAbsolutePath(file);
//
//                mainPage = new MainPage();
//                depGoupid = depGoupid.equals("") ? configService.getConfigFolderByFile().get("dependency") : depGoupid;
//                depGoupid = depGoupid.equals("") ? configService.getInputDependencyDialog() : depGoupid;
//                if (!depGoupid.equals("")) {
//                    mainPage.renderMain(singleModifyGrid, baseFolder, projectPath, originVersionMap, pomVersionMap, depGoupid);
//                }
//            }
//        }
//    }

    public void setUpMainPage(String baseFolder, String depGoupid) {
        this.depGoupid = depGoupid;
        this.baseFolder = baseFolder;
        mainPage = new MainPage();
        mainPage.renderMain(singleModifyGrid, baseFolder, projectPath, originVersionMap, pomVersionMap, depGoupid);
    }

    @FXML
    protected void samePomVersionEvent(ActionEvent event) {
        if (projectPath == null || projectPath.isEmpty()) {
            if (baseFolder.equals("")) {
                baseFolder = configService.getBaseFolderDialog(stage);
            }
            projectPath = fileService.getPomPath(Paths.get(baseFolder));
        }

        if (((Button) event.getSource()).getId().equals("sameVersionButton")) {
            SamePomVersionEvent samePomVersionEvent = new SamePomVersionEvent(event);

            //讀取pom並收集成Map(projectName, version)
            if (pomVersionMap == null || pomVersionMap.isEmpty()) {
                pomVersionMap = fileService.getPomVersion(projectPath);
            }

            depGoupid = depGoupid.equals("") ? configService.getInputDependencyDialog() : depGoupid;
            if (!depGoupid.equals("") && !sameVersionTField.getText().equals("")) {
                samePomVersionEvent.onAction(singleModifyGrid, sameVersionTField.getText(), projectPath, pomVersionMap, depGoupid);
            }
        }
        if (((Button) event.getSource()).getId().equals("discardAllPom")) {
            projectPath.forEach(pjpath -> {
                if (gitService.exist(new File(pjpath.toString(), ".git"))) {
                    Repository repository = gitService.getRepository(pjpath.toString());
                    gitService.discard(repository, "pom.xml");
                }
            });
            singleModifyGrid.getChildren().clear();
            if (!baseFolder.equals("") && !depGoupid.equals("")) {
                mainPage.renderMain(singleModifyGrid, baseFolder, projectPath, originVersionMap, pomVersionMap, depGoupid);
            }
        }

    }

    @FXML
    protected void refreshMainPage(ActionEvent event) {
        singleModifyGrid.getChildren().clear();
        if (!baseFolder.equals("") && !depGoupid.equals("")) {
            mainPage.renderMain(singleModifyGrid, baseFolder, projectPath, originVersionMap, pomVersionMap, depGoupid);
        }

    }

    @FXML
    protected void clearMain(ActionEvent event) {
        singleModifyGrid.getChildren().clear();
    }

    @FXML
    protected void setBaseFolder(ActionEvent event) {
        DirectoryChooser chooser = fileService.initDirectoryChooser("請選擇專案的父層資料夾");
        File file = chooser.showDialog(stage);
        if (file != null && file.exists()) {
            this.baseFolder = fileService.getAbsolutePath(file);
            configService.setBaseFolderPath(baseFolder);
            if (depGoupid.equals("")) {
                depGoupid = configService.getInputDependencyDialog();
                setDepGoupid(depGoupid);
            }
            singleModifyGrid.getChildren().clear();
            if (!baseFolder.equals("") && !depGoupid.equals("")) {
                mainPage.renderMain(singleModifyGrid, baseFolder, projectPath, originVersionMap, pomVersionMap, depGoupid);
            }

        }
    }

    @FXML
    protected void setDepGroupId(ActionEvent event) {
        depGoupid = configService.getInputDependencyDialog();
        configService.setBaseDependency(depGoupid);
    }


    public StackPane getMainPane() {
        return mainPane;
    }


    public void setControllerStage(Stage stage) {
        this.stage = stage;
    }

    public void setProjectPath(List<Path> projectPath) {
        this.projectPath = projectPath;
    }

    public void setDepGoupid(String depGoupid) {
        this.depGoupid = depGoupid;
    }
}
