package com.apman.utils.Dialogs;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

public class InfoDialog extends Dialog<Void>{

    public InfoDialog(String title, String message) {
        setTitle(title);

        // Set content of the custom dialog
        getDialogPane().setContent(new Label(message));

        // Set buttons in the button bar
        getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        

    }

    public static void show(String title, String message) {
        InfoDialog dialog = new InfoDialog(title, message);
        dialog.showAndWait();
    }

    
}   
