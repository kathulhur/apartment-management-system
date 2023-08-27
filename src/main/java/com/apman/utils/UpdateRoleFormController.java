package com.apman.utils;

import java.net.URL;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.apman.App;
import com.apman.models.fx.RoleFX;
import com.apman.models.pojos.Log;
import com.apman.utils.Dialogs.InfoDialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateRoleFormController implements Initializable {
    
    @FXML
    private TextField nameField;

    @FXML
    private Button cancelButton;

    
    @FXML
    private Button updateButton;


    private Stage stage;

    private RoleFX roleFX;

    private SessionFactory sessionFactory = App.getSessionFactory();

    public UpdateRoleFormController(Stage stage, RoleFX roleFX){
        this.stage = stage;
        this.roleFX = roleFX;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameField.setText(this.roleFX.getName());
        cancelButton.setOnAction(buildCancelButtonHandler());
        updateButton.setOnAction(buildUpdateButtonHandler());
    }

    private EventHandler<ActionEvent> buildUpdateButtonHandler() {
        return e -> {
            try {
                Session session = sessionFactory.openSession();
                session.beginTransaction();

                roleFX.setName(nameField.getText());
                session.merge(roleFX.getRoleModel());

                Log log = Log.Factory.roleUpdateLog(App.getCurrentUser(), roleFX.getRoleModel());
                session.persist(log);

                session.getTransaction().commit();
                session.close();
                InfoDialog.show("Success", "Role updated successfully");
                stage.close();
            } catch (Exception exception) {
                System.out.println(exception);
                InfoDialog.show("Failed", "Update Failed");
            } 
        };
    }

    private EventHandler<ActionEvent> buildCancelButtonHandler() {
        return e -> {
            stage.close();
        };
    }

}
