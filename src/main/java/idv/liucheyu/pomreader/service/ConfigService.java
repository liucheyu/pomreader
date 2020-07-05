package idv.liucheyu.pomreader.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import idv.liucheyu.pomreader.Main;
import idv.liucheyu.pomreader.controller.EditTextController;
import idv.liucheyu.pomreader.controller.MainController;
import idv.liucheyu.pomreader.model.ConfigModel;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Path;
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
        String folderPath = "";
        if (file != null) {
            folderPath = fileService.getAbsolutePath(file);
        }
        setBaseFolderPath(folderPath);
        return folderPath;
    }

    public void getEditDocDialog(MainController mainController, Path path) {
        FileChooser chooser = fileService.initFileChooser("請選擇檔案", path.toString());
        File file = chooser.showOpenDialog(mainController.getStage());

        if (file != null) {
            if (file.getName().substring(file.getName().indexOf(".") + 1).equals("txt")) {
                HostServices services = mainController.getApplication().getHostServices();
                services.showDocument(file.getAbsolutePath());
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditText.fxml"));
                try (FileReader fr = new FileReader(file);
                     BufferedReader reader = new BufferedReader(fr)) {
                    Parent root = loader.load();
                    Stage stage2 = new Stage();
                    EditTextController controller = loader.getController();

                    StringBuilder stringBuilder = new StringBuilder();
                    String result = null;
                    while ((result = reader.readLine()) != null) {
                        stringBuilder.append(result);
                        stringBuilder.append(System.lineSeparator());
                    }
                    controller.setMainController(mainController);
                    controller.setStage(stage2);
                    controller.getEditArea().appendText(stringBuilder.toString());
                    controller.setMdFile(file);
                    Scene scene = new Scene(root, 800, 650);
                    stage2.setScene(scene);
                    stage2.setTitle("編輯檔案");
                    stage2.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }


    public String getMavenDirDialog(Stage stage) {
        DirectoryChooser chooser = fileService.initDirectoryChooser("請選擇Maven的bin目錄");
        File file = chooser.showDialog(stage);
        String folderPath = "";
        if (file != null) {
            folderPath = fileService.getAbsolutePath(file);
        }
        setMavenDir(folderPath);
        return folderPath;
    }

    public String getMavenSettingDialog(Stage stage) {
        String mavenSetting = "";
        FileChooser xmlChooser = fileService.initFileChooser("請選擇Maven的setting.xml檔案", "C:\\", "*.xml");
        File mavenSettingFile = xmlChooser.showOpenDialog(stage);

        if (mavenSettingFile != null && mavenSettingFile.exists()) {
            mavenSetting = mavenSettingFile.getAbsolutePath();
        }
        return mavenSetting;
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
        setBaseDependency(inputOp.get());
        return inputOp.get();
    }

    public Optional<ConfigModel> getConfigFolderByFile(Stage stage) {
        File file = new File(javaPath, "pathConfig.properties");
        ConfigModel configModel;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            String baseFolder = "";
            while (baseFolder.equals("")) {
                baseFolder = getBaseFolderDialog(stage);
            }

            String depGoupId = "";
            while (depGoupId.equals("")) {
                depGoupId = getInputDependencyDialog();
            }

            String mavenDir = getMavenDirDialog(stage);


            String mavenSetting = getMavenSettingDialog(stage);


            try (FileWriter writer = new FileWriter(file)) {
                prop.setProperty("baseFolder", baseFolder);
                prop.setProperty("dependency", depGoupId);
                prop.setProperty("mavenDir", mavenDir);
                prop.setProperty("mavenSetting", mavenSetting);
                prop.store(writer, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            configModel = new ConfigModel();
            configModel.setBaseFolder(baseFolder);
            configModel.setDependencyGroupId(depGoupId);
            return Optional.ofNullable(configModel);
        }

        configModel = new ConfigModel();
        String result = "";
        try (FileReader fr = new FileReader(file);
             BufferedReader reader = new BufferedReader(fr)) {
            prop.load(reader);

            if (prop.getProperty("baseFolder", "").equals("")) {
                String baseFolder = getBaseFolderDialog(stage);
                prop.setProperty("baseFolder", baseFolder);
            }
            if (prop.getProperty("dependency", "").equals("")) {
                String depGoupId = getInputDependencyDialog();
                prop.setProperty("dependency", depGoupId);
            }
            if (prop.getProperty("mavenDir", "").equals("")) {
                String mavenDir = getMavenDirDialog(stage);
                prop.setProperty("mavenDir", mavenDir);
            }
            if (prop.getProperty("mavenSetting", "").equals("")) {
                String mavenSetting = getMavenSettingDialog(stage);
                prop.setProperty("mavenSetting", mavenSetting);
            }

            configModel.setBaseFolder(prop.getProperty("baseFolder", ""));

            configModel.setDependencyGroupId(prop.getProperty("dependency", ""));

            configModel.setMavenDir(prop.getProperty("mavenDir", ""));

            configModel.setMavenSetting(prop.getProperty("mavenSetting", ""));

            try (FileWriter writer = new FileWriter(javaPath + "pathConfig.properties")) {
                prop.store(writer, null);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return Optional.ofNullable(configModel);
    }

    public String getProperty(String proprertyName) {
        File file = new File(javaPath, "pathConfig.properties");
        String result = "";
        try (FileReader fr = new FileReader(file);
             BufferedReader reader = new BufferedReader(fr)) {
            prop.load(reader);

            result = prop.getProperty(proprertyName, "");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return result;
    }

    public void setBaseFolderPath(String path) {
        File file = new File(javaPath, "pathConfig.properties");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        try (FileReader fr = new FileReader(file);
             BufferedReader reader = new BufferedReader(fr)) {
            prop.load(reader);

        } catch (IOException e) {
            e.printStackTrace();
        }

        prop.setProperty("baseFolder", path);

        try (FileWriter writer = new FileWriter(file)) {
            prop.store(writer, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void setBaseDependency(String dependency) {
        File file = new File(javaPath, "pathConfig.properties");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try (FileReader fr = new FileReader(file);
             BufferedReader reader = new BufferedReader(fr)) {
            prop.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        prop.setProperty("dependency", dependency);
        try (FileWriter writer = new FileWriter(file)) {
            prop.store(writer, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setMavenDir(String path) {
        File file = new File(javaPath, "pathConfig.properties");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        try (FileReader fr = new FileReader(file);
             BufferedReader reader = new BufferedReader(fr)) {
            prop.load(reader);

        } catch (IOException e) {
            e.printStackTrace();
        }

        prop.setProperty("mavenDir", path);

        try (FileWriter writer = new FileWriter(file)) {
            prop.store(writer, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void setMavenSetting(String path) {
        File file = new File(javaPath, "pathConfig.properties");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        try (FileReader fr = new FileReader(file);
             BufferedReader reader = new BufferedReader(fr)) {
            prop.load(reader);

        } catch (IOException e) {
            e.printStackTrace();
        }

        prop.setProperty("mavenSetting", path);

        try (FileWriter writer = new FileWriter(file)) {
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