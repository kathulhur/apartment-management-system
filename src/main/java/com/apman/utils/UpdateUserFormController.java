package com.apman.utils;

import java.net.URL;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.apman.App;
import com.apman.models.fx.UserFX;
import com.apman.models.pojos.Log;
import com.apman.utils.Dialogs.InfoDialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateUserFormController implements Initializable {

    @FXML
    private Button cancelButton;

    @FXML
    private TextField emailField;

    @FXML
    private TextField fullnameField;

    @FXML
    private Button updateButton;


    private Stage stage;

    private UserFX userFX;

    private SessionFactory sessionFactory = App.getSessionFactory();

    public UpdateUserFormController(Stage stage, UserFX user){
        this.stage = stage;
        this.userFX = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fullnameField.setText(userFX.getName());
        emailField.setText(userFX.getEmail());
        cancelButton.setOnAction(buildCancelButtonHandler());

        updateButton.setOnAction(buildUpdateButtonHandler());
    }

    private EventHandler<ActionEvent> buildUpdateButtonHandler() {
        return e -> {
            try {
                Session session = sessionFactory.openSession();
                session.beginTransaction();

                userFX.setName(fullnameField.getText());
                userFX.setEmail(emailField.getText());
                session.merge(userFX.getUserModel());

                Log log = Log.Factory.userUpdateLog(App.getCurrentUser(), userFX.getUserModel());
                session.persist(log);
                
                session.getTransaction().commit();
                session.close();

                InfoDialog.show("Success", "User update successful");
                stage.close();
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                InfoDialog.show("Error", "User update failed");
            } 
        };
    }

    private EventHandler<ActionEvent> buildCancelButtonHandler() {
        return e -> {
            stage.close();
        };
    }

}
