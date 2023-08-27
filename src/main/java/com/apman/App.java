package com.apman;

import java.io.IOException;
import java.time.LocalDate;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.apman.models.fx.InvoiceStatus;
import com.apman.models.pojos.Invoice;
import com.apman.models.pojos.Log;
import com.apman.models.pojos.Role;
import com.apman.models.pojos.Tenant;
import com.apman.models.pojos.Unit;
import com.apman.models.pojos.User;
import com.apman.models.pojos.Log.LogType;
import com.apman.utils.Dialogs.ConfirmationDialog;
import com.apman.utils.Dialogs.InfoDialog;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class App extends Application {

    private static App applicationInstance;

    private static Stage stage;

    public static Stage getStage() {
        return stage;
    }

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static App getApplicationInstance() {
        return applicationInstance;
    }

    private AnchorPane rootNode;
    
    private static Dotenv dotenv = Dotenv.load();

    public static Dotenv getDotenv() {
        return dotenv;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            App.stage = primaryStage;
            stage.setMinWidth(1280);
            stage.setMinHeight(720);
            stage.setOnCloseRequest( e -> {
                e.consume();
                closeEventHandler();
            });
            
            // what does this getClass() mean
            rootNode = FXMLLoader.load(getClass().getClassLoader().getResource("App.fxml"));
            Parent loginNode = FXMLLoader.load(getClass().getClassLoader().getResource("Login.fxml"));
            setView(loginNode);
            
            Scene scene = new Scene(rootNode);
            primaryStage.setTitle("Apartment Management System");
            primaryStage.setScene(scene);
            primaryStage.show();
           
            
        } catch (Exception e) {
            System.out.println("Failed to load login page");
            System.out.println(e);
        }
    }

    @Override
    public void init() {
        applicationInstance = this;
        configureDatabase();
    }

    private void setView(Node node) {
        AnchorPane.setLeftAnchor(node, 0D);
        AnchorPane.setTopAnchor(node, 0D);
        AnchorPane.setRightAnchor(node, 0D);
        AnchorPane.setBottomAnchor(node, 0D);
        rootNode.getChildren().setAll(node);
    }

    private void configureDatabase() {
        try {
            
            Configuration config = new Configuration()
                .configure()
                .addAnnotatedClass(Role.class)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Tenant.class)
                .addAnnotatedClass(Unit.class)
                .addAnnotatedClass(Invoice.class)
                .addAnnotatedClass(Log.class);

            config
                .setProperty("hibernate.connection.username", dotenv.get("DB_USERNAME"))
                .setProperty("hibernate.connection.password", dotenv.get("DB_PASSWORD"))
                .setProperty("hibernate.connection.url", dotenv.get("DB_URL"));

            sessionFactory = config.buildSessionFactory();
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            User user = session.createQuery("from User where name = :name", User.class)
                .setParameter("name", "admin")
                .uniqueResult();
    
            session.getTransaction().commit();
            session.close();

            if (user == null) {
                setInitialData();
            } 
            
        } catch (Exception e) {
            System.out.println("Failed to configure database");
            System.out.println(e);
            Platform.exit();
        }
    }

    private void setInitialData() {
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
    
            Role role = new Role("admin");
            User user = new User("admin", "admin@mail.com", "password", role);
            role.getUsers().add(user);
    
            Log log1 = new Log(user, LogType.LOGIN, user.getName() + " logged in");
            user.getLogs().add(log1);
            Unit unit = new Unit("unit1", 9500.00);
            unit.setCreatedBy(user);
            session.persist(log1);
    
    
            Tenant tenant = new Tenant("tenant1");
            tenant.setCreatedBy(user);
            tenant.getUnits().add(unit);
            unit.setTenant(tenant);
            unit.setNextInvoiceIssuance(LocalDate.now().minusMonths(1));

            Invoice invoice1 = new Invoice(tenant, unit, LocalDate.now(), InvoiceStatus.PENDING);
            invoice1.setIssuedBy(user);
            Invoice invoice2 = new Invoice(tenant, unit, LocalDate.now().minusMonths(7), InvoiceStatus.OVERDUE);
            invoice2.setIssuedBy(user);
            Invoice invoice3 = new Invoice(tenant, unit, LocalDate.now().minusMonths(6), InvoiceStatus.OVERDUE);
            invoice3.setIssuedBy(user);
            Invoice invoice4 = new Invoice(tenant, unit, LocalDate.now().minusMonths(5), InvoiceStatus.PAID);
            invoice4.setIssuedBy(user);
            Invoice invoice5 = new Invoice(tenant, unit, LocalDate.now().minusMonths(4), InvoiceStatus.PAID);
            invoice5.setIssuedBy(user);
            Invoice invoice6 = new Invoice(tenant, unit, LocalDate.now().minusMonths(3), InvoiceStatus.PAID);
            invoice6.setIssuedBy(user);
            Invoice invoice7 = new Invoice(tenant, unit, LocalDate.now().minusMonths(2), InvoiceStatus.PAID);
            invoice7.setIssuedBy(user);
            Invoice invoice8 = new Invoice(tenant, unit, LocalDate.now().minusMonths(2), InvoiceStatus.PAID);
            invoice8.setIssuedBy(user);
            Invoice invoice9 = new Invoice(tenant, unit, LocalDate.now().minusMonths(2), InvoiceStatus.PAID);
            invoice9.setIssuedBy(user);
            Invoice invoice10 = new Invoice(tenant, unit, LocalDate.now().minusMonths(2), InvoiceStatus.PAID);
            invoice10.setIssuedBy(user);
            Invoice invoice11 = new Invoice(tenant, unit, LocalDate.now().minusMonths(2), InvoiceStatus.PAID);
            invoice11.setIssuedBy(user);
            Invoice invoice12 = new Invoice(tenant, unit, LocalDate.now().minusMonths(2), InvoiceStatus.PAID);
            invoice12.setIssuedBy(user);

            
            session.persist("role", role);
            session.persist("user", user);
            session.persist("unit", unit);
            session.persist("tenant", tenant);
            session.persist("invoice",  invoice1);
            session.persist("invoice",  invoice2);
            session.persist("invoice",  invoice3);
            session.persist("invoice",  invoice4);
            session.persist("invoice",  invoice5);
            session.persist("invoice",  invoice6);
            session.persist("invoice",  invoice7);
            session.persist("invoice",  invoice8);
            session.persist("invoice",  invoice9);
            session.persist("invoice",  invoice10);
            session.persist("invoice",  invoice11);
            
            
            Unit unit2 = new Unit("unit 2", 9500.00);
            
            Tenant tenant2 = new Tenant("tenant 2");
            tenant2.setCreatedBy(user);
            tenant2.getUnits().add(unit2);
            unit2.setTenant(tenant2);
            
            Invoice invoiceA = new Invoice(tenant2, unit2, LocalDate.now(), InvoiceStatus.PENDING);
            unit2.setNextInvoiceIssuance(LocalDate.now());

            invoiceA.setIssuedBy(user);
            session.persist("unit", unit2);
            session.persist("tenant", tenant2);
            session.persist("invoice",  invoiceA);
    
            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            System.out.println("Failed to set initial data");
            System.out.println(e);
            Platform.exit();
        }
    }


    public void navigateToDashboard(){
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Dashboard.fxml"));
            Parent dashboardNode = loader.load();
            setView(dashboardNode);
            Platform.runLater(() -> {
                rootNode.lookup("#dashboardButton").requestFocus();
            });
            
        } catch (Exception e) {
            System.out.println("navigate to dashboard failed");
            System.out.println(e);
        }
    }

    
    public static void main(String[] args) {
        Application.launch(args);
    }


    public void navigateToUnitList() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("UnitList.fxml"));
            Parent UnitListNode = loader.load();
            
            setView(UnitListNode);
            Platform.runLater(() -> {
                rootNode.lookup("#unitListButton").requestFocus();
            });

            
        } catch (IOException e) {
            System.out.println("navigate to unit list failed");
            System.out.println(e);
        }
    }

    public void navigateToTenantList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("TenantList.fxml"));
            Parent root = loader.load();
            setView(root);
            Platform.runLater(() -> {
                rootNode.lookup("#tenantListButton").requestFocus();
            });
            
        } catch (IOException e) {
            System.out.println("navigate to tenant list failed");
            System.out.println(e);
        }
    }

    public void navigateToUserList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("UserList.fxml"));
            Parent root = loader.load();
            setView(root);
            Platform.runLater(() -> {
                rootNode.lookup("#userListButton").requestFocus();
            });
        } catch (Exception e) {
            System.out.println("navigate to user list failed");
            System.out.println(e);
        }
    }

    public void login(String email, String password) {
        try {
            Session userQuerySession = sessionFactory.openSession();
            User storedUser = userQuerySession.createQuery("from User where email = :email and password = :password", User.class)
                                .setParameter("email", email)
                                .setParameter("password", password)
                                .uniqueResult();
                                
            userQuerySession.close();

            if (storedUser != null && storedUser.getEmail().equals(email) && storedUser.getPassword().equals(password)) {
                try {
                    Session loginSession = sessionFactory.openSession();
                    loginSession.beginTransaction();
                    
                    App.currentUser = storedUser;
                    Log log = Log.Factory.loginLog(currentUser);
                    loginSession.persist("Log", log);
                    
    
                    loginSession.getTransaction().commit();
                    loginSession.close();

                } catch (Exception e) {
                    System.out.println("Failed to create log");
                    System.out.println(e);
                }

                navigateToDashboard();

            } else {
                System.out.println("Login failed");
                InfoDialog.show("Login", "Login failed");
            }
            

            
        } catch (Exception e) {
            System.out.println("Failed to login");
            System.out.println(e);
        }
    }

    public void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Login.fxml"));
            Parent root = loader.load();
            
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            
            Log log = Log.Factory.logoutLog(currentUser);
            session.persist("log", log);
            session.getTransaction().commit();
            App.currentUser = null;
            
            session.close();
            
            setView(root);

        } catch (IOException e) {
            System.out.println("navigate to tenant list failed");
            System.out.println(e);
        }
    }


    public void navigateToRoleList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("RoleList.fxml"));
            Parent root = loader.load();
            setView(root);
            Platform.runLater(() -> {
                rootNode.lookup("#roleListButton").requestFocus();
            });
        } catch (IOException e) {
            System.out.println("navigate to role list failed");
            System.out.println(e);
        }
    }

    public void navigateToLogList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("LogList.fxml"));
            Parent root = loader.load();
            setView(root);
            Platform.runLater(() -> {
                rootNode.lookup("#logListButton").requestFocus();
            });
        } catch (IOException e) {
            System.out.println("navigate to log list failed");
            System.out.println(e);
        }
    }

    public static void closeEventHandler() {
        try {
            new ConfirmationDialog("Exit", "Do you really want to exit?")
                .showAndWait()
                .ifPresent( exitConfirmed -> {
                    if (exitConfirmed) {
                        if (currentUser != null) {
                            Session session = sessionFactory.openSession();
                            session.beginTransaction();

                            Log log = Log.Factory.logoutLog(currentUser);
                            session.persist("log", log);

                            session.getTransaction().commit();
                            session.close();
                        }
                        sessionFactory.close();
                        stage.close();
                    }

                });
        } catch (Exception e) {
            System.out.println("Failed to close session");
            System.out.println(e);
        }
    }



}