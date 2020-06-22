package idv.liucheyu.pomreader.event;

import idv.liucheyu.pomreader.service.ConfigService;
import idv.liucheyu.pomreader.service.FileService;
import idv.liucheyu.pomreader.service.GitService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.dom4j.Document;
import org.eclipse.jgit.lib.Repository;

import java.nio.file.Path;
import java.util.Iterator;


public class PomButtonOnClickEvent implements EventHandler<ActionEvent> {

    private GitService gitService = new GitService();
    private FileService fileService = new FileService();
    private ConfigService configService = new ConfigService();
    private Path projectPath;
    private GridPane pane;

    public PomButtonOnClickEvent(Path projectPath, GridPane pane) {
        this.projectPath = projectPath;
        this.pane = pane;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        if (btn.getId().equals("discardPom")) {
            Repository repository = gitService.getRepository(projectPath.toString());
            gitService.discard(repository, "pom.xml");

            Document document = fileService.getDocument(projectPath.toString() + "\\pom.xml");
            String pomVersion = fileService.getElemet(document.getRootElement(), "version").getText();

            String depGroupid = configService.getConfigFolderByFile().get("dependency");
            String depVersion = fileService.getDependencyNameAndVersion(projectPath, depGroupid).get("version");

            Iterator<Node> iterator = pane.getChildren().iterator();

            while (iterator.hasNext()) {
                Node node = iterator.next();
                if (node instanceof Text) {
                    if (node.getId() != null && node.getId().equals(projectPath.getFileName().toString() + "-pomVersionText")) {
                        ((Text) node).setText(pomVersion);
                    }
                    if (node.getId() != null && node.getId().equals(projectPath.getFileName().toString() + "-depVersionText")) {
                        ((Text) node).setText(depVersion);
                    }
                }

            }

        }
        if (btn.getId().equals("commitPom")) {
            Repository repository = gitService.getRepository(projectPath.toString());
            gitService.commit(repository, "pom.xml");
        }
    }
}
