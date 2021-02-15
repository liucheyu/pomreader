package idv.liucheyu.pomreader.event;

import idv.liucheyu.pomreader.model.PomModel;
import idv.liucheyu.pomreader.service.FileService;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.List;
import java.util.stream.Collectors;

public class SamePomVersionEvent {

    private FileService fileService = new FileService();
    private ActionEvent actionEvent;

    public SamePomVersionEvent(ActionEvent actionEvent) {
        this.actionEvent = actionEvent;
    }

    public void onAction(GridPane pane, String version, List<PomModel> pomModels) {
        pomModels.forEach(pomModel -> {
            String projectName = pomModel.getProjectName();

            fileService.writeAndSavePom(pomModel.getProjectPath() + "\\pom.xml", "version", version);
            Document document = fileService.getDocument(pomModel.getProjectPath() + "\\pom.xml");
            Element versionElement = fileService.getElemet(document.getRootElement(), "version");
            List<Label> textList = pane.getChildren().stream().filter(n -> n instanceof Label).map(n2 -> (Label) n2).collect(Collectors.toList());
            textList.forEach(node -> {
                if (node.getId() != null && node.getId().equals(projectName + "-pomVersionText")) {
                    node.setText(versionElement.getText());
                }
                if (node.getId() != null && node.getId().equals(projectName + "-depVersionText")) {
                    fileService.writeAndSavePomDepVersion(pomModel.getProjectPath(), node, version, pomModel.getDependency().getGroupId());
                }
            });

        });
    }

}
