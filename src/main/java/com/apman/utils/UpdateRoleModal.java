package com.apman.utils;

import javafx.stage.*;
import javafx.scene.*;

import com.apman.models.fx.RoleFX;

import javafx.fxml.FXMLLoader;

public class UpdateRoleModal
{
   public static void show(RoleFX roleFX)
   {
      Stage stage = new Stage();
      stage.initModality(Modality.WINDOW_MODAL);
      stage.setTitle("New Tenant");
      stage.initStyle(StageStyle.UNDECORATED);
      stage.setMinWidth(250);
      try {
         FXMLLoader loader = new FXMLLoader(UpdateRoleModal.class.getResource("/UpdateRoleForm.fxml"));
         
         loader.setController(new UpdateRoleFormController(stage, roleFX));
         Parent root = loader.load();
         Scene scene = new Scene(root);
         stage.setScene(scene);
         stage.showAndWait();

      } catch (Exception e) {
         System.out.println("UpdateRoleModal.show" + e);
      }
   }
}