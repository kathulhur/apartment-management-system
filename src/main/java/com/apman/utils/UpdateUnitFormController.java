package com.apman.utils;

import java.net.URL;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.apman.App;
import com.apman.models.fx.LogFX;
import com.apman.models.fx.UnitFX;
import com.apman.models.pojos.Log;
import com.apman.utils.Dialogs.InfoDialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateUnitFormController implements Initializable{

    @FXML
    private Button cancelButton;

    @FXML
    private TextField nameField;


    @FXML
    private Button updateButton;

    private UnitFX unit;

    private Stage stage;

    private SessionFactory sessionFactory = App.getSessionFactory();

    public UpdateUnitFormController(Stage stage, UnitFX unit) {
        this.stage = stage;
        this.unit = unit;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameField.setText(unit.getName());
        updateButton.setOnAction(buildUpdateButtonHandler());

        cancelButton.setOnAction(buildCancelButtonHandler());
    }

    private EventHandler<ActionEvent> buildCancelButtonHandler() {
        return e -> {
            System.out.println("Cancel button clicked");
            stage.close();
        };
    }

    private EventHandler<ActionEvent> buildUpdateButtonHandler() {
        return e -> {
            Session session = null;
            try {
                session = sessionFactory.openSession();
                session.beginTransaction();
                
                String newName = nameField.getText();
                unit.setName(newName);
                session.merge(unit.getUnitModel());

                Log updateLog = LogFX.createLogUnitUpdate(App.getCurrentUser(), unit.getUnitModel());
                session.persist(updateLog);
                
                session.getTransaction().commit();
                session.close();
                InfoDialog.show("Success", "Unit updated successfully");
                stage.close();

            } catch (Exception exception) {

                System.out.println(exception);
                InfoDialog.show("Error", "Failed updating unit");
                
            } finally {
                if (session != null) {
                    session.close();
                }
            }
        };
    }

    
}
