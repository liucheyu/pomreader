package idv.liucheyu.pomreader.service;

import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
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

    public FileChooser initFileChooser(String titleName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\"));
        fileChooser.setTitle(titleName);
        return fileChooser;
    }

    public DirectoryChooser initDirectoryChooser(String titleName) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(titleName);
        directoryChooser.setInitialDirectory(new File("C:\\"));
        return directoryChooser;
    }

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
            try (FileInputStream fis = new FileInputStream(path)) {
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

    public void writeAndSavePomDepVersion(Path projectPath, Text text, String version, String depGroupid) {
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


    public Map<String, String> getPomVersion(List<Path> projectPath) {
        Map<String, String> versionMap = new HashMap<>(projectPath.size());
        projectPath.forEach(pjp -> {
            //讀取每個檔案的xml檔
            try {
                String version = getPomXmlVersion(pjp.toString(), "\\pom.xml", "version");
                versionMap.put(pjp.getFileName().toString(), version);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        });
        return versionMap;
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

    public Optional<String> getMessageDialog(){
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("message");
        textInputDialog.setHeaderText("請輸入message, 預設為none message");
        textInputDialog.setContentText("message:");
        return  textInputDialog.showAndWait();
    }


}
