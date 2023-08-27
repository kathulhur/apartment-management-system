package com.apman.utils;

import javafx.stage.*;
import javafx.util.Callback;
import javafx.scene.*;

import com.apman.models.pojos.Tenant;

import javafx.fxml.FXMLLoader;

public class CreateTenantModal
{
   public static void show(Callback<Tenant, Void> onCreate)
   {
      Stage stage = new Stage();
      stage.initModality(Modality.WINDOW_MODAL);
      stage.setTitle("New Tenant");
      stage.initStyle(StageStyle.UNDECORATED);
      stage.setMinWidth(250);
      try {
         FXMLLoader loader = new FXMLLoader(CreateTenantModal.class.getClassLoader().getResource("CreateTenantForm.fxml"));
         
         loader.setController(new CreateTenantFormController(stage, onCreate));
         Parent root = loader.load();
         Scene scene = new Scene(root);
         stage.setScene(scene);
         stage.showAndWait();

      } catch (Exception e) {
         System.out.println("AddTenantModal.show" + e);
      }
   }
}