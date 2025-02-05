package mx.fei.coilvicapp.gui.controllers;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import java.util.Optional;
import javafx.collections.ObservableList;

public class DialogController {
    
    public static final ButtonType BUTTON_YES = new ButtonType("Sí", ButtonBar.ButtonData.YES);
    public static final ButtonType BUTTON_NO = new ButtonType("No", ButtonBar.ButtonData.NO);
    public static final ButtonType BUTTON_ACCEPT = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
    
    public static Optional<ButtonType> getDialog(AlertMessage message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (null != message.getAlertType()) {
            switch (message.getAlertType()) {
                case SUCCESS -> {
                }
                case WARNING -> {
                    alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Advertencia");
                }
                case ERROR -> {
                    alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Error");
                }
                case FATAL -> {
                    alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Fatal");
                }
                default -> throw new IllegalArgumentException("Tipo de mensaje no admitido");                    
            }
        }
        alert.setTitle("Cuadro de diálogo");
        alert.setContentText(message.getContent());
        return alert.showAndWait();
    }
    
    public static Optional<ButtonType> getConfirmationDialog(String title, String message) {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                message,
                BUTTON_YES,
                BUTTON_NO);
        alert.setHeaderText(title);
        return alert.showAndWait();
    }
    
    public static Optional<ButtonType> getPositiveConfirmationDialog(String title, String message) {
        Alert alert = new Alert(
                Alert.AlertType.INFORMATION,
                message, BUTTON_ACCEPT);
        alert.setHeaderText(title);
        return alert.showAndWait();
    }
    
    public static Optional<ButtonType> getEmptyFieldsConfirmationDialog (String title, String message) {
        Alert alert = new Alert(
                Alert.AlertType.INFORMATION,
                message, BUTTON_ACCEPT);
        alert.setHeaderText(title);
        return alert.showAndWait();
    }
   
}