package com.apman;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.apman.models.fx.RoleFX;
import com.apman.models.pojos.Log;
import com.apman.models.pojos.Role;
import com.apman.utils.CreateRoleModal;
import com.apman.utils.UpdateRoleModal;
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
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.scene.control.cell.*;

public class RoleListController implements Initializable {

    @FXML
    private Button createRoleButton;

    @FXML
    private Pagination pagination;
    
    
    private SessionFactory sessionFactory = App.getSessionFactory();
    
    
    private TableView<RoleFX> table = new TableView<RoleFX>();
    private TableColumn<RoleFX, Long> idColumn = new TableColumn<RoleFX, Long>("ID");
    private TableColumn<RoleFX, String> nameColumn = new TableColumn<RoleFX, String>("Name");
    private TableColumn<RoleFX, String> actionsColumn = new TableColumn<RoleFX, String>("Actions");
    ObservableList<RoleFX> tableData = FXCollections.observableArrayList();

    {
        idColumn.setMinWidth(100);
        idColumn.setMaxWidth(Double.MAX_VALUE);
        idColumn.setCellValueFactory(new PropertyValueFactory<RoleFX, Long>("id"));

        nameColumn.setMinWidth(200);
        nameColumn.setMaxWidth(Double.MAX_VALUE);
        nameColumn.setCellValueFactory(new PropertyValueFactory<RoleFX, String>("name"));

        actionsColumn.setMinWidth(400);
        actionsColumn.setMaxWidth(Double.MAX_VALUE);
        actionsColumn.setCellFactory(actionsColumnCellFactory());

        table.getColumns().add(idColumn);
        table.getColumns().add(nameColumn);
        table.getColumns().add(actionsColumn);
    }

    private final int ITEMS_PER_PAGE = 20;

    private ObservableList<RoleFX> loadRoleFXs() {
        try {
            Session session = sessionFactory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
            Root<Role> root = criteria.from(Role.class);
            root.fetch("users", JoinType.LEFT);
            criteria.select(root).where(builder.equal(root.get("deleted"), false));
            Query<Role> query = session.createQuery(criteria);
            List<Role> roles = query.getResultList();
            var result = RoleFX.from(roles);
            session.close();
            return result;

        } catch (Exception e) {
            System.out.println("[loadRoleFXs]" + e.getMessage());
            return null;
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            createRoleButton.setOnAction(createUserEventHandler());
            tableData.setAll(loadRoleFXs());
            pagination.setPageCount((int) Math.ceil((double) tableData.size() / ITEMS_PER_PAGE));
            pagination.setPageFactory(pageIndex -> {
                int fromIndex = pageIndex * ITEMS_PER_PAGE;
                int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, tableData.size());
                table.getItems().addAll(tableData.subList(fromIndex, toIndex));
                return table;
            });
            
        } catch (Exception e) {
            System.out.println("[RoleListController.initialize]" + e.getMessage());
        }

    }
    


    private Callback<TableColumn<RoleFX, String>, TableCell<RoleFX, String>> actionsColumnCellFactory() {
        return e -> {
            return new TableCell<>(){

                private final Button updateButton = new Button("Update");
                {
                    updateButton.setOnAction(actionEvent -> {
                        RoleFX role = this.getTableView().getItems().get(getIndex());
                        UpdateRoleModal.show(role);
                    });
                }

                private final Button deleteButton = new Button("Delete");
                {
                    deleteButton.setOnAction(actionEvent -> {
                        new ConfirmationDialog("Delete Role", "Are you sure you want to delete this role?")
                        .showAndWait()
                        .ifPresent(response -> {
                            if (response == true) {
                                Session session = null;
                                try {
                                    session = sessionFactory.openSession();
                                    RoleFX roleFX = this.getTableView().getItems().get(getIndex());
                                    session.beginTransaction();
                                    
                                    roleFX.deleteRole();
                                    session.merge(roleFX.getRoleModel());

                                    Log log = Log.Factory.roleDeleteLog(App.getCurrentUser(), roleFX.getRoleModel());
                                    session.persist(log);
                                    table.getItems().remove(getIndex());
                                    session.getTransaction().commit();

                                    InfoDialog.show("Success", "Role deleted successfully");
                                } catch (Exception e) {
                                        System.out.println("Error: " + e.getMessage());
                                        InfoDialog.show("Error", "failed deleting role");
                                } finally {
                                    if (session != null) {
                                        session.close();
                                    }
                                }
                            }
                        });
                    });
                }

                private final HBox buttons = new HBox();
                {
                    buttons.getChildren().addAll(updateButton, deleteButton);
                }

                @Override
                protected void updateItem(String item, boolean empty) {
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
            CreateRoleModal.show((Role newRole) -> {
                table.getItems().add(RoleFX.from(newRole));
                return null;
            });
        };
    }



}
