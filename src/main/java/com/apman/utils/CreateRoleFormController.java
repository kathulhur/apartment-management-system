package com.apman.utils;

import java.net.URL;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.apman.App;
import com.apman.models.pojos.Log;
import com.apman.models.pojos.Role;
import com.apman.utils.Dialogs.InfoDialog;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;

public class CreateRoleFormController implements Initializable {


    @FXML
    private CheckBox canViewDashboard;

    @FXML
    private CheckBox canViewRole;

    @FXML
    private CheckBox canViewTenant;

    @FXML
    private CheckBox canViewUnit;

    @FXML
    private CheckBox canViewUser;

    @FXML
    private CheckBox canViewlog;

    @FXML
    private Button cancelButton;

    @FXML
    private Button createRoleButton;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField nameField;


    private Stage stage;

    private SessionFactory sessionFactory = App.getSessionFactory();

    private Callback<Role, Void> onCreate;
    public CreateRoleFormController(Stage stage, Callback<Role, Void> onCreate) {
        this.stage = stage;
        this.onCreate = onCreate;

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancelButton.setOnAction(e -> {
            this.stage.close();
        });

        createRoleButton.setOnAction(e -> {
            try {
                if (nameField.getText().isEmpty()) {
                    InfoDialog.show("Error", "Name field is required");
                    return;
                }

                if (descriptionField.getText().isEmpty()) {
                    InfoDialog.show("Error", "Description field is required");
                    return;
                }

                if (canViewDashboard.isSelected() == false
                        && canViewRole.isSelected() == false
                        && canViewTenant.isSelected() == false 
                        && canViewUnit.isSelected() == false
                        && canViewUser.isSelected() == false 
                        && canViewlog.isSelected() == false) {
                    InfoDialog.show("Error", "At least one permission must be selected");
                    return;
                }

                Session session = sessionFactory.openSession();
                session.beginTransaction();

                Role newRole = new Role(nameField.getText());
                newRole.setCreatedBy(App.getCurrentUser());
                session.persist(newRole);

                Log log = Log.Factory.roleCreateLog(App.getCurrentUser(), newRole);
                session.persist(log);

                session.getTransaction().commit();
                session.close();

                onCreate.call(newRole);
                InfoDialog.show("Success", "Role created successfully");
                
                this.stage.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

    }

}
