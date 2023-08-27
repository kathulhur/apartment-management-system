package com.apman;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.apman.models.fx.TenantFX;
import com.apman.models.pojos.Log;
import com.apman.models.pojos.Tenant;
import com.apman.models.pojos.Unit;
import com.apman.utils.CreateTenantModal;
import com.apman.utils.InvoiceListWindow;
import com.apman.utils.UpdateTenantModal;
import com.apman.utils.UpdateTenantUnitModal;
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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class TenantListController implements Initializable{
    private SessionFactory sessionFactory = App.getSessionFactory();
    
    @FXML
    private Button createTenantButton;

    @FXML
    private Pagination pagination;

    private TableView<TenantFX> table = new TableView<TenantFX>();
    private TableColumn<TenantFX, String> idColumn = new TableColumn<TenantFX, String>("ID");
    private TableColumn<TenantFX, String> nameColumn = new TableColumn<TenantFX, String>("Name");
    private TableColumn<TenantFX, List<Unit>> unitsColumn = new TableColumn<TenantFX, List<Unit>>("Units");
    private TableColumn<TenantFX, Void> actionsColumn = new TableColumn<TenantFX, Void>("Actions");
    {
        idColumn.setMinWidth(50);
        idColumn.setMaxWidth(Double.MAX_VALUE);
        idColumn.setCellValueFactory(new PropertyValueFactory<TenantFX, String>("id"));
        table.getColumns().add(idColumn);

        nameColumn.setMinWidth(200);
        nameColumn.setMaxWidth(Double.MAX_VALUE);
        nameColumn.setCellValueFactory(new PropertyValueFactory<TenantFX, String>("name"));
        table.getColumns().add(nameColumn);

        unitsColumn.setMinWidth(200);
        unitsColumn.setMaxWidth(Double.MAX_VALUE);
        unitsColumn.setCellValueFactory(new PropertyValueFactory<TenantFX, List<Unit>>("units"));
        unitsColumn.setCellFactory(buildUnitsColumnCellFactory());
        table.getColumns().add(unitsColumn);

        actionsColumn.setMinWidth(400);
        actionsColumn.setMaxWidth(Double.MAX_VALUE);
        actionsColumn.setCellFactory(buildActionsColumnCellFactory());
        table.getColumns().add(actionsColumn);

    }
    
    
    
    private ObservableList<TenantFX> tableItems = FXCollections.observableArrayList();

    private final int ROWS_PER_PAGE = 20;

    private List<Tenant> loadTenantRecords() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Tenant> criteria = builder.createQuery(Tenant.class);
            Root<Tenant> root = criteria.from(Tenant.class);
            root.fetch("units", JoinType.LEFT);
            criteria.select(root).where(builder.equal(root.get("deleted"), false));
            Query<Tenant> query = session.createQuery(criteria);
            List<Tenant> tenants = query.getResultList();
            
            return tenants;
        } catch (Exception e) {
            System.out.println("[loadTenantRecords] error occured");
            System.out.println(e);
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
        
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            createTenantButton.setOnAction(buildCreateTenantButtonHandler());
            
            tableItems.setAll(TenantFX.from(loadTenantRecords()));

            pagination.setPageCount((int) Math.ceil((double) tableItems.size() / ROWS_PER_PAGE));
            pagination.setPageFactory( pageNumber -> {
                int fromIndex = pageNumber * ROWS_PER_PAGE;
                int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, tableItems.size());
                table.getItems().setAll(tableItems.subList(fromIndex, toIndex));
                return table;
            });

        } catch (Exception e) {
            table.setItems(null);
            System.out.println(e);
        }
        
        
    }

    private EventHandler<ActionEvent> buildCreateTenantButtonHandler() {
        return e -> {
            CreateTenantModal.show( (newTenant) -> {
                table.getItems().add(new TenantFX(newTenant));
                return null;
            });
        };
    }

    private Callback<TableColumn<TenantFX, List<Unit>>, TableCell<TenantFX, List<Unit>>> buildUnitsColumnCellFactory() {
        return arg -> {
                return new TableCell<TenantFX, List<Unit>>() {
                    
                    @Override
                    protected void updateItem(List<Unit> items, boolean empty) {
                        VBox vbox = new VBox();
                        super.updateItem(items, empty);

                        if(empty || items == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            int itemsSize = items.size();
                            ObservableList<Node> vBoxChildren = vbox.getChildren();

                            if (itemsSize > 0) {
                                for (int i = 0; i < items.size() - 1; i++) {
                                    vBoxChildren.add(0, new Label(items.get(0).getName()));
                                }
                                
                                vBoxChildren.add(0, new Label(items.get(itemsSize-1).getName()));
                            }
                            setText("");
                            setGraphic(vbox);
                        }
                    }

                };

            };
    }

    private Callback<TableColumn<TenantFX, Void>, TableCell<TenantFX, Void>> buildActionsColumnCellFactory() {
        return arg -> {

                return new TableCell<TenantFX, Void>() {
                    private final Button assignToUnitButton = new Button("Assign to Unit");
                    {
                        assignToUnitButton.setOnAction((event) -> {
                            TenantFX tenant = getTableView().getItems().get(getIndex());
                            UpdateTenantUnitModal.show(tenant);
                            
                        });
                    }

                    private final Button updateButton = new Button("Update");
                    {
                        updateButton.setOnAction((event) -> {
                            TenantFX tenant = getTableView().getItems().get(getIndex());
                            UpdateTenantModal.show(tenant);
                            
                        });
                    }

                    private final Button deleteButton = new Button("Delete");
                    {
                        deleteButton.setOnAction((event) -> {
                            new ConfirmationDialog("Delete Tenant", "Are you sure you want to delete this tenant?")
                            .showAndWait()
                            .ifPresent(response -> {
                                if (response == true) {
                                    Session session = null;
                                    try {
                                        session = sessionFactory.openSession();
                                        Transaction transaction = session.beginTransaction();
            
                                        TenantFX tenantFX = getTableView().getItems().get(getIndex());
                                        tenantFX.delete();
                                        session.merge(tenantFX.getTenantModel()); 

                                        Log log = Log.Factory.tenantDeleteLog(App.getCurrentUser(), tenantFX.getTenantModel());
                                        session.persist(log);

                                        
                                        transaction.commit();
                                        table.getItems().remove(getIndex());
                                        
                                    } catch (Exception exception) {
                                        System.out.println(exception.getMessage());
                                        InfoDialog.show("Error", "An error occured while deleting the tenant");
                                    } finally {
                                        if (session != null) {
                                            session.close();
                                        }
                                    }
                                }
                            });
                                
                        });
                    }

                    private final Button viewInvoicesButton = new Button("view invoices");
                    {
                        viewInvoicesButton.setOnAction((event) -> {
                            TenantFX tenant = getTableView().getItems().get(getIndex());
                            InvoiceListWindow.show(tenant);
                        });
                    }

                    private final HBox buttons = new HBox(assignToUnitButton, updateButton, deleteButton, viewInvoicesButton);

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(buttons);
                        }
                    }
                };
            };
    }


    


}
