package com.car.maintenance;

/**
 * @author Qu Li 
 * Created on 2025-02-08
 */

import com.car.maintenance.util.SpringFXMLLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class JavaFxApp extends Application {

    private static ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        applicationContext = new SpringApplicationBuilder(JavaFxApp.class).run();
        SpringFXMLLoader.setApplicationContext(applicationContext);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = SpringFXMLLoader.load("/fxml/MainView.fxml").load();

        primaryStage.setTitle("Spring Boot + JavaFX");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        applicationContext.close();
        Platform.exit();
    }
}