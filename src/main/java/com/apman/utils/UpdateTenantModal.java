package com.apman.utils;

import javafx.stage.*;
import javafx.scene.*;

import com.apman.models.fx.TenantFX;

import javafx.fxml.FXMLLoader;

public class UpdateTenantModal
{
   public static void show(TenantFX tenant)
   {
      Stage stage = new Stage();
      stage.initModality(Modality.WINDOW_MODAL);
      stage.setTitle("New Tenant");
      stage.initStyle(StageStyle.UNDECORATED);
      stage.setMinWidth(250);
      try {
         FXMLLoader loader = new FXMLLoader(CreateUnitModal.class.getResource("/UpdateTenantForm.fxml"));
         
         loader.setController(new UpdateTenantFormController(stage, tenant));
         Parent root = loader.load();
         Scene scene = new Scene(root);
         stage.setScene(scene);
         stage.showAndWait();

      } catch (Exception e) {
         System.out.println("UpdateTenantModal.show" + e);
      }
   }
}