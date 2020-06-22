package idv.liucheyu.pomreader.component;

import idv.liucheyu.pomreader.event.PomButtonOnClickEvent;
import idv.liucheyu.pomreader.event.UpdatePomVersionEvent;
import idv.liucheyu.pomreader.service.FileService;
import idv.liucheyu.pomreader.service.GitService;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndividualPomVersionComponent implements BaseComponent {

    private FileService fileService = new FileService();
    UpdatePomVersionEvent updatePomVersionEvent = UpdatePomVersionEvent.getInstance();
    private GridPane pane;
    private List<Path> projectPath;
    private Map<String, String> originVersionMap;
    private Map<String, String> pomVersionMap;
    private String depGoupid = "";
    private GitService gitService = new GitService();

    public IndividualPomVersionComponent(GridPane pane, List<Path> projectPath, Map<String, String> originVersionMap, Map<String, String> pomVersionMap, String depGoupid) {
        this.pane = pane;
        this.projectPath = projectPath;
        this.originVersionMap = originVersionMap;
        this.pomVersionMap = pomVersionMap;
        this.depGoupid = depGoupid;
    }


    @Override
    public void render() {

        int rowIndex = 0;
        for (int i = 0; i < projectPath.size(); i++) {
            String pjName = projectPath.get(i).getFileName().toString();
            Label pjLabel = new Label(pjName);
            rowIndex++;
            pane.add(pjLabel, 0, rowIndex);
            Text originVersionText = new Text(originVersionMap.get(pjName));
            pane.add(originVersionText, 1, rowIndex);
            Text pomVersionText = new Text(pomVersionMap.get(pjName));
            pane.add(pomVersionText, 2, rowIndex);
            pomVersionText.setId(pjName + "-pomVersionText");
            TextField modifyField = new TextField();
            pane.add(modifyField, 3, rowIndex);

            Button pomVersionButton = new Button("確定");
            pomVersionButton.setOnAction(updatePomVersionEvent.updateIdvPomVersion(pomVersionText, projectPath.get(i), modifyField));
            pomVersionButton.setId("pomVersionButton");
            pane.add(pomVersionButton, 4, rowIndex);

            if(gitService.exist(new File(projectPath.get(i).toString(), ".git"))){
                HBox hBox = new HBox();
                Button discardPomBtn = new Button("Discard Pom");
                discardPomBtn.setId("discardPom");
                discardPomBtn.setOnAction(new PomButtonOnClickEvent(projectPath.get(i), pane));
                Button commitPomBtn = new Button("Commit Pom");
                commitPomBtn.setId("commitPom");
                commitPomBtn.setOnAction(new PomButtonOnClickEvent(projectPath.get(i), pane));
                hBox.getChildren().add(discardPomBtn);
                hBox.getChildren().add(commitPomBtn);
                hBox.setSpacing(10.0);
                pane.add(hBox, 5, rowIndex, 1, 2);
            }

            Map<String, String> depMap = new HashMap<>(2);
            //依賴專案
            depMap = fileService.getDependencyNameAndVersion(projectPath.get(i), depGoupid);
            rowIndex++;
            if (!depMap.isEmpty()) {
                Label dependencyLabel = new Label("  依賴|-" + depMap.get("dependency"));
                pane.add(dependencyLabel, 0, rowIndex);

                Text depVersion = new Text(depMap.get("version"));
                depVersion.setId(pjName + "-depVersionText");
                pane.add(depVersion, 2, rowIndex);
                TextField modifyDepField = new TextField();
                pane.add(modifyDepField, 3, rowIndex);
                Button depVersionButton = new Button("確定");
                depVersionButton.setId("depVersionButton");
                depVersionButton.setOnAction(updatePomVersionEvent.updateDepPomVersion(depVersion, projectPath.get(i), depMap.get("groupId"), modifyDepField));
                pane.add(depVersionButton, 4, rowIndex);

            }

            rowIndex++;
            Separator separator = new Separator();
            separator.setOrientation(Orientation.HORIZONTAL);
            pane.add(separator, 0, rowIndex, 6, 1);
        }


    }


}