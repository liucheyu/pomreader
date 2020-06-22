package idv.liucheyu.pomreader.event;

import idv.liucheyu.pomreader.service.FileService;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.dom4j.Document;
import org.dom4j.Element;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SamePomVersionEvent {

    private FileService fileService = new FileService();
    private ActionEvent actionEvent;

    public SamePomVersionEvent(ActionEvent actionEvent) {
        this.actionEvent = actionEvent;
    }

    public void onAction(GridPane pane, String version, List<Path> projectPaths, Map<String, String> pomVersionMap, String depGroupid) {
        projectPaths.forEach(projPath -> {
            String projectName = projPath.getFileName().toString();
            if (pomVersionMap.get(projectName) != null && !pomVersionMap.get(projectName).equals("")) {

                fileService.writeAndSavePom(projPath.toString() + "\\pom.xml", "version", version);
                Document document = fileService.getDocument(projPath.toString() + "\\pom.xml");
                Element versionElement = fileService.getElemet(document.getRootElement(), "version");
                Iterator<Node> iterator = pane.getChildren().iterator();
                while (iterator.hasNext()) {
                    Node node = iterator.next();
                    if (node instanceof Text) {
                        if (node.getId() != null && node.getId().equals(projectName + "-pomVersionText")) {
                            ((Text) node).setText(versionElement.getText());
                        }
                        if (node.getId() != null && node.getId().equals(projectName + "-depVersionText")) {
                            fileService.writeAndSavePomDepVersion(projPath, ((Text) node), version, depGroupid);
                        }
                    }

                }


            }
        });
    }

}
