package com.apman.utils;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.apman.App;
import com.apman.models.fx.TenantFX;
import com.apman.models.pojos.Unit;
import com.apman.utils.Dialogs.InfoDialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class UpdateTenantUnitController implements Initializable {

    @FXML
    private Label currentUnitsLabel;

    @FXML
    private Label tenantLabel;

    @FXML
    private ChoiceBox<Unit> unitChoiceBox;

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;

    private TenantFX tenant;

    private Stage stage;

    private SessionFactory sessionFactory = App.getSessionFactory();

    public UpdateTenantUnitController(Stage stage, TenantFX tenant) {
        this.stage = stage;
        this.tenant = tenant;

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        List<Unit> currentTenantUnits = tenant.getUnits();
        int currentUnitsCount = currentTenantUnits.size();
        String currentUnitsText = "";
        if (currentUnitsCount > 0) {
            for(int i = 0; i < currentTenantUnits.size()-1; i++){
                currentUnitsText += currentTenantUnits.get(i) + ", ";
            }
            currentUnitsText += currentTenantUnits.get(currentUnitsCount-1);

        }
        currentUnitsLabel.setText(currentUnitsText);
        tenantLabel.setText(tenant.getName());
        cancelButton.setOnAction(buildCancelButtonHandler());
        confirmButton.setOnAction(buildConfirmButtonHandler());

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            Query<Unit> query = session.createQuery("from Unit where deleted=false and tenant=null", Unit.class);
            List<Unit> availableUnits = query.list();

            session.getTransaction().commit();
            session.close();
            unitChoiceBox.getItems().setAll(availableUnits);

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            InfoDialog.show("Error", "Fetch available units failed");
        }
        

    }


    private EventHandler<ActionEvent> buildConfirmButtonHandler() {
        return e -> {
            // Update the unit related properties of tenant
            Unit selectedUnit = unitChoiceBox.getValue();

            if (selectedUnit.getTenant() != null) { // prevent operation if selected unit is occupied
                throw new UnsupportedOperationException("Selecting an occupied unit is not allowed");
            }

            Session session = null;
            try {
                // assign Tenant to the unit
                session = sessionFactory.openSession();
                session.beginTransaction();

                selectedUnit.setDateOccupied(LocalDate.now());
                selectedUnit.setNextInvoiceIssuance(LocalDate.now()); // TODO: set next invoice issuance date
                tenant.addUnit(selectedUnit);
                session.merge(selectedUnit);

                session.getTransaction().commit();
                InfoDialog.show("Success", tenant.getName() + " was successfully assigned to " + selectedUnit.getName());

            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                InfoDialog.show("Error", "Failed assigning " + tenant.getName() +  " to unit " + selectedUnit.getName());
            } finally {
                if (session != null) {
                    session.close();
                }
            }

            // close the modal
            stage.close();
        };
    }


    private EventHandler<ActionEvent> buildCancelButtonHandler() {
        return e -> {
            stage.close();
        };
    }

}
