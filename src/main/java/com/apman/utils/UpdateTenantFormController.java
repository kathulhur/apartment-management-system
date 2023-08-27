package com.apman.utils;

import java.net.URL;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.apman.App;
import com.apman.models.fx.TenantFX;
import com.apman.models.pojos.Log;
import com.apman.utils.Dialogs.InfoDialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateTenantFormController implements Initializable{

    @FXML
    private TextField fullnameField;

    @FXML
    private Button updateTenantButton;

    @FXML
    private Button cancelButton;

    private TenantFX tenantFX;
    private Stage stage;

    private SessionFactory sessionFactory = App.getSessionFactory();

    public UpdateTenantFormController(Stage stage, TenantFX tenantFX) {
        this.tenantFX = tenantFX;
        this.stage = stage;
    }
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fullnameField.setText(tenantFX.getName());
        updateTenantButton.setOnAction(buildUpdateTenantButtonHandler());
        cancelButton.setOnAction(buildCancelButtonHandler());
    }


    private EventHandler<ActionEvent> buildCancelButtonHandler() {
        return e -> {
            System.out.println("Cancel button clicked");
            this.stage.close();
        };
    }


    private EventHandler<ActionEvent> buildUpdateTenantButtonHandler() {
        return e -> {
            Session session = null;
            try {
                session = sessionFactory.openSession();
                session.beginTransaction();

                String modifiedName = fullnameField.getText();
    
                tenantFX.setName(modifiedName);
                session.merge(tenantFX.getTenantModel());
    
                Log log = Log.Factory.tenantUpdateLog(App.getCurrentUser(), tenantFX.getTenantModel());
                session.persist(log);
                
                session.getTransaction().commit();
                session.close();
                InfoDialog.show("Success", "Tenant updated successfully");
                this.stage.close();

            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                InfoDialog.show("Error", "Failed updating tenant");
            } finally {
                if (session != null) {
                    session.close();
                }
            }
        };
    }

    


}
