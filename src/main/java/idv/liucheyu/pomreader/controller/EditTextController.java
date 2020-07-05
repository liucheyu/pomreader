package idv.liucheyu.pomreader.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class EditTextController {

    @FXML
    private TextArea editArea;
    @FXML
    private Button editBtn;

    private Text mainPageVersionText;
    private String projectPath;
    private File mdFile;
    private MainController mainController;
    private Stage stage;

    private String editText;

    public void show() {

    }

    @FXML
    protected void confirmAction(ActionEvent event) {
        String content = editArea.getText();
        try (FileWriter writer = new FileWriter(getMdFile())) {
            writer.write(content);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Text originVersionText = mainController.getSingleModifyGrid().getChildren().stream()
                .filter(o -> o instanceof Node)
                .filter(o -> o.getId() != null)
                .filter(node -> node.getId().equals("originVersionText-" + mdFile.getParent().substring(mdFile.getParent().lastIndexOf("\\") + 1)))
                .map(nd -> (Text) nd).findFirst().get();
        try (FileReader fr = new FileReader(getMdFile());
             BufferedReader reader = new BufferedReader(fr)
        ) {
            String result = null;
            String version = "";
            List<String> versionList = new ArrayList<>();
            while ((result = reader.readLine()) != null) {
                if (result.contains("ver")) {
                    versionList.add(result.replace("ver", "").trim());
                }
            }
            if (versionList.size() > 0) {
                originVersionText.setText(versionList.get(0));
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.close();
    }

    public TextArea getEditArea() {
        return editArea;
    }

    public void setEditArea(TextArea editArea) {
        this.editArea = editArea;
    }

    public Button getEditBtn() {
        return editBtn;
    }

    public void setEditBtn(Button editBtn) {
        this.editBtn = editBtn;
    }

    public String getEditText() {
        return editText;
    }

    public void setEditText(String editText) {
        this.editText = editText;
    }

    public Text getMainPageVersionText() {
        return mainPageVersionText;
    }

    public void setMainPageVersionText(Text mainPageVersionText) {
        this.mainPageVersionText = mainPageVersionText;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public File getMdFile() {
        return mdFile;
    }

    public void setMdFile(File mdFile) {
        this.mdFile = mdFile;
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
