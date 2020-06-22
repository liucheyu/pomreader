package idv.liucheyu.pomreader.event;

import idv.liucheyu.pomreader.service.FileService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuOnClickEvent implements EventHandler<ActionEvent> {
    private FileService fileService = new FileService();
    private Stage stage;
    private GridPane pane;
    List<Path> projectPath;
    private String depGoupid = "";

    public MenuOnClickEvent(Stage stage, GridPane pane, List<Path> projectPat, String depGoupid) {
        this.stage = stage;
        this.pane = pane;
        this.projectPath = projectPath;
        this.depGoupid= depGoupid;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        MenuItem menuItem = (MenuItem) actionEvent.getSource();
            String menuItemName = menuItem.getText();
            if ("匯入".equals(menuItemName)) {

                DirectoryChooser chooser = fileService.initDirectoryChooser("請選擇專案的父層資料夾");
                File file = chooser.showDialog(stage);
                String folderPath = fileService.getAbsolutePath(file);
                projectPath = fileService.getPomPath(Paths.get(folderPath));

                pane.setHgap(projectPath.size());
                Label batchVersionLabel = new Label("全部專案設為同一版號：");
                pane.add(batchVersionLabel, 0, 0);
                TextField sameVersionField = new TextField();
                pane.add(sameVersionField, 1, 0);
                Button batchUpdateButton = new Button("確定");
                batchUpdateButton.setId("batchUpdateButton");
                pane.add(batchUpdateButton, 2, 0);

                Label seperateLabel = new Label("各別更改：");
                pane.add(seperateLabel, 0, 1);
                Label originLabel = new Label("Release Note版號");
                pane.add(originLabel, 1, 1);
                Label pomLabel = new Label("POM版號");
                pane.add(pomLabel, 2, 1);
                Label modiyLabel = new Label("修改POM版本");
                pane.add(modiyLabel, 3, 1);
                Label conFirmLabel = new Label("確認修改");
                pane.add(conFirmLabel, 4, 1);
                Label plusOneLabel = new Label("選定的專案進一版(依賴會)");
                pane.add(plusOneLabel, 5, 1);

                //讀取releaseNote的版號,並加為Map(projectName, version)
                Map<String, String> originVersionMap = fileService.getLastReleaseVersion(projectPath);
                //讀取pom並收集成Map(projectName, version)
                Map<String, String> pomVersionMap = fileService.getPomVersion(projectPath);
                int rowIndex = 2;
                for (int i = 0; i < projectPath.size(); i ++) {
                    String pjName = projectPath.get(i).getFileName().toString();
                    Label pjLabel = new Label(pjName);
                    rowIndex ++;
                    pane.add(pjLabel, 0, rowIndex);
                    Label originVersionLabel = new Label(originVersionMap.get(pjName));
                    pane.add(originVersionLabel, 1, rowIndex);
                    Label pomVersionLabel = new Label(pomVersionMap.get(pjName));
                    pane.add(pomVersionLabel, 2, rowIndex);
                    TextField modifyField = new TextField();
                    pane.add(modifyField, 3, rowIndex);
                    Button modifyButton = new Button("確定");
                    modifyButton.setId("modifyButton");
                    pane.add(modifyButton, 4, rowIndex);

                    Map<String, String> depMap = new HashMap<>(2);
                    //依賴專案
                    depMap = fileService.getDependencyNameAndVersion(projectPath.get(i), depGoupid);
                    rowIndex ++;
                    if(!depMap.isEmpty()) {
                        Label dependencyLabel = new Label("  依賴|-" + depMap.get("dependency").split("/")[1]);
                        pane.add(dependencyLabel, 0, rowIndex);

                        Label depVersion = new Label(depMap.get("version"));
                        pane.add(depVersion, 2, rowIndex);
                        TextField modifyDepField = new TextField();
                        pane.add(modifyDepField, 3, rowIndex);
                        Button modifyDepButton = new Button("確定");
                        modifyDepButton.setId("modifyDepButton");
                        pane.add(modifyDepButton, 4, rowIndex);
                    }

                }


            }
    }
}
