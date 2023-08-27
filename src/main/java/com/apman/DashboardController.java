package com.apman;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import javax.sound.midi.MidiDevice.Info;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import com.apman.models.fx.InvoiceFX;
import com.apman.models.fx.InvoiceStatus;
import com.apman.models.fx.UnitFX;
import com.apman.models.pojos.Invoice;
import com.apman.models.pojos.Tenant;
import com.apman.models.pojos.Unit;
import com.apman.utils.Dialogs.InfoDialog;
import com.infobip.ApiException;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

public class DashboardController implements Initializable {

    
    @FXML
    private Label pendingInvoicesCountLabel;
    
    @FXML
    private Label paidInvoicesCountLabel;

    @FXML
    private Label overdueInvoicesCountLabel;

    @FXML
    private ComboBox<InvoiceStatus> statusFilterComboBox;

    @FXML
    private ComboBox<Tenant> tenantFilterComboBox;

    @FXML
    private ComboBox<Unit> unitFilterComboBox;

    @FXML
    private Button clearStatusFilterButton;

    @FXML
    private Button clearTenantFilterButton;

    @FXML
    private Button clearUnitFilterButton;

    @FXML
    private Pagination pagination;

    private TableView<InvoiceFX> table = new TableView<>();
    private TableColumn<InvoiceFX, Double> amountColumn = new TableColumn<>("Amount");
    private TableColumn<InvoiceFX, LocalDate> dateIssuedColumn = new TableColumn<>("Date Issued");
    private TableColumn<InvoiceFX, LocalDate> dueDateColumn = new TableColumn<>("Due Date");
    private TableColumn<InvoiceFX, Tenant> tenantColumn = new TableColumn<>("Tenant");
    private TableColumn<InvoiceFX, Unit> unitColumn = new TableColumn<>("Unit");    
    private TableColumn<InvoiceFX, Void> actionsColumn = new TableColumn<>("Action");
    private TableColumn<InvoiceFX, InvoiceStatus> statusColumn = new TableColumn<>("Status");
    
    {
                        
        statusColumn.setMinWidth(50);
        statusColumn.setMaxWidth(Double.MAX_VALUE);
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().add(statusColumn);

        amountColumn.setMinWidth(100);
        amountColumn.setMaxWidth(Double.MAX_VALUE);
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        table.getColumns().add(amountColumn);
        
        dateIssuedColumn.setMinWidth(200);
        dateIssuedColumn.setMaxWidth(Double.MAX_VALUE);
        dateIssuedColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateIssuedColumn.setCellFactory(buildDateIssuedColumnCellFactory());
        table.getColumns().add(dateIssuedColumn);
        
        dueDateColumn.setMinWidth(200);
        dueDateColumn.setMaxWidth(Double.MAX_VALUE);
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dueDateColumn.setCellFactory(buildDueDateColumnCellFactory());
        table.getColumns().add(dueDateColumn);
        
        tenantColumn.setMinWidth(200);
        tenantColumn.setMaxWidth(Double.MAX_VALUE);
        tenantColumn.setCellValueFactory(new PropertyValueFactory<>("tenant"));
        tenantColumn.setCellFactory(buildTenantColumnCellFactory());
        table.getColumns().add(tenantColumn);
        
        unitColumn.setMinWidth(200);
        unitColumn.setMaxWidth(Double.MAX_VALUE);
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        unitColumn.setCellFactory(buildUnitColumnCellFactory());
        table.getColumns().add(unitColumn);

        actionsColumn.setMinWidth(300);
        actionsColumn.setMaxWidth(Double.MAX_VALUE);
        actionsColumn.setCellFactory(e -> {
            return new TableCell<>() {
                
                private Button resendInvoice = new Button("Resend");
                {
                    final PopOver popOver = new PopOver();
                    final Button sendViaEmailButton = new Button("Send via Email");
                    {
                        sendViaEmailButton.setOnAction(e -> {
                            InvoiceFX invoiceFX = getTableView().getItems().get(getIndex());
                            try {
                                invoiceFX.sendViaEmail();

                            } catch (Exception ex) {
                                System.out.println("[resendInvoice] " + ex.getClass().getName() + " [" + ex.getMessage() + "]");
                            } finally {
                                popOver.hide();
                                InfoDialog.show("Success", "Invoice sent via Email");
                            }
                        });
                    }
                    final Button sendViaSMSButton = new Button("Send via SMS");
                    {
                        sendViaSMSButton.setOnAction(e -> {
                            InvoiceFX invoiceFX = getTableView().getItems().get(getIndex());
                            try {
                                invoiceFX.sendViaSMS();

                            } catch(ApiException apiException) {
                                System.out.println("[resendInvoice] " + apiException.getClass().getName() + " [" + apiException.getMessage() + "]");
                            } finally {
                                popOver.hide();
                                InfoDialog.show("Success", "Invoice sent via SMS");
                            }
                        });
                    }
                    
                    final HBox hbox = new HBox();
                    hbox.getChildren().addAll(sendViaEmailButton, sendViaSMSButton);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    hbox.setSpacing(10);

                    setGraphic(hbox);
                    popOver.setContentNode(hbox);

                    resendInvoice.setOnAction(e -> {
                        popOver.show(resendInvoice);
                    });

                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(null, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(resendInvoice);
                    }
                }
            };
        });
        table.getColumns().add(actionsColumn);
        
        
    }

    private SessionFactory sessionFactory = App.getSessionFactory();
    private final int ROWS_PER_PAGE = 10;

    private ObservableList<InvoiceFX> tableItems;    
    private FilteredList<InvoiceFX> filteredTableItems;

    private Predicate<InvoiceFX> statusFilterPredicate = i -> true;
    private Predicate<InvoiceFX> tenantFilterPredicate = i -> true;
    private Predicate<InvoiceFX> unitFilterPredicate = i -> true;

    private List<Tenant> tenants;


    private List<Tenant> getTenants() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Tenant> criteria = builder.createQuery(Tenant.class);
            Root<Tenant> root = criteria.from(Tenant.class);
            root.fetch("units", JoinType.LEFT);
            criteria.select(root).where(builder.equal(root.get("deleted"), false));
            Query<Tenant> query = session.createQuery(criteria);
            return query.getResultList();

        } catch (Exception e) {
            return new ArrayList<>();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Session session = null;
        try {
            tableItems = InvoiceFX.from(getInvoices());

            pendingInvoicesCountLabel.setText(tableItems.stream().filter(i -> i.getStatus() == InvoiceStatus.PENDING).count() + "");
            paidInvoicesCountLabel.setText(tableItems.stream().filter(i -> i.getStatus() == InvoiceStatus.PAID).count() + "");
            overdueInvoicesCountLabel.setText(tableItems.stream().filter(i -> i.getStatus() == InvoiceStatus.OVERDUE).count() + "");

            session = sessionFactory.openSession();
            tenants = getTenants();
            

            // Action was getting triggered 3 times; i don't know why
            tenantFilterComboBox.setItems(FXCollections.observableArrayList(tenants));
            tenantFilterComboBox.setOnAction(this::tenantFilterComboBoxHandler);
            clearTenantFilterButton.setOnAction(e -> {
                System.out.println("Clearing tenant filter");
                
                // clear tenant filter
                tenantFilterComboBox.setOnAction(null);
                tenantFilterComboBox.getSelectionModel().clearSelection();
                tenantFilterPredicate = i -> true;
                tenantFilterComboBox.setOnAction(this::tenantFilterComboBoxHandler);

                tenantFilterComboBox.setPromptText("Select a Tenant");

                // clear unit filter

                unitFilterComboBox.setOnAction(null);
                unitFilterComboBox.getSelectionModel().clearSelection();
                unitFilterPredicate = i -> true;
                unitFilterComboBox.setOnAction(this::unitFilterComboBoxHandler);

                unitFilterComboBox.setDisable(true);
                
                pagination.setPageFactory(this::pageFactory);
                unitFilterComboBox.setPromptText("Select a tenant first");
            });
            
            unitFilterComboBox.setOnAction(this::unitFilterComboBoxHandler);
            clearUnitFilterButton.setOnAction( e -> {
                System.out.println("Clearing unit filter");
                unitFilterPredicate = i -> true;   

                unitFilterComboBox.setOnAction(null);             
                unitFilterComboBox.getSelectionModel().clearSelection();
                unitFilterComboBox.setOnAction(this::unitFilterComboBoxHandler);

                pagination.setPageFactory(this::pageFactory);

                unitFilterComboBox.setPromptText("All");
            });
            unitFilterComboBox.setDisable(true);
            
            
            statusFilterComboBox.setItems(FXCollections.observableArrayList(InvoiceStatus.values()));
            statusFilterComboBox.setCellFactory(buildStatusFilterComboBoxCellFactory());
            statusFilterComboBox.setOnAction(this::statusFilterComboBoxHandler);
            clearStatusFilterButton.setOnAction(e -> {
                statusFilterPredicate = i -> true;

                statusFilterComboBox.setOnAction(null);
                statusFilterComboBox.getSelectionModel().clearSelection();
                statusFilterComboBox.setOnAction(this::statusFilterComboBoxHandler);

                pagination.setPageFactory(this::pageFactory);
                statusFilterComboBox.setPromptText("All");
            });

            
            int pageCount = getPageCount();
            pagination.setPageCount(pageCount);
            pagination.setPageFactory(this::pageFactory);
            

        } catch (Exception exception) {
            System.out.println("dashboard initialization error");
        } finally {
            if (session != null) {
                session.close();
            }
        }
    
    }

    
    private Node pageFactory(Integer pageIndex) {
        try {
            filteredTableItems =  tableItems.filtered(tenantFilterPredicate.and(unitFilterPredicate).and(statusFilterPredicate));
            filteredTableItems.sorted(table.comparatorProperty().get());
            
            int fromIndex = pageIndex * ROWS_PER_PAGE;
            int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredTableItems.size());
            table.getItems().setAll(filteredTableItems.subList(fromIndex, toIndex));
            return table;

        } catch (Exception e) {
            System.out.println("[pagination.setPageFactory] Error setting page factory");
            return null;
        }
    };



    private int getPageCount() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            Root<Invoice> root = criteriaQuery.from(Invoice.class);
            criteriaQuery.select(criteriaBuilder.count(root));
            Query<Long> query = session.createQuery(criteriaQuery);
    
            Long totalCount = query.getSingleResult();
    
            return (int) Math.ceil((double) totalCount /  ROWS_PER_PAGE);

        } catch (Exception e) {
            System.out.println("Error getting page count");
            return 0;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }


    private List<Invoice> getInvoices() {
        Session session = null;
        try {
            session = sessionFactory.openSession();

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Invoice> criteriaQuery = criteriaBuilder.createQuery(Invoice.class);
            Root<Invoice> root = criteriaQuery.from(Invoice.class);
            root.fetch("unit", JoinType.LEFT);
            root.fetch("tenant", JoinType.LEFT);
            criteriaQuery.orderBy(criteriaBuilder.desc(root.get("date")));
    
            Query<Invoice> query = session.createQuery(criteriaQuery);
            List<Invoice> invoices = query.getResultList();

            return invoices;
        } catch (Exception e) {
            System.out.println("[getInvoices] Error message: "  + e.getMessage());
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }


    private Callback<ListView<InvoiceStatus>, ListCell<InvoiceStatus>> buildStatusFilterComboBoxCellFactory() {
        return e -> {
            return new ListCell<>(){
                @Override
                protected void updateItem(InvoiceStatus item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.toString());
                    }
                }
            };
        };
    }

    private void statusFilterComboBoxHandler(ActionEvent event) {
        try {
            System.out.println("statusFilterComboBoxHandler fired");
            InvoiceStatus selectedStatus = statusFilterComboBox.getSelectionModel().getSelectedItem();
            System.out.println("selectedStatus: " + selectedStatus);
            if (selectedStatus == null) {
                statusFilterPredicate = invoiceFX -> true;
            } else {
                statusFilterPredicate = invoiceFX -> invoiceFX.getStatus() == selectedStatus;
            }
            pagination.setPageFactory(this::pageFactory);

        } catch (Exception e) {
            System.out.println("[statusFilterComboBoxHandler] Error message: "  + e.getMessage());
        }
    }

    private void tenantFilterComboBoxHandler(ActionEvent e) {
        try {
                System.out.println("tenantFilterComboBoxHandler fired");
                Tenant selectedTenant = tenantFilterComboBox.getSelectionModel().getSelectedItem();
                if (selectedTenant != null) {

                    // a tenant gets selected, so we enable the unit filter combo box
                    unitFilterComboBox.setDisable(false);
                    unitFilterComboBox.setPromptText("All tenant's units");
                    unitFilterPredicate = invoiceFX -> true;

                    List<Unit> tenantUnits = selectedTenant.getUnits();
                    System.out.println("tenantUnits: " + tenantUnits.size());
                    unitFilterComboBox.setItems(FXCollections.observableArrayList(tenantUnits));
                    
                    tenantFilterPredicate = invoiceFX -> {
                        if (invoiceFX.getTenant() == null) { // this should not happen
                            throw new NullPointerException("Tenant is null");
                        } else { // we set the unit filter combo box items to the units of the selected tenant
                            return invoiceFX.getTenant().equals(selectedTenant);
                        }
                    }; 

                    // we set the unit filter predicate to true, so that all invoices related to the selected tenant are shown
                    pagination.setPageFactory(this::pageFactory);
                } else {
                    unitFilterPredicate = invoiceFX -> true;
                    unitFilterComboBox.setDisable(true);
                }
        } catch (Exception ex) {
            System.out.println("[tenantFilterComboBoxHandler] Error message: ");
        }
    }

    private void unitFilterComboBoxHandler(ActionEvent e) {
        try {
                Unit selectedUnit = unitFilterComboBox.getSelectionModel().getSelectedItem();
                if (selectedUnit != null) {
                    unitFilterPredicate = invoiceFX -> invoiceFX.getUnit().getId() == selectedUnit.getId();
                } else {
                    unitFilterPredicate = invoiceFX -> true;
                }
                pagination.setPageFactory(this::pageFactory);
        } catch (Exception ex) {
            System.out.println("[tenantFilterComboBoxHandler] Error message: ");
        }
    }


    private Callback<TableColumn<InvoiceFX, Unit>, TableCell<InvoiceFX, Unit>> buildUnitColumnCellFactory() {
        return e -> {
            return new TableCell<>(){

                @Override
                protected void updateItem(Unit item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            };
        };
    }

    private Callback<TableColumn<InvoiceFX, Tenant>, TableCell<InvoiceFX, Tenant>> buildTenantColumnCellFactory() {
        return e -> {
            return new TableCell<>(){
                
                @Override
                protected void updateItem(Tenant item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            };
        };
    }

    private Callback<TableColumn<InvoiceFX, LocalDate>, TableCell<InvoiceFX, LocalDate>> buildDueDateColumnCellFactory() {
        return e -> {
            return new TableCell<>(){

                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                        String dueDateString = item.plusDays(5).format(formatter);
                        setText(dueDateString);
                    }
                }
            };
        };
    }

    private Callback<TableColumn<InvoiceFX, LocalDate>, TableCell<InvoiceFX, LocalDate>> buildDateIssuedColumnCellFactory() {
        return e -> {
            return new TableCell<>(){
                
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                        String dateIssuedString = item.format(formatter);
                        setText(dateIssuedString);
                        setGraphic(null);
                    }
                }
            };
        };
    }

}
