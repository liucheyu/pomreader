package idv.liucheyu.pomreader.event;

import idv.liucheyu.pomreader.service.FileService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.dom4j.Document;
import org.dom4j.Element;

import java.nio.file.Path;

public class UpdatePomVersionEvent implements EventHandler<ActionEvent> {
    private FileService fileService = new FileService();

    private Label text;
    private TextField textField;
    private Path projectPath;
    private String depGoupid;

    public static UpdatePomVersionEvent getInstance() {
        return new UpdatePomVersionEvent();
    }

    private UpdatePomVersionEvent() {
    }

    private UpdatePomVersionEvent(Label text, Path projectPath, TextField textField) {
        this.text = text;
        this.textField = textField;
        this.projectPath = projectPath;
    }

    private UpdatePomVersionEvent(Label text, Path projectPath, String dependentName, TextField textField) {
        this.text = text;
        this.textField = textField;
        this.projectPath = projectPath;
        this.depGoupid = dependentName;
    }

    public UpdatePomVersionEvent updateIdvPomVersion(Label text, Path projectPath, TextField textField) {
        return new UpdatePomVersionEvent(text, projectPath, textField);
    }

    public UpdatePomVersionEvent updateDepPomVersion(Label text, Path projectPath, String dependentName, TextField textField) {
        return new UpdatePomVersionEvent(text, projectPath, dependentName,  textField);
    }


    @Override
    public void handle(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        String id = button.getId();
        if (id.equals("pomVersionButton")) {

            fileService.writeAndSavePom(projectPath.toString() + "\\pom.xml", "version", textField.getText());
            Document document = fileService.getDocument(projectPath.toString() + "\\pom.xml");
            Element element = fileService.getElemet(document.getRootElement(), "version");
            text.setText(element.getText());

        }

        if (id.equals("depVersionButton")) {
            fileService.writeAndSavePomDepVersion(projectPath, text, textField.getText(), depGoupid);
        }
    }
}
