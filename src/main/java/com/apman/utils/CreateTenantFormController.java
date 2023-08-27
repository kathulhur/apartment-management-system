package com.apman.utils;

import java.net.URL;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.apman.App;
import com.apman.models.pojos.Log;
import com.apman.models.pojos.Tenant;
import com.apman.utils.Dialogs.InfoDialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;

public class CreateTenantFormController implements Initializable {

    @FXML
    private Button createTenantButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField fullnameField;

    private SessionFactory sessionFactory = App.getSessionFactory();

    private Callback<Tenant, Void> onCreate;

    private Stage stage;
    public CreateTenantFormController(Stage stage, Callback<Tenant, Void> onCreate) {
        this.stage = stage;
        this.onCreate = onCreate;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        createTenantButton.setOnAction(buildCreateTenantButtonHandler());
        cancelButton.setOnAction(buildSetOnActionHandler());
    }

    private EventHandler<ActionEvent> buildSetOnActionHandler() {
        return e -> {
            System.out.println("AddTenantFormController.cancelButton.setOnAction");
            stage.close();
        };
    }

    private EventHandler<ActionEvent> buildCreateTenantButtonHandler() {
        return e -> {
            try {
                Session session = sessionFactory.openSession();
                session.beginTransaction();
    
                System.out.println("AddTenantFormController.createTenantButton.setOnAction");
                String fullname = fullnameField.getText();
    
                Tenant newTenant = new Tenant(fullname);
                newTenant.setCreatedBy(App.getCurrentUser());
                session.persist(newTenant);

                Log log = Log.Factory.tenantCreateLog(App.getCurrentUser(), newTenant);
                session.persist(log);
                
                session.getTransaction().commit();
                session.close();
                onCreate.call(newTenant);

                InfoDialog.show("Success", "Tenant created successfully");
                stage.close();

            } catch (Exception ex) {
                System.out.println("AddTenantFormController.createTenantButton.setOnAction" + ex);
            }

        };
    }

}
