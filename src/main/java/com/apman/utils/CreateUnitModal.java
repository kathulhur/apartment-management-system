package com.apman.utils;

import javafx.stage.*;
import javafx.util.Callback;
import javafx.scene.*;

import com.apman.models.pojos.Unit;

import javafx.fxml.FXMLLoader;

public class CreateUnitModal
{
   public static void show(Callback<Unit, Void> onSubmit)
   {
      Stage stage = new Stage();
      stage.initModality(Modality.WINDOW_MODAL);
      stage.setTitle("New Tenant");
      stage.initStyle(StageStyle.UNDECORATED);
      stage.setMinWidth(250);
      try {
         FXMLLoader loader = new FXMLLoader(CreateUnitModal.class.getClassLoader().getResource("CreateUnitForm.fxml"));
         
         loader.setController(new CreateUnitFormController(stage, onSubmit));
         Parent root = loader.load();
         Scene scene = new Scene(root);
         stage.setScene(scene);
         stage.showAndWait();

      } catch (Exception e) {
         System.out.println("AddTenantModal.show" + e);
      }
   }
}