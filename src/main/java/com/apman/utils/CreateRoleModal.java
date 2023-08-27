package com.apman.utils;

import javafx.stage.*;
import javafx.util.Callback;
import javafx.scene.*;

import com.apman.models.pojos.Role;

import javafx.fxml.FXMLLoader;

public class CreateRoleModal
{
   public static void show(Callback<Role, Void> onCreate)
   {
      Stage stage = new Stage();
      stage.initModality(Modality.WINDOW_MODAL);
      stage.setTitle("New Role");
      stage.initStyle(StageStyle.UNDECORATED);
      stage.setMinWidth(250);
      try {
         FXMLLoader loader = new FXMLLoader(CreateRoleModal.class.getClassLoader().getResource("CreateRoleForm.fxml"));
         
         loader.setController(new CreateRoleFormController(stage, onCreate));
         Parent root = loader.load();
         Scene scene = new Scene(root);
         stage.setScene(scene);
         stage.showAndWait();

      } catch (Exception e) {
         System.out.println("AddRoleModal.show" + e);
      }
   }
}