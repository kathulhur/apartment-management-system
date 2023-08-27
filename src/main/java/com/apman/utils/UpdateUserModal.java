package com.apman.utils;

import javafx.stage.*;
import javafx.scene.*;

import com.apman.models.fx.UserFX;

import javafx.fxml.FXMLLoader;

public class UpdateUserModal
{
   public static void show(UserFX user)
   {
      Stage stage = new Stage();
      stage.initModality(Modality.WINDOW_MODAL);
      stage.setTitle("New Tenant");
      stage.initStyle(StageStyle.UNDECORATED);
      stage.setMinWidth(250);
      try {
         FXMLLoader loader = new FXMLLoader(UpdateUserModal.class.getResource("/UpdateUserForm.fxml"));
         
         loader.setController(new UpdateUserFormController(stage, user));
         Parent root = loader.load();
         Scene scene = new Scene(root);
         stage.setScene(scene);
         stage.showAndWait();

      } catch (Exception e) {
         System.out.println("UpdateUserModal.show" + e);
      }
   }
}