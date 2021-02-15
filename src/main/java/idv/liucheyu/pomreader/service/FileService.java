package idv.liucheyu.pomreader.service;

import idv.liucheyu.pomreader.model.Dependency;
import idv.liucheyu.pomreader.model.PomModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileService {

    public FileChooser initFileChooser(String titleName, String path) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(path));
        fileChooser.setTitle(titleName);
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Text Files", "*.md", "*.txt");
        fileChooser.getExtensionFilters().add(filter);
        return fileChooser;
    }

    public FileChooser initFileChooser(String titleName, String path, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(path));
        fileChooser.setTitle(titleName);
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Files", extensions);
        fileChooser.getExtensionFilters().add(filter);
        return fileChooser;
    }

    public DirectoryChooser initDirectoryChooser(String titleName) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(titleName);
        directoryChooser.setInitialDirectory(new File("C:\\"));
        return directoryChooser;
    }

//    public String getTextInputDialog(String contentText) {
//
//        return result;
//    }

    public String getAbsolutePath(File file) {
        String path = "";
        if (file != null) {
            path = file.getAbsolutePath();
        }
        return path;
    }

    public List<Path> getPomPath(Path parentPath) {
        List<Path> projectPath = new ArrayList<>();
        try {
            Stream<Path> stream = Files.list(parentPath);
            projectPath = stream
                    .filter(f -> !f.getFileName().toString().contains("."))
                    .collect(Collectors.toList());

            projectPath = projectPath.stream().filter(prp -> {
                boolean hasP = false;
                try {
                    hasP = Files.list(prp).anyMatch(f -> f.getFileName().toString().contains("pom.xml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return hasP;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return projectPath;
    }

    public Map<String, String> getLastReleaseVersion(List<Path> projectPath) {
        Map<String, String> verMap = new HashMap<>(projectPath.size());
        projectPath.forEach(pp -> {
            File releaseNote = new File(pp.toString() + "\\release-notes.md");
            if (releaseNote.exists()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(releaseNote), "UTF-8"))) {
                    StringBuilder builder = new StringBuilder();
                    String str = "";
                    while ((str = reader.readLine()) != null) {
                        if (str.contains("ver")) {
                            builder.append(str.trim());
                            builder.append(",");
                        }
                    }
                    verMap.put(pp.getFileName().toString(), builder.toString().split(",")[0].replace("ver", ""));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return verMap;
    }

    public Document getDocument(String path) {
        File file = new File(path);
        Document document = null;
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                SAXReader saxReader = new SAXReader();
                document = saxReader.read(fis);
            } catch (IOException | DocumentException ex) {
                ex.printStackTrace();
            }

        }

        return document;
    }

    public void writeAndSavePom(String path, String tagName, String value) {

        FileOutputStream out = null;
        OutputStreamWriter osw = null;
        XMLWriter writer = null;
        try {
            Document document = getDocument(path);
            Element ele = getElemet(document.getRootElement(), tagName);
            ele.setText(value);
            OutputFormat format = OutputFormat.createPrettyPrint();

            //writer = new XMLWriter(System.out, format);
            out = new FileOutputStream(path);
            osw = new OutputStreamWriter(out, "utf-8");
            writer = new XMLWriter(osw);
            writer.write(document);
            writer.flush();
            out.close();
            osw.close();
            writer.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                osw.close();
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    public void writeAndSavePomDepVersion(Path projectPath, Label text, String version, String depGroupid) {
        Document document = getDocument(projectPath.toString() + "\\pom.xml");
        Element depselement = getElemet(document.getRootElement(), "dependencies");
        Element projEle = depselement.elements().stream().filter(ele -> ele.elementText("groupId").equals(depGroupid)).findFirst().get();
        Element verEle = projEle.element("version");
        verEle.setText(version);
        savePom(document, projectPath.toString() + "\\pom.xml");
        text.setText(verEle.getText());

    }

    public void savePom(Document document, String path) {

        FileOutputStream out = null;
        OutputStreamWriter osw = null;
        XMLWriter writer = null;
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();

            //writer = new XMLWriter(System.out, format);
            out = new FileOutputStream(path);
            osw = new OutputStreamWriter(out, "utf-8");
            writer = new XMLWriter(osw);
            writer.write(document);
            writer.flush();
            out.close();
            osw.close();
            writer.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                osw.close();
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }


    public Element getRootElement(Document document) throws DocumentException {
        return document.getRootElement();
    }

    public Element getElemet(Element element, String elementName) {
        return element.element(elementName);
    }

    public Element getElemet(Element element, String... elementName) {
        Optional<Element> eleOp = Arrays.asList(elementName).stream().map(el -> {
            Element tempEle = null;
            if (Arrays.asList(elementName).indexOf(el) == 0) {
                tempEle = getElemet(element, el);
            }
            tempEle = getElemet(tempEle, el);
            return tempEle;
        }).findFirst();

        return eleOp.orElseThrow(() -> new NullPointerException());
    }

    public String getPomXmlVersion(String path, String subPath, String elementVersion) throws DocumentException {
        Document document = getDocument(path + subPath);
        Element rootElement = getRootElement(document);
        Element versionElement = getElemet(rootElement, elementVersion);
        return versionElement.getText();
    }


    public List<PomModel> getPomVersion(List<Path> projectPath, String depGroupId) {
        List<PomModel> pomList = new ArrayList<>();
        projectPath.forEach(pjp -> {
            //讀取每個檔案的xml檔
            PomModel pomModel = new PomModel();
            pomModel.setProjectName(pjp.getFileName().toString());
            pomModel.setProjectPath(pjp);
            Document document = getDocument(pjp.toString() + "/pom.xml");
            if (document != null) {
                Element rootElement = document.getRootElement();
                pomModel.setVersion(rootElement.elementText("version") == null ? "" : rootElement.elementText("version"));
                pomModel.setGroupId(rootElement.elementText("groupId") == null ? "" : rootElement.elementText("groupId"));
                pomModel.setArtifactId(rootElement.elementText("artifactId") == null ? "" : rootElement.elementText("artifactId"));
                if (rootElement.element("dependencies") != null) {
                    Element depsEl = rootElement.element("dependencies");
                    List<Element> deplist = depsEl.elements("dependency");
                    if (!deplist.isEmpty()) {
                        Optional<Element> opp = deplist.stream().filter(dep ->
                                dep.elementText("groupId").equals(depGroupId)
                        ).findFirst();
                        if (opp.isPresent()) {
                            Dependency dependency = new Dependency();
                            dependency.setArtifactId(opp.get().elementText("artifactId") == null ? "" : opp.get().elementText("artifactId"));
                            dependency.setGroupId(opp.get().elementText("groupId") == null ? "" : opp.get().elementText("groupId"));
                            dependency.setVersion(opp.get().elementText("version") == null ? "" : opp.get().elementText("version"));
                            pomModel.setDependency(dependency);
                        }
                    }
                }
                pomList.add(pomModel);
            }
        });
        return pomList;
    }

    public Map<String, String> getDependencyNameAndVersion(Path projectNamePath, String depGroupid) {
        Map<String, String> depMap = depMap = new HashMap<>(1);
        try {
            Document document = getDocument(projectNamePath.toString() + "\\pom.xml");
            Element rootElement = getRootElement(document);
            Element dependenciesElel = getElemet(rootElement, "dependencies");
            Optional<Element> dependencysOp = dependenciesElel.elements().stream()
                    .filter(ele -> ele.elementText("groupId").trim().equalsIgnoreCase(depGroupid))
                    .findFirst();

            if (dependencysOp.isPresent()) {
                Element depEle = dependencysOp.get();
                depMap.put("dependency", depEle.elementText("artifactId"));
                depMap.put("groupId", depEle.elementText("groupId"));
                depMap.put("version", depEle.elementText("version"));
            }
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
        return depMap;
    }

    public Optional<String> getMessageDialog() {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("message");
        textInputDialog.setHeaderText("請輸入message, 預設為none message");
        textInputDialog.setContentText("message:");
        return textInputDialog.showAndWait();
    }

    public Stage getGitAddOption(List<String> fileNames, List<String> selectString) {
        Stage stage = new Stage();
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.TOP_LEFT);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        fileNames.forEach(fn -> {
            CheckBox checkBox = new CheckBox(fn);
            vBox.getChildren().add(checkBox);
        });
        Button confirmBtn = new Button("確認");

        confirmBtn.setOnAction(event -> {
            List<String> slist = vBox.getChildren().stream().filter(n -> n instanceof CheckBox)
                    .map(n2 -> (CheckBox) n2)
                    .filter(n3 -> n3.isSelected())
                    .map(n4 -> n4.getText()).collect(Collectors.toList());
            selectString.removeAll(vBox.getChildren().stream().filter(n -> n instanceof CheckBox)
                    .map(n2 -> (CheckBox) n2)
                    .map(n4 -> n4.getText()).collect(Collectors.toList()));
            selectString.addAll(slist);
            stage.hide();
        });

        vBox.getChildren().add(confirmBtn);
        Scene scene = new Scene(vBox, 600, 600);

        stage.setScene(scene);
        stage.setTitle("請選擇git要操作add的檔案");
        stage.show();
        return stage;
    }

    public Stage getGitDiscardOption(List<String> fileNames, List<String> selectString) {
        Stage stage = new Stage();
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.TOP_LEFT);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        fileNames.forEach(fn -> {
            CheckBox checkBox = new CheckBox(fn);
            vBox.getChildren().add(checkBox);
        });
        Button confirmBtn = new Button("確認");

        confirmBtn.setOnAction(event -> {
            List<String> slist = vBox.getChildren().stream().filter(n -> n instanceof CheckBox)
                    .map(n2 -> (CheckBox) n2)
                    .filter(n3 -> n3.isSelected())
                    .map(n4 -> n4.getText()).collect(Collectors.toList());
            selectString.removeAll(vBox.getChildren().stream().filter(n -> n instanceof CheckBox)
                    .map(n2 -> (CheckBox) n2)
                    .map(n4 -> n4.getText()).collect(Collectors.toList()));
            selectString.addAll(slist);
            stage.hide();
        });

        vBox.getChildren().add(confirmBtn);
        Scene scene = new Scene(vBox, 600, 600);

        stage.setScene(scene);
        stage.setTitle("請選擇git要操作Discard的檔案");
        stage.show();
        return stage;
    }
}
