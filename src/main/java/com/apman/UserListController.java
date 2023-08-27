package com.apman;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.apman.models.fx.UserFX;
import com.apman.models.pojos.Log;
import com.apman.models.pojos.Role;
import com.apman.models.pojos.User;
import com.apman.utils.CreateUserModal;
import com.apman.utils.UpdateUserModal;
import com.apman.utils.Dialogs.ConfirmationDialog;
import com.apman.utils.Dialogs.InfoDialog;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class UserListController implements Initializable {

    @FXML
    private Button createUserButton;

    @FXML
    private Pagination pagination;

    private TableView<UserFX> table = new TableView<>();
    private TableColumn<UserFX, String> emailColumn = new TableColumn<>("Email");
    private TableColumn<UserFX, String> idColumn = new TableColumn<>("ID");
    private TableColumn<UserFX, String> nameColumn = new TableColumn<>("Name");
    private TableColumn<UserFX, Role> roleColumn = new TableColumn<>("Role");
    private TableColumn<UserFX, Void> actionsColumn = new TableColumn<>("Actions");

    {
        idColumn.setMinWidth(50);
        idColumn.setMaxWidth(Double.MAX_VALUE);
        idColumn.setCellValueFactory(new PropertyValueFactory<UserFX, String>("id"));
        table.getColumns().add(idColumn);

        nameColumn.setMinWidth(200);
        nameColumn.setMaxWidth(Double.MAX_VALUE);
        nameColumn.setCellValueFactory(new PropertyValueFactory<UserFX, String>("name"));
        table.getColumns().add(nameColumn);

        emailColumn.setMinWidth(200);
        emailColumn.setMaxWidth(Double.MAX_VALUE);
        emailColumn.setCellValueFactory(new PropertyValueFactory<UserFX, String>("email"));
        table.getColumns().add(emailColumn);

        roleColumn.setMinWidth(100);
        roleColumn.setMaxWidth(Double.MAX_VALUE);
        roleColumn.setCellValueFactory(new PropertyValueFactory<UserFX, Role>("role"));
        roleColumn.setCellFactory(roleColumnCellFactory());
        table.getColumns().add(roleColumn);

        actionsColumn.setMinWidth(300);
        actionsColumn.setMaxWidth(Double.MAX_VALUE);
        actionsColumn.setCellFactory(actionsColumnCellFactory());
        table.getColumns().add(actionsColumn);

    }

    private SessionFactory sessionFactory = App.getSessionFactory();
    private ObservableList<UserFX> tableItems = FXCollections.observableArrayList();

    private int ROWS_PER_PAGE = 20;


    private List<User> loadUsers() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> criteria = builder.createQuery(User.class);
            
            Root<User> root = criteria.from(User.class);
            root.fetch("role", JoinType.LEFT);
            
            criteria.select(root).where(builder.equal(root.get("deleted"), false));
            List<User> users = session.createQuery(criteria).getResultList();
            return users;

        } catch (Exception e) {
            System.out.println("[UserListController.loadUsers] Fetch users failed");
            System.out.println(e);
            return null;
        } finally {
            session.close();
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
        
            createUserButton.setOnAction(createUserEventHandler());
            tableItems.setAll(UserFX.from(loadUsers()));
            pagination.setPageCount((int) Math.ceil((double) tableItems.size() / ROWS_PER_PAGE));
            pagination.setPageFactory( pageNumber -> {
                int fromIndex = pageNumber * ROWS_PER_PAGE;
                int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, tableItems.size());
                table.getItems().setAll(tableItems.subList(fromIndex, toIndex));
                return table;
            });
        } catch (Exception e) {
            System.out.println("[UserListController.initialize] failed");
            System.out.println(e);
        }
    }


    private Callback<TableColumn<UserFX, Void>, TableCell<UserFX, Void>> actionsColumnCellFactory() {
        return e -> {
            return new TableCell<>(){

                private final Button updateButton = new Button("Update");
                private final Button deleteButton = new Button("Delete");

                private final HBox buttons = new HBox();
                {
                    updateButton.setOnAction(e -> {
                        UpdateUserModal.show(this.getTableView().getItems().get(getIndex()));
                    });

                    
                    deleteButton.setOnAction(e -> {
                        new ConfirmationDialog("Delete user", "Are you sure you want to delete this user?")
                            .showAndWait()
                            .ifPresent(response -> {
                                if (response == true) {
                                    Session session = null;
                                    try {
                                        session = sessionFactory.openSession();
                                        Transaction transaction = session.beginTransaction();
            
                                        UserFX selectedUser = table.getItems().get(getIndex());
                                        selectedUser.deleteUser();
                                        session.merge(selectedUser.getUserModel());
                                        
                                        Log log = Log.Factory.userDeleteLog(App.getCurrentUser(), selectedUser.getUserModel());
                                        session.persist(log);
                                        
                                        table.getItems().remove(selectedUser);
                                        transaction.commit();
            
                                        InfoDialog.show("Success", "Deletion success");
                                    } catch(Exception ex) {
                                        System.out.println(ex);
                                        InfoDialog.show("Failed", "Deletion failed");
                                    } finally {
                                        session.close();
                                    }
                                }
                            });
                    });

                    buttons.getChildren().addAll(updateButton, deleteButton);
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText("");
                        setGraphic(buttons);
                    }
                }
            };
        };
    }


    private EventHandler<ActionEvent> createUserEventHandler() {
        return e -> {
            CreateUserModal.show(newUser -> {
                table.getItems().add(UserFX.from(newUser));
                return null;
            });
        };
    }


    private Callback<TableColumn<UserFX, Role>, TableCell<UserFX, Role>> roleColumnCellFactory() {
        return e -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(Role item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item.getName());
                    }
                }
            };
        };
    }


}
