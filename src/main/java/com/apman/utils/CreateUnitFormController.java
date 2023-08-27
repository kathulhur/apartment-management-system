package com.apman.utils;

import java.net.URL;
import java.util.ResourceBundle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.apman.App;
import com.apman.models.fx.LogFX;
import com.apman.models.pojos.Log;
import com.apman.models.pojos.Unit;
import com.apman.utils.Dialogs.InfoDialog;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;



public class CreateUnitFormController implements Initializable{

    @FXML
    private Button createUnitButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField nameField;

    @FXML
    private TextField rateField;

    private Stage stage;

    private Callback<Unit, Void> onSubmit;

    private SessionFactory sessionFactory = App.getSessionFactory();

    PseudoClass errorClass = PseudoClass.getPseudoClass("error");

    

    public CreateUnitFormController(Stage stage, Callback<Unit, Void> onSubmit) {
        this.stage = stage;
        this.onSubmit = onSubmit;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rateField.textProperty().addListener(event -> {
            rateField.pseudoClassStateChanged(// magbabago si Pseudoclass
                PseudoClass.getPseudoClass("error"), // error
                !rateField.getText().isEmpty() && 
                    !rateField.getText().matches(Validation.validRateRegex) // kapag ganito
            );
            

            if (!rateField.getText().isEmpty() &&  !rateField.getText().matches(Validation.validRateRegex)) {
                createUnitButton.setDisable(true);
            } else {
                createUnitButton.setDisable(false);
            }
        });
        createUnitButton.setOnAction(buildCreateUnitButtonHandler());
        cancelButton.setOnAction(buildCancelButtonHandler());

    }

    private EventHandler<ActionEvent> buildCancelButtonHandler() {
        return e -> {
            System.out.println("AddUnitFormController.cancelButton.setOnAction");
            this.stage.close();
        };
    }

    private EventHandler<ActionEvent> buildCreateUnitButtonHandler() {
        return e -> {
            try {
                Session session = sessionFactory.openSession();
                session.beginTransaction();
                String name = nameField.getText();
                Double rate = Converter.DoubleStringConverter.fromString(rateField.getText());
                
                Unit newUnit = new Unit(
                    name,
                    rate
                );
                newUnit.setCreatedBy(App.getCurrentUser());

                session.persist(newUnit);    

                // log creation
                Log log = LogFX.createLogUnitCreate(App.getCurrentUser(), newUnit);
                session.persist(log);
                
                session.getTransaction().commit();
                session.close();

                onSubmit.call(newUnit);
                InfoDialog.show("Success", "Unit created successfully");
                this.stage.close();

            } catch (Exception exception) {
                InfoDialog.show("Error", "Failed creating unit");
                System.out.println(exception);
            }
        };
    }



}
