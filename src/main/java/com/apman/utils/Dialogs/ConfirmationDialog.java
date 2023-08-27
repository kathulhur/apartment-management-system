package com.apman.utils.Dialogs;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

public class ConfirmationDialog extends Dialog<Boolean>{

    public ConfirmationDialog(String title, String message) {
        setTitle(title);
        setHeaderText("Confirmation");

        // Set content of the custom dialog
        getDialogPane().setContent(new Label(message));

        // Set buttons in the button bar
        getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        // Handle button action and return result
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.YES) {
                return true;
            } else {
                return false;
            }
        });
    }


    
}   
