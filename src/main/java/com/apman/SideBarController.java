package com.apman;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class SideBarController implements Initializable {

    @FXML
    private Label apartmentLabel;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button tenantListButton;

    @FXML
    private Button unitListButton;

    @FXML
    private Button userListButton;
    
    @FXML
    private Button roleListButton;

    @FXML
    private Button logListButton;

    @FXML String currentView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        App app = App.getApplicationInstance();

        dashboardButton.setOnAction(e -> {
            System.out.println("Dashboard");
            app.navigateToDashboard();
        });

        tenantListButton.setOnAction(e -> {
            System.out.print("TenantList");
            
            app.navigateToTenantList();
        });   
        
        unitListButton.setOnAction(e -> {
            System.out.print("UnitList");
            app.navigateToUnitList();
        });  
        
        userListButton.setOnAction(e -> {
            System.out.print("UserList");
            app.navigateToUserList();
        });   

        roleListButton.setOnAction(e -> {
            System.out.print("RoleList");
            app.navigateToRoleList();
        }); 

        logListButton.setOnAction(e -> {
            System.out.print("Activity Logs");
            app.navigateToLogList();
        });   



        logoutButton.setOnAction(e -> {
            System.out.println("Logout");
            app.logout();
        });

        

        
    }

    
    

}
