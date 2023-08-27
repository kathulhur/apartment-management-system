package com.apman;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import org.controlsfx.control.PopOver;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.apman.models.fx.InvoiceFX;
import com.apman.models.fx.LogFX;
import com.apman.models.fx.UnitFX;
import com.apman.models.pojos.Invoice;
import com.apman.models.pojos.Log;
import com.apman.models.pojos.Tenant;
import com.apman.models.pojos.Unit;
import com.apman.services.Email;
import com.apman.utils.CreateUnitModal;
import com.apman.utils.UpdateUnitModal;
import com.apman.utils.Dialogs.ConfirmationDialog;
import com.apman.utils.Dialogs.InfoDialog;
import com.infobip.ApiException;

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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class UnitListController implements Initializable {

    @FXML
    private Pagination pagination;
    
    @FXML
    private Button createUnitButton;

    @FXML Label occupiedUnitsCountLabel;

    @FXML Label vacantUnitsCountLabel;

    private TableView<UnitFX> table = new TableView<UnitFX>();
    private TableColumn<UnitFX, LocalDate> indicatorsColumn = new TableColumn<>("");
    private TableColumn<UnitFX, String> id = new TableColumn<>("ID");
    private TableColumn<UnitFX, String> name = new TableColumn<>("Name");
    private TableColumn<UnitFX, Tenant> tenant = new TableColumn<>("Tenant");
    private TableColumn<UnitFX, String> rateColumn = new TableColumn<>("Rate");
    private TableColumn<UnitFX, Tenant> status = new TableColumn<>("Status");
    private TableColumn<UnitFX, Void> actionsColumn = new TableColumn<>("Actions");
    private GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
    
    {
        indicatorsColumn.setMinWidth(50);
        indicatorsColumn.setMaxWidth(Double.MAX_VALUE);
        indicatorsColumn.setCellValueFactory(new PropertyValueFactory<UnitFX, LocalDate>("nextInvoiceIssuance"));
        indicatorsColumn.setCellFactory( tableColumn -> {
            return new TableCell<>() {
                Tooltip tooltip = new Tooltip();
                @Override
                protected void updateItem(LocalDate nextInvoiceIssuance, boolean empty) {
                    super.updateItem(nextInvoiceIssuance, empty);
                    
                    if (empty || nextInvoiceIssuance == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        LocalDate now = LocalDate.now();


                        if (
                            nextInvoiceIssuance.isAfter(now.minusDays(5)) &&
                            nextInvoiceIssuance.isBefore(now.plusDays(5))
                        ) {
                            setGraphic(fontAwesome.create(FontAwesome.Glyph.PAPER_PLANE));
                            tooltip.setText("Invoice needs to be issued for this unit");
                            Tooltip.install(getGraphic(), tooltip);
                        } else if (nextInvoiceIssuance.isBefore(now)) {
                            tooltip.setText("Invoice is overdue for this unit, please issue it as soon as possible");
                            setGraphic(fontAwesome.create(FontAwesome.Glyph.PAPER_PLANE).color(Color.ORANGERED));
                            tooltip.getStyleClass().add("tooltip-warning");
                            Tooltip.install(getGraphic(), tooltip);
                        } else {
                            setGraphic(null);
                        }
                    }
                }
            };
        });
        table.getColumns().add(indicatorsColumn);

        id.setMinWidth(50);
        id.setMaxWidth(Double.MAX_VALUE);
        id.setCellValueFactory(new PropertyValueFactory<UnitFX, String>("id"));
        table.getColumns().add(id);

        name.setMinWidth(200);
        name.setMaxWidth(Double.MAX_VALUE);
        name.setCellValueFactory(new PropertyValueFactory<UnitFX, String>("name"));
        table.getColumns().add(name);
  
        rateColumn.setMinWidth(80);
        rateColumn.setMaxWidth(Double.MAX_VALUE);
        rateColumn.setCellValueFactory(new PropertyValueFactory<UnitFX, String>("rate"));
        table.getColumns().add(rateColumn);
    
        tenant.setMinWidth(200);
        tenant.setMaxWidth(Double.MAX_VALUE);
        tenant.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<UnitFX, Tenant>("tenant"));
        tenant.setCellFactory(buildTenantCellFactory());
        table.getColumns().add(tenant);

        status.setMinWidth(50);
        status.setMaxWidth(Double.MAX_VALUE);
        status.setCellValueFactory(new PropertyValueFactory<UnitFX, Tenant>("tenant"));
        status.setCellFactory(buildStatusCellFactory());
        table.getColumns().add(status);
    
        actionsColumn.setMinWidth(400);
        actionsColumn.setMaxWidth(Double.MAX_VALUE);
        actionsColumn.setCellFactory(buildActionsColumnCellFactory());
        table.getColumns().add(actionsColumn);
        
    }

    private SessionFactory sessionFactory = App.getSessionFactory();
    private final int ROWS_PER_PAGE = 20;
    private ObservableList<UnitFX> tableItems = FXCollections.observableArrayList();

    private List<Unit> loadUnits() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Unit> criteria = builder.createQuery(Unit.class);

            Root<Unit> root = criteria.from(Unit.class);
            root.fetch("tenant", JoinType.LEFT);
            
            criteria.select(root).where(builder.equal(root.get("deleted"), false));
            Query<Unit> query = session.createQuery(criteria);
            List<Unit> units = query.getResultList();

            return units;
        } catch (Exception e) {
            System.out.println("[UnitListController.loadUnits] Error");
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
            createUnitButton.setOnAction(buildCreateUnitHandler());
            tableItems.setAll(UnitFX.from(loadUnits()));
            vacantUnitsCountLabel.setText(String.valueOf(tableItems.stream().filter(unit -> unit.getTenant() == null).count()));
            occupiedUnitsCountLabel.setText(String.valueOf(tableItems.stream().filter(unit -> unit.getTenant() != null).count()));



            pagination.setPageCount((int) Math.ceil((double) tableItems.size() / ROWS_PER_PAGE));
            pagination.setPageFactory(buildPaginationFactory());
        } catch (Exception e) {
            System.out.println("[UnitListController.initialize] Error");
            System.out.println(e);
        }
    }

    private Callback<Integer, Node> buildPaginationFactory() {
        return pageIndex -> {
            int fromIndex = pageIndex * ROWS_PER_PAGE;
            int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, tableItems.size());
            table.getItems().setAll(tableItems.subList(fromIndex, toIndex));
            return table;
        };
    }

    private Callback<TableColumn<UnitFX, Void>, TableCell<UnitFX, Void>> buildActionsColumnCellFactory() {
        return e -> {
                return new TableCell<UnitFX, Void>() {


                    private final Button issueInvoiceButton = new Button("Issue Invoice");
                    {
                        final PopOver popOver = new PopOver();
                        final Button smsButton = new Button("Send via SMS");
                        final Button emailButton = new Button("Send via Email");
                        HBox hBox = new HBox();
                        hBox.setPadding(new Insets(10));
                        hBox.setSpacing(10);
                        hBox.getChildren().addAll(smsButton, emailButton);
                        hBox.setAlignment(Pos.CENTER);
                        popOver.setContentNode(hBox);

                        issueInvoiceButton.setOnAction(e -> {
                            popOver.show(issueInvoiceButton);
                        });

                        smsButton.setOnAction((e) -> {
                            Session session = null;
                            try {
                                session = sessionFactory.openSession();
                                session.beginTransaction();

                                // persist the new invoice
                                UnitFX selectedUnit = table.getItems().get(getIndex());
                                InvoiceFX newInvoice = new InvoiceFX(selectedUnit.issueInvoice());
                                newInvoice.sendViaSMS();
                                session.persist(newInvoice.getInvoiceModel());

                                // update the unit to reflect the persist the modified next invoice issuance date
                                session.merge(selectedUnit.getUnitModel());


                                if (selectedUnit.getNextInvoiceIssuance().plusMonths(30).isAfter(LocalDate.now())) {
                                    issueInvoiceButton.setDisable(true);
                                }

                                session.getTransaction().commit();
                                session.close();
                                
                                InfoDialog.show("Invoice issued successfully", "Success");
                            } catch(ApiException apiException) {
                                System.out.println("Error at sending invoice via sms");
                                InfoDialog.show("Error", "Failed sending invoice via sms");
                            } catch (Exception exception) {
                                System.out.println("Error at issue invoice button");
                                System.out.println(exception);
                                
                            } finally {
                                if (session != null) {
                                    session.close();
                                }
                            }
                        });

                        emailButton.setOnAction(e-> {
                            Session session = null;
                            try {
                                session = sessionFactory.openSession();
                                session.beginTransaction();

                                // persist the new invoice
                                UnitFX selectedUnit = table.getItems().get(getIndex());
                                InvoiceFX newInvoice = new InvoiceFX(selectedUnit.issueInvoice());
                                newInvoice.sendViaEmail();

                                session.persist(newInvoice.getInvoiceModel());


                                // update the unit to reflect the persist the modified next invoice issuance date
                                session.merge(selectedUnit.getUnitModel());


                                if (selectedUnit.getNextInvoiceIssuance().plusMonths(30).isAfter(LocalDate.now())) {
                                    issueInvoiceButton.setDisable(true);
                                }

                                session.getTransaction().commit();
                                session.close();
                                
                                InfoDialog.show("Invoice issued successfully", "Success");
                            } catch (ApiException apiException) {
                                System.out.println("Error at sending invoice via email");
                                System.out.println(apiException);
                                InfoDialog.show("Error", "Failed sending invoice via email");
                            }catch (Exception exception) {
                                System.out.println("Error at issue invoice button");
                                System.out.println(exception);
                                
                            } finally {
                                if (session != null) {
                                    session.close();
                                }
                            }
                        });
                        
                    }

                    private final Button clearUnitButton = new Button("Clear Unit");
                    {
                        clearUnitButton.setOnAction((e) -> {
                            new ConfirmationDialog("Remove tenant to unit", "Are you sure you want to clear this unit?").showAndWait().ifPresent(response -> {
                                if (response == true) {
                                    Session session = null;
                                    try {
                                        session = sessionFactory.openSession();
                                        Transaction transaction = session.beginTransaction();
                                        UnitFX selectedUnit = getTableView().getItems().get(getIndex());
                                        selectedUnit.removeTenant();
                                        session.merge(selectedUnit.getUnitModel());
                                        transaction.commit();

                                        clearUnitButton.setDisable(true);
                                        session.close();

                                        InfoDialog.show("Unit cleared successfully", "Success");
                                    } catch (Exception exception) {
                                        System.out.println("Error at clear unit button");
                                        System.out.println(exception);
                                    } finally {
                                        if (session != null) {
                                            session.close();
                                        }
                                    }
                                } 
                            });

                        });
                    }

                    private final Button updateButton = new Button("Update");
                    {
                        updateButton.setOnAction((e) -> {
                            try {
                                
                                UnitFX selectedUnit = table.getItems().get(getIndex());
                                UpdateUnitModal.show(selectedUnit);
                            } catch (Exception exception) {
                                System.out.println("Update Unit Modal failed...");
                                System.out.println(e);
                            }
                        });
                    }

                    private final Button deleteButton = new Button("Delete");
                    {
                        deleteButton.setOnAction((e) -> {
                            new ConfirmationDialog("Delete unit", "Are you sure you want to delete this unit?")
                                .showAndWait()
                                .ifPresent(response -> {
                                if (response == true) {
                                    Session session = null;
                                    try {
                                        session = sessionFactory.openSession();
                                        session.beginTransaction();

                                        UnitFX unit = getTableView().getItems().get(getIndex());
                                        unit.deleteUnit();
                                        session.merge(unit.getUnitModel());
                                        
                                        session.persist(Log.Factory.unitDeleteLog(App.getCurrentUser(), unit.getUnitModel()));

                                        table.getItems().remove(getIndex());
                                        
                                        // generate log
                                        Log log = LogFX.createLogUnitDelete(App.getCurrentUser(), unit.getUnitModel());
                                        session.persist(log);

                                        session.getTransaction().commit();
                                        session.close();
                                        
                                    } catch (Exception exception) {
                                        System.out.println("Delete failed...");
                                        System.out.println(exception);
                                        InfoDialog.show("Error", "Delete failed");
                                    } finally {
                                        if (session != null) {
                                            session.close();
                                        }
                                    }
                                }
                            });
                        });
                    }


                    private final HBox hBox = new HBox(issueInvoiceButton, updateButton, clearUnitButton, deleteButton);
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        }
                        else {
                            UnitFX unit = table.getItems().get(getIndex());

                            if (unit.getTenant() == null) {
                                clearUnitButton.setDisable(true);
                                issueInvoiceButton.setDisable(true);
                            }
                            LocalDate now = LocalDate.now();
                            LocalDate nextInvoiceIssuance = unit.getNextInvoiceIssuance();
                            if ( nextInvoiceIssuance != null && now.isBefore(nextInvoiceIssuance.minusDays(6))) {
                                issueInvoiceButton.setDisable(true);
                            }


                            setGraphic(hBox);
                        }
                    }
                };
            };
    }

    private EventHandler<ActionEvent> buildCreateUnitHandler() {
        return e -> {
            System.out.println("UnitListController.createUnitButton.setOnAction");
            CreateUnitModal.show((newUnit) -> {
                table.getItems().add(new UnitFX(newUnit));
                return null;
            });
        };
    }

    private Callback<TableColumn<UnitFX, Tenant>, TableCell<UnitFX, Tenant>> buildStatusCellFactory() {
        return e -> {
                return new TableCell<UnitFX, Tenant>() {

                    @Override
                    protected void updateItem(Tenant item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            return;
                        }

                        if (item == null) {
                            setText("Vacant");
                        } else {
                            setText("Occupied");
                        }
                    }
                };
            };
    }

    private Callback<TableColumn<UnitFX, Tenant>, TableCell<UnitFX, Tenant>> buildTenantCellFactory() {
        return tableColumn -> {
            return new TableCell<>(){
                @Override
                protected void updateItem(Tenant item, boolean empty) {
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
