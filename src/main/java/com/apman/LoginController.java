package com.apman;


import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class LoginController implements Initializable {

    @FXML
    private Button loginButton;

    @FXML
    private Pane loginPane;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailField;
    
    @FXML
    private Button exitButton;

    private App application = App.getApplicationInstance();
    @FXML
    void onLoginButtonClicked(ActionEvent event) {
        String emailInput = emailField.getText();
        String passwordInput = passwordField.getText();
        application.login(emailInput, passwordInput);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        exitButton.setOnAction(e -> {
            App.closeEventHandler();
        });
    }

    


}
