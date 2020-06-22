package idv.liucheyu.pomreader.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import idv.liucheyu.pomreader.Main;
import idv.liucheyu.pomreader.component.MainPage;
import idv.liucheyu.pomreader.service.FileService;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class ConfigService {

    private FileService fileService = new FileService();
    private ObjectMapper objectMapper = new ObjectMapper();
    String javaPath = Main.class.getResource("").getPath();
    Properties prop = new Properties();

    public String getBaseFolderDialog(Stage stage) {
        DirectoryChooser chooser = fileService.initDirectoryChooser("請選擇專案的父層資料夾");
        File file = chooser.showDialog(stage);
        String folderPath = fileService.getAbsolutePath(file);
        setBaseFolderPath(folderPath);
        return folderPath;
    }

    public String getInputDependencyDialog() {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Project Dependency");
        textInputDialog.setHeaderText("請設定依賴專案的groupId");
        textInputDialog.setContentText("groupId:");
        Optional<String> inputOp = textInputDialog.showAndWait();
        if (inputOp.isEmpty()) {
            return "";
        }
        return inputOp.get();
    }

    public Map<String, String> getConfigFolderByFile() {

        Map<String, String> conofigMap = new HashMap<>(2);
        conofigMap.put("baseFolder", "");
        conofigMap.put("dependency", "");
        File file = new File(javaPath + "pathConfig.properties");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try (FileWriter writer = new FileWriter(javaPath + "pathConfig.properties")) {
                prop.setProperty("baseFolder", "");
                prop.setProperty("dependency", "");
                prop.store(writer, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return conofigMap;
        }

        String result = "";
        try (FileReader fr = new FileReader(javaPath + "pathConfig.properties");
             BufferedReader reader = new BufferedReader(fr)) {
            prop.load(reader);
            conofigMap.put("baseFolder", prop.getProperty("baseFolder", ""));

            conofigMap.put("dependency", prop.getProperty("dependency", ""));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return conofigMap;
    }

    public void setBaseFolderPath(String path) {
        try (FileReader fr = new FileReader(javaPath + "pathConfig.properties");
             BufferedReader reader = new BufferedReader(fr)) {
            prop.load(reader);

        } catch (IOException e) {
            e.printStackTrace();
        }

        prop.setProperty("baseFolder", path);

        try (FileWriter writer = new FileWriter(javaPath + "pathConfig.properties")) {
            prop.store(writer, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void setBaseDependency(String dependency) {
        try (FileReader fr = new FileReader(javaPath + "pathConfig.properties");
             BufferedReader reader = new BufferedReader(fr)) {
            prop.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        prop.setProperty("dependency", dependency);
        try (FileWriter writer = new FileWriter(javaPath + "pathConfig.properties")) {
            prop.store(writer, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public JsonNode readFileJson() {
        String result = "";
        JsonNode jsonNode = null;
        try (FileReader fr = new FileReader(javaPath + "pathConfig.properties");
             BufferedReader reader = new BufferedReader(fr)) {
            StringBuilder builder = new StringBuilder();
            while ((result = reader.readLine()) != null) {
                builder.append(result);
            }

            jsonNode = objectMapper.readTree(builder.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonNode;
    }
}