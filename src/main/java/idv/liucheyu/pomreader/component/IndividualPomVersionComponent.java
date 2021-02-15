package idv.liucheyu.pomreader.component;

import idv.liucheyu.pomreader.controller.MainController;
import idv.liucheyu.pomreader.event.PomButtonOnClickEvent;
import idv.liucheyu.pomreader.event.UpdatePomVersionEvent;
import idv.liucheyu.pomreader.model.PomModel;
import idv.liucheyu.pomreader.service.ConfigService;
import idv.liucheyu.pomreader.service.FileService;
import idv.liucheyu.pomreader.service.GitService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class IndividualPomVersionComponent implements BaseComponent {

    private FileService fileService = new FileService();
    private ConfigService configService = new ConfigService();
    UpdatePomVersionEvent updatePomVersionEvent = UpdatePomVersionEvent.getInstance();
    private GridPane pane;
    private List<Path> projectPath;
    private Map<String, String> originVersionMap;
    private GitService gitService = new GitService();
    private List<PomModel> pomModels;
    private MainController controller;

    public IndividualPomVersionComponent(MainController mainController, List<Path> projectPath, Map<String, String> originVersionMap, List<PomModel> pomModels) {
        this.controller = mainController;
        this.pane = mainController.getSingleModifyGrid();
        this.projectPath = projectPath;
        this.originVersionMap = originVersionMap;
        this.pomModels = pomModels;
    }


    @Override
    public void render() {

        int rowIndex = 0;
        for (int i = 0; i < pomModels.size(); i++) {

           PomModel pomModel = pomModels.get(i);
            String pjName = pomModel.getProjectName();
            Label pjLabel = new Label(pjName);
            rowIndex++;
            pane.add(pjLabel, 0, rowIndex);
            Label originVersionText = new Label(originVersionMap.get(pjName));
            pane.add(originVersionText, 1, rowIndex);
            originVersionText.setId("originVersionText-" + pjName);
            ContextMenu contextMenu = new ContextMenu();
            MenuItem menuItem1 = new MenuItem("複製");
            contextMenu.getItems().add(menuItem1);
            originVersionText.setContextMenu(contextMenu);
            menuItem1.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent clipboardContent = new ClipboardContent();
                    clipboardContent.putString(originVersionText.getText());
                    clipboard.setContent(clipboardContent);
                }
            });

            Button editDocBtn = new Button("編輯");
            editDocBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    configService.getEditDocDialog(controller, pomModel.getProjectPath());
                }
            });
            pane.add(editDocBtn, 2, rowIndex);
            Label pomVersionText = new Label(pomModel.getVersion());
            pane.add(pomVersionText, 3, rowIndex);
            pomVersionText.setId(pjName + "-pomVersionText");
            ContextMenu contextMenu2 = new ContextMenu();
            MenuItem menuItem2 = new MenuItem("複製");
            contextMenu2.getItems().add(menuItem2);
            pomVersionText.setContextMenu(contextMenu2);
            menuItem2.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent clipboardContent = new ClipboardContent();
                    clipboardContent.putString(pomVersionText.getText());
                    clipboard.setContent(clipboardContent);
                }
            });

            TextField modifyField = new TextField();
            pane.add(modifyField, 4, rowIndex);

            Button pomVersionButton = new Button("確定");
            pomVersionButton.setOnAction(updatePomVersionEvent.updateIdvPomVersion(pomVersionText, pomModel.getProjectPath(), modifyField));
            pomVersionButton.setId("pomVersionButton");
            pane.add(pomVersionButton, 5, rowIndex);

            if(gitService.exist(pomModel.getProjectPath().toString())){
                HBox hBox = new HBox();
                hBox.setSpacing(10.0);
                VBox vBox = new VBox();
                Button discardPomBtn = new Button("Discar列表");

                discardPomBtn.setId("discardPom");
                discardPomBtn.setOnAction(new PomButtonOnClickEvent(pomModel.getProjectPath(), pane, pomModel.getDependency().getGroupId()));
                Button commitOp = new Button("Add&Commit");
                commitOp.setId("commitOp");
                commitOp.setOnAction(new PomButtonOnClickEvent(pomModel.getProjectPath(), pane, pomModel.getDependency().getGroupId()));
                vBox.getChildren().add(discardPomBtn);
                vBox.getChildren().add(commitOp);
                vBox.setSpacing(10.0);
                discardPomBtn.setMaxWidth(Double.MAX_VALUE);
                commitOp.setMaxWidth(Double.MAX_VALUE);
                vBox.setFillWidth(true);

                VBox vBox2 = new VBox();
                vBox2.setSpacing(10.0);
                Button commitPj = new Button("commit專案");
                commitPj.setId("commitPj");
                commitPj.setOnAction(new PomButtonOnClickEvent(pomModel.getProjectPath(), pane, pomModel.getDependency().getGroupId()));
                Button gitStatusBtn = new Button("status");
                gitStatusBtn.setId("gitStausBtn");
                gitStatusBtn.setOnAction(new PomButtonOnClickEvent(pomModel.getProjectPath(), pane, pomModel.getDependency().getGroupId()));
                vBox2.getChildren().add(commitPj);
                vBox2.getChildren().add(gitStatusBtn);
                commitPj.setMaxWidth(Double.MAX_VALUE);
                gitStatusBtn.setMaxWidth(Double.MAX_VALUE);
                vBox2.setFillWidth(true);

                hBox.getChildren().add(vBox);
                hBox.getChildren().add(vBox2);

                pane.add(hBox, 6, rowIndex, 1, 2);
            }

            //依賴專案
            rowIndex++;
            if(pomModel.getDependency() != null){
                if (pomModel.getDependency().getGroupId() != null && !pomModel.getDependency().getGroupId().equals("")) {
                    Label dependencyLabel = new Label("  依賴|-" + pomModel.getDependency().getArtifactId());
                    pane.add(dependencyLabel, 0, rowIndex);

                    Label depVersion = new Label(pomModel.getDependency().getVersion());
                    depVersion.setId(pjName + "-depVersionText");
                    pane.add(depVersion, 3, rowIndex);

                    ContextMenu contextMenu3 = new ContextMenu();
                    MenuItem menuItem3 = new MenuItem("複製");
                    contextMenu3.getItems().add(menuItem3);
                    depVersion.setContextMenu(contextMenu3);
                    menuItem3.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            Clipboard clipboard = Clipboard.getSystemClipboard();
                            ClipboardContent clipboardContent = new ClipboardContent();
                            clipboardContent.putString(depVersion.getText());
                            clipboard.setContent(clipboardContent);
                        }
                    });


                    TextField modifyDepField = new TextField();
                    pane.add(modifyDepField, 4, rowIndex);
                    Button depVersionButton = new Button("確定");
                    depVersionButton.setId("depVersionButton");
                    depVersionButton.setOnAction(updatePomVersionEvent.updateDepPomVersion(depVersion, projectPath.get(i), pomModel.getDependency().getGroupId(), modifyDepField));
                    pane.add(depVersionButton, 5, rowIndex);

                }
            }

            rowIndex++;
            Separator separator = new Separator();
            separator.setOrientation(Orientation.HORIZONTAL);
            pane.add(separator, 0, rowIndex, 7, 1);

        }

    }


}