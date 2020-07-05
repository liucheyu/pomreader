package idv.liucheyu.pomreader.event;

import idv.liucheyu.pomreader.model.PomModel;
import idv.liucheyu.pomreader.service.FileService;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.Iterator;
import java.util.List;

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
                Iterator<Node> iterator = pane.getChildren().iterator();
                while (iterator.hasNext()) {
                    Node node = iterator.next();
                    if (node instanceof Text) {
                        if (node.getId() != null && node.getId().equals(projectName + "-pomVersionText")) {
                            ((Text) node).setText(versionElement.getText());
                        }
                        if (node.getId() != null && node.getId().equals(projectName + "-depVersionText")) {
                            fileService.writeAndSavePomDepVersion(pomModel.getProjectPath(), ((Label) node), version, pomModel.getDependency().getGroupId());
                        }
                    }

            }
        });
    }

}
