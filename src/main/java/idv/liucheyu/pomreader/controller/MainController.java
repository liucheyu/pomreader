package idv.liucheyu.pomreader.controller;

import idv.liucheyu.pomreader.component.IndividualPomVersionComponent;
import idv.liucheyu.pomreader.component.PageSetting;
import idv.liucheyu.pomreader.event.SamePomVersionEvent;
import idv.liucheyu.pomreader.model.ConfigModel;
import idv.liucheyu.pomreader.model.PomModel;
import idv.liucheyu.pomreader.service.ConfigService;
import idv.liucheyu.pomreader.service.FileService;
import idv.liucheyu.pomreader.service.GitService;
import idv.liucheyu.pomreader.service.MavenCmdService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @FXML
    private VBox mavenPage;

    @FXML
    private VBox choiceParent;
    @FXML
    private Button choiceAdd;

    private FileService fileService = new FileService();
    private ConfigService configService = new ConfigService();
    private PageSetting pageSetting = new PageSetting();
    private MavenCmdService mavenCmdService = new MavenCmdService();

    private Stage stage;
    private List<Path> projectPath;
    private IndividualPomVersionComponent idvComponent;
    private List<Text> pomVersionText = new ArrayList<>();
    private PageSetting mainPage;

    private Map<String, String> originVersionMap;
    private Map<String, String> pomVersionMap;
    private GitService gitService = new GitService();
    private ConfigModel configModel;
    private Application application;


    @FXML
    protected void samePomVersionEvent(ActionEvent event) {
        if (configModel == null) {
            Optional<ConfigModel> op = configService.getConfigFolderByFile(stage);
            if (op.isPresent()) {
                configModel = op.get();
            }
        }

        if (configModel.getBaseFolder().equals("")) {
            configModel.setBaseFolder(configService.getBaseFolderDialog(stage));
        }

        if (configModel.getDependencyGroupId().equals("")) {
            configModel.setDependencyGroupId(configService.getInputDependencyDialog());
        }

        if (!configModel.getBaseFolder().equals("")) {
            projectPath = fileService.getPomPath(Paths.get(configModel.getBaseFolder()));
        }


        if (((Button) event.getSource()).getId().equals("sameVersionButton")) {
            SamePomVersionEvent samePomVersionEvent = new SamePomVersionEvent(event);

            //讀取pom並收集
            List<PomModel> pomModels = new ArrayList<>();
            if (!configModel.getDependencyGroupId().equals("")) {
                pomModels = fileService.getPomVersion(projectPath, configModel.getDependencyGroupId());
            }

            if (!pomModels.isEmpty() && !sameVersionTField.getText().equals("")) {
                samePomVersionEvent.onAction(singleModifyGrid, sameVersionTField.getText(), pomModels);
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

            if (!configModel.getDependencyGroupId().equals("") && !configModel.getBaseFolder().equals("")) {
                pageSetting.setUpMainPage(configModel, stage, this);
            }
        }

    }

    @FXML
    protected void refreshMainPage(ActionEvent event) {
        singleModifyGrid.getChildren().clear();
        Optional<ConfigModel> configOp = Optional.empty();
        if (configModel == null) {
            configOp = configService.getConfigFolderByFile(stage);
            if (configOp.isPresent()) {
                configModel = configOp.get();
            }
        }

        if (configModel != null) {
            if (configModel.getDependencyGroupId().equals("")) {
                configModel.setDependencyGroupId(configService.getProperty("dependency"));
                if(configModel.getDependencyGroupId().equals("")){
                    configModel.setDependencyGroupId(configService.getInputDependencyDialog());
                }

            }
            if (configModel.getBaseFolder().equals("")) {
                configModel.setBaseFolder(configService.getProperty("baseFolder"));
                if (configModel.getBaseFolder().equals("")){
                    configModel.setBaseFolder(configService.getBaseFolderDialog(stage));
                }
            }
            if (!configModel.getBaseFolder().equals("") && !configModel.getDependencyGroupId().equals("")) {
                pageSetting.setUpMainPage(configModel, stage, this);
            }
            choiceParent.getChildren().removeIf(cp -> cp.getId().contains("choiceBox"));
        }

    }

    @FXML
    protected void clearMain(ActionEvent event) {
        singleModifyGrid.getChildren().clear();
        //            List<ComboBox> comboBox = choiceParent.getChildren().stream().filter(cp -> cp.getId().contains("choiceBox")).map(cp2 -> (ComboBox)cp2).collect(Collectors.toList());
        choiceParent.getChildren().removeIf(cp -> cp.getId().contains("choiceBox"));
    }

    @FXML
    protected void setBaseFolder(ActionEvent event) {
        singleModifyGrid.getChildren().clear();
        configModel.setBaseFolder(configService.getBaseFolderDialog(stage));
        configModel.setDependencyGroupId(configService.getInputDependencyDialog());

        if (!configModel.getBaseFolder().equals("") && !configModel.getDependencyGroupId().equals("")) {
            pageSetting.setUpMainPage(configModel, stage, this);
        }


    }

    @FXML
    protected void setDepGroupId(ActionEvent event) {
        if (configModel == null) {
            configModel = new ConfigModel();
        }
        configModel.setDependencyGroupId(configService.getInputDependencyDialog());
    }

    @FXML
    protected void setMaenDir(ActionEvent event) {
        if (configModel == null) {
            configModel = new ConfigModel();
        }
        configModel.setMavenDir(configService.getMavenDirDialog(stage));
        mavenPage.setDisable(true);
        if(!configModel.getMavenDir().equals("")){
            File file = new File(configModel.getMavenDir());
            if(file.exists()){
                FilenameFilter filter = new FilenameFilter() {
                    @Override
                    public boolean accept(File file, String s) {
                        return s.contains("mvn");
                    }
                };
                if(file.list(filter).length > 0){
                    mavenPage.setDisable(false);
                }
            }

        }
    }


    @FXML
    protected void choiceAddAction() {

        if (configModel == null) {
            Optional<ConfigModel> op = configService.getConfigFolderByFile(stage);
            if (op.isPresent()) {
                configModel = op.get();
            }
        }
        if (!configModel.getBaseFolder().equals("")) {
            if (projectPath == null) {
                projectPath = fileService.getPomPath(Paths.get(configModel.getBaseFolder()));
            }

            List<String> prpPath = projectPath.stream().map(pj -> pj.toString()).collect(Collectors.toList());
            ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableList(prpPath));
            List<ComboBox<String>> comboBoxList = choiceParent.getChildren().stream().filter(cp -> cp instanceof ComboBox).map(cp -> (ComboBox<String>) cp).collect(Collectors.toList());
            int num = 1;
            comboBox.setId("choiceBox" + num);
            if (!comboBoxList.isEmpty()) {
                num = Integer.parseInt(comboBoxList.get(comboBoxList.size() - 1).getId().replace("choiceBox", ""));
                comboBox.setId("choiceBox" + (num + 1));
            }

            choiceParent.getChildren().add(comboBox);
        }

    }

    @FXML
    protected void cleanInstall(ActionEvent event) {
        System.out.println("clean install start");
        if (projectPath == null || projectPath.isEmpty()) {
            projectPath = fileService.getPomPath(Paths.get(configModel.getBaseFolder()));

        }

        List<ComboBox<String>> comboBoxList = choiceParent.getChildren().stream().filter(cp -> cp instanceof ComboBox).map(cp -> (ComboBox<String>) cp).collect(Collectors.toList());
        if (!comboBoxList.isEmpty()) {
            List<SingleSelectionModel> modles = comboBoxList.stream().map(mb -> mb.getSelectionModel()).collect(Collectors.toList());
            List<String> projectPaths = modles.stream().map(ssm -> String.valueOf(ssm.getSelectedItem()).trim()).collect(Collectors.toList());
            projectPaths = projectPaths.stream().filter(pp -> !pp.equals("null")).collect(Collectors.toList());
            if(!projectPaths.isEmpty()){
                if(configModel == null){
                    configModel = new ConfigModel();
                }
                if(configModel.getMavenDir() ==null || configModel.getMavenDir().equals("")){
                    configModel.setMavenDir(configService.getMavenDirDialog(stage));
                }
                if(configModel.getMavenSetting() == null || configModel.getMavenSetting().equals("")){
                    configModel.setMavenSetting(configService.getMavenSettingDialog(stage));
                }
                if(!configModel.getMavenDir().equals("")){
                    mavenCmdService.CleanInstall(projectPaths, configModel.getMavenDir(), configModel.getMavenSetting());
                }
            }

        }
    }

    @FXML
    protected void cleanDeploy() {
        System.out.println("clean deploy start");
        if (projectPath == null || projectPath.isEmpty()) {
            projectPath = fileService.getPomPath(Paths.get(configModel.getBaseFolder()));

        }

        List<ComboBox<String>> comboBoxList = choiceParent.getChildren().stream().filter(cp -> cp instanceof ComboBox).map(cp -> (ComboBox<String>) cp).collect(Collectors.toList());
        if (!comboBoxList.isEmpty()) {
            List<SingleSelectionModel> modes = comboBoxList.stream().map(mb -> mb.getSelectionModel()).collect(Collectors.toList());
            List<String> projectPaths = modes.stream().map(ssm -> String.valueOf(ssm.getSelectedItem()).trim()).collect(Collectors.toList());
            projectPaths.removeIf(pp -> pp.equals(""));
            if(configModel == null){
                configModel = new ConfigModel();
            }
            if(configModel.getMavenDir() ==null || configModel.getMavenDir().equals("")){
                configModel.setMavenDir(configService.getProperty("mavenDir"));
                if(configModel.getMavenDir().equals("")){
                    configModel.setMavenDir(configService.getMavenDirDialog(stage));
                }
            }
            if(configModel.getMavenSetting() == null || configModel.getMavenSetting().equals("")){
                configModel.setMavenSetting(configService.getProperty("mavenSetting"));
                if(configModel.getMavenSetting().equals("")){
                    configModel.setMavenSetting(configService.getMavenSettingDialog(stage));
                }
            }
            if(!configModel.getMavenDir().equals("")){
                mavenCmdService.CleanDeploy(projectPaths, configModel.getMavenDir(), configModel.getMavenSetting());
            }

        }
    }

    @FXML
    protected void setMaenSetting(ActionEvent event){
        mavenPage.setDisable(true);
        String mavenSettingDir = configService.getMavenSettingDialog(stage);
        configService.setMavenSetting(mavenSettingDir);
        if(mavenSettingDir!=null && !mavenSettingDir.equals("")){
            mavenPage.setDisable(false);
        }
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


    public GridPane getSingleModifyGrid() {
        return singleModifyGrid;
    }

    public void setSingleModifyGrid(GridPane singleModifyGrid) {
        this.singleModifyGrid = singleModifyGrid;
    }

    public ConfigModel getConfigModel() {
        return configModel;
    }

    public void setConfigModel(ConfigModel configModel) {
        this.configModel = configModel;
    }

    public VBox getMavenPage() {
        return mavenPage;
    }

    public void setMavenPage(VBox mavenPage) {
        this.mavenPage = mavenPage;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
