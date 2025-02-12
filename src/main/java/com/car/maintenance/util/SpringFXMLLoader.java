package com.car.maintenance.util;

import javafx.fxml.FXMLLoader;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

/**
 * @author Qu Li 
 * Created on 2025-02-10
 */
public class SpringFXMLLoader {
    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringFXMLLoader.applicationContext = applicationContext;
    }

    public static FXMLLoader load(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(applicationContext::getBean);
        loader.setLocation(SpringFXMLLoader.class.getResource(fxmlPath));
        return loader;
    }
}
