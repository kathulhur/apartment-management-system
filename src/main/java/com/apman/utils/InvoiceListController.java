package com.apman.utils;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.apman.App;
import com.apman.models.fx.InvoiceFX;
import com.apman.models.fx.InvoiceStatus;
import com.apman.models.fx.TenantFX;
import com.apman.models.pojos.Invoice;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class InvoiceListController implements Initializable{

    @FXML
    private Accordion accordion;

    private TenantFX tenant;

    private Stage stage;
    
    private SessionFactory sessionFactory = App.getSessionFactory();
    public InvoiceListController(Stage stage, TenantFX tenant) {
        this.stage = stage;
        this.tenant = tenant;
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<InvoiceFX> invoices = new ArrayList<>();
        try {
            Session session = sessionFactory.openSession();

            List<Invoice> storedInvoices = session.createQuery("FROM Invoice WHERE tenant = :tenant", Invoice.class)
                                            .setParameter("tenant", tenant.getTenantModel())
                                            .getResultList();
            invoices.addAll(InvoiceFX.from(storedInvoices));
            session.close();
        } catch (Exception exception) {
            System.out.println("InvoiceListController.initialize");
            System.out.println(exception);
        }
        
        ObservableList<TitledPane> titledPanes = accordion.getPanes();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        for (InvoiceFX invoice: invoices) {
            TitledPane pane = new TitledPane();
            pane.setText(invoice.getId() +  " - " + invoice.getDate().format(dateFormatter));

            
            Node invoiceDateLabel = new Label("Date: " + invoice.getDate());
            Node invoiceAmountLabel = new Label("Amount: " + invoice.getAmount());
            Node invoiceStatusLabel = new Label("Status: " + invoice.getStatus());
            Node invoiceUnitLabel = new Label("Unit: " + invoice.getUnit().getName());
            
            
            Button markAsPaidButton = new Button("Mark as Paid");
            markAsPaidButton.setOnAction(buildMarkAsPaidButtonHandler(invoice, pane));
            
            Button markAsOverdueButton = new Button("Mark as Overdue");
            markAsOverdueButton.setOnAction(buildMarkAsOverdueButton(invoice, pane));
            
            Button markAsPendingButton = new Button("Mark as Pending");
            markAsPendingButton.setOnAction(buildMarkAsPendingButton(invoice, pane));


            Button resendInvoiceButton = new Button("Resend Invoice");
            resendInvoiceButton.setOnAction(e -> {
                System.out.println("Invoice successfully sent to " + invoice.getTenant().getName());
            });
            
            
            HBox buttonList = new HBox(resendInvoiceButton, markAsPaidButton, markAsPendingButton, markAsOverdueButton);
            
            VBox infoVBox = new VBox(10);
            infoVBox.getChildren().addAll(invoiceDateLabel, invoiceAmountLabel, invoiceStatusLabel, invoiceUnitLabel, buttonList);
            
            pane.setContent(infoVBox);

            InvoiceStatus status = invoice.getStatus();

            if (status == InvoiceStatus.PAID) {

                pane.setTextFill(Color.GREEN);

            } else if (status == InvoiceStatus.OVERDUE) {
                
                pane.setTextFill(Color.RED);
            } 

            titledPanes.add(pane);
        }

    }



    private EventHandler<ActionEvent> buildMarkAsPendingButton(InvoiceFX invoice, TitledPane pane) {
        return e -> {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            invoice.setStatus(InvoiceStatus.PENDING);
            pane.setTextFill(Color.BLACK);

            session.persist(invoice.getInvoiceModel());

            session.getTransaction().commit();
            session.close();
        };
    }


    private EventHandler<ActionEvent> buildMarkAsOverdueButton(InvoiceFX invoice, TitledPane pane) {
        return e -> {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            invoice.setStatus(InvoiceStatus.OVERDUE);
            pane.setTextFill(Color.RED);
            
            session.persist(invoice.getInvoiceModel());
            session.getTransaction().commit();
            session.close();
        };
    }



    private EventHandler<ActionEvent> buildMarkAsPaidButtonHandler(InvoiceFX invoice, TitledPane pane) {
        return e -> {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            invoice.setStatus(InvoiceStatus.PAID);
            pane.setTextFill(Color.GREEN);

            session.persist(invoice.getInvoiceModel());

            session.getTransaction().commit();
            session.close();
        };
    }






    
}
