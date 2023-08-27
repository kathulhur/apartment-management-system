package com.apman.utils;

import javafx.stage.*;
import javafx.scene.*;

import com.apman.models.fx.UnitFX;

import javafx.fxml.FXMLLoader;

public class UpdateUnitModal
{
   public static void show(UnitFX unit)
   {
      Stage stage = new Stage();
      stage.initModality(Modality.WINDOW_MODAL);
      stage.setTitle("New Tenant");
      stage.initStyle(StageStyle.UNDECORATED);
      stage.setMinWidth(250);
      try {
         FXMLLoader loader = new FXMLLoader(UpdateUnitModal.class.getResource("/UpdateUnitForm.fxml"));
         loader.setController(new UpdateUnitFormController(stage, unit));
         Parent root = loader.load();
         Scene scene = new Scene(root);
         stage.setScene(scene);
         stage.showAndWait();

      } catch (Exception e) {
         System.out.println("UpdateUnitModal.show");
         System.out.println(e);
      }
   }

}