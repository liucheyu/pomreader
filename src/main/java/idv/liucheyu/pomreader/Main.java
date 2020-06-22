package idv.liucheyu.pomreader;

import idv.liucheyu.pomreader.controller.MainController;
import idv.liucheyu.pomreader.service.ConfigService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.*;


public class Main extends Application {

    ConfigService configService = new ConfigService();


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLDocument.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.setControllerStage(stage);
        //等待讀取完設定檔再show
        controller.getMainPane().setVisible(false);


        Map<String, String> configMap = configService.getConfigFolderByFile();
        //取得並儲存baseFolder
        String baseFolder = configMap.get("baseFolder");
        while (baseFolder.equals("")) {
            baseFolder = configService.getBaseFolderDialog(stage);
        }
        configService.setBaseFolderPath(baseFolder);
        //取得並儲存dependency
        String dependency = configMap.get("dependency");
        while (dependency.equals("")) {
            dependency = configService.getInputDependencyDialog();
        }
        configService.setBaseDependency(dependency);

        if(!baseFolder.equals("") && !dependency.equals("")){
            //渲染首頁
            controller.setUpMainPage(baseFolder, dependency);
        }

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
