package fxWebProtocols;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * @author Harri Linna
 * @version 15.2.2022
 *
 */
public class WebProtocolsGUIController implements Initializable {

    @FXML
    private Label statusBar;
    
    @FXML
    private void handleClose() {
        System.exit(0);
    }
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        statusBar.textProperty().bindBidirectional(statusBind);
        statusBind.set(status);
    }
    
    private SimpleStringProperty statusBind = new SimpleStringProperty("Hello World!");
    private String status = "Well Done!";
    
}
