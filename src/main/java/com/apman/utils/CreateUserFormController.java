package com.apman.utils;

import java.net.URL;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.apman.App;
import com.apman.models.pojos.Log;
import com.apman.models.pojos.Role;
import com.apman.models.pojos.User;
import com.apman.utils.Dialogs.InfoDialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;

public class CreateUserFormController implements Initializable {

    @FXML
    private Button cancelButton;

    @FXML
    private Button createUserButton;

    @FXML
    private TextField emailField;

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    private Stage stage;
    
    private SessionFactory sessionFactory = App.getSessionFactory();

    private Callback<User, Void> onCreate;
    public CreateUserFormController(Stage stage, Callback<User, Void> onCreate) {
        this.stage = stage;
        this.onCreate = onCreate;
    }

    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancelButton.setOnAction(buildCancelButtonHandler());
        createUserButton.setOnAction(buildCreateUserButtonHandler());
    }

    private EventHandler<ActionEvent> buildCreateUserButtonHandler() {
        return e -> {
            try {
                Session session = sessionFactory.openSession();
                session.beginTransaction();
                Role userRole = session.get(Role.class, 1);
                User newUser = new User(
                    nameField.getText(),
                    emailField.getText(),
                    passwordField.getText(),
                    userRole
                );
                session.persist(newUser);

                Log log = Log.Factory.userCreateLog(App.getCurrentUser(), newUser);
                session.persist(log);

                session.getTransaction().commit();
                session.close();

                onCreate.call(newUser);
                InfoDialog.show("Success", "User created successfully");
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                InfoDialog.show("Error", "Failed creating user");
            }
        };
    }

    private EventHandler<ActionEvent> buildCancelButtonHandler() {
        return e -> {
            stage.close();
        };
    }

}
