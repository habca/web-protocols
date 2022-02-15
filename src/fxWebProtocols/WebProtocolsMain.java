package fxWebProtocols;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;


/**
 * @author harri
 * @version 15.2.2022
 *
 */
public class WebProtocolsMain extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader ldr = new FXMLLoader(getClass().getResource("WebProtocolsGUIView.fxml"));
            final Pane root = ldr.load();
            //final WebProtocolsGUIController webprotocolsCtrl = (WebProtocolsGUIController) ldr.getController();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("webprotocols.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("WebProtocols");
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args Ei käytössä
     */
    public static void main(String[] args) {
        launch(args);
    }
}