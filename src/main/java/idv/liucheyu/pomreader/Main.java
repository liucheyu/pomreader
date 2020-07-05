package idv.liucheyu.pomreader;

import idv.liucheyu.pomreader.component.PageSetting;
import idv.liucheyu.pomreader.controller.MainController;
import idv.liucheyu.pomreader.model.ConfigModel;
import idv.liucheyu.pomreader.service.ConfigService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;


public class Main extends Application {

    ConfigService configService = new ConfigService();
    PageSetting pageSetting = new PageSetting();


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLDocument.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.setApplication(this);
        controller.setControllerStage(stage);
        //等待讀取完設定檔再show
        controller.getMainPane().setVisible(false);

        Optional<ConfigModel> configOp = configService.getConfigFolderByFile(stage);

        if (!configOp.isEmpty()) {
            ConfigModel configModel = configOp.get();
            if (configModel.getMavenDir() != null) {
                File file = new File(configOp.get().getMavenDir());
                if (file.exists()) {
                    FilenameFilter filter = new FilenameFilter() {
                        @Override
                        public boolean accept(File file, String s) {
                            return s.contains("mvn");
                        }
                    };
                    if (file.list(filter).length > 0) {
                        controller.getMavenPage().setDisable(false);
                    }
                }
            }
            pageSetting.setUpMainPage(configOp.get(), stage, controller);
        }


        controller.setConfigModel(configOp.get());

        controller.getMainPane().setVisible(true);
        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(getClass().getResource("/main.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("POM Reader");
        stage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
