package com.apman.utils;

import com.apman.models.fx.TenantFX;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InvoiceListWindow {

    public static void show(TenantFX tenant) {

        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Invoices issued to Mr/Ms. " + tenant.getName());
        stage.setMinWidth(250);
        stage.setResizable(false);
        try {
            FXMLLoader loader = new FXMLLoader(InvoiceListWindow.class.getResource("/InvoiceList.fxml"));
            
            loader.setController(new InvoiceListController(stage, tenant));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait();

        } catch (Exception e) {
            System.out.println("InvoiceListWindow.show");
            System.out.println(e);
        }

    }
    
}
