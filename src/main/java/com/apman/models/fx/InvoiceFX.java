package com.apman.models.fx;

import java.time.LocalDate;
import java.util.Collection;


import com.apman.models.pojos.Invoice;
import com.apman.models.pojos.Tenant;
import com.apman.models.pojos.Unit;
import com.apman.services.Email;
import com.apman.services.SMS;
import com.infobip.ApiException;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class InvoiceFX {
    
    private LongProperty id;
    private DoubleProperty amount;
    private ObjectProperty<LocalDate> date;
    private ObjectProperty<InvoiceStatus> status;
    private ObjectProperty<Tenant> tenant;
    private ObjectProperty<Unit> unit;
    private Invoice invoiceModel;


    public InvoiceFX(Invoice invoiceModel) {
        this.invoiceModel = invoiceModel;        
        this.id = new SimpleLongProperty(this, "id", invoiceModel.getId());
        this.date = new SimpleObjectProperty<>(this, "date", invoiceModel.getDate());
        this.amount = new SimpleDoubleProperty(this, "amount", invoiceModel.getAmount());
        this.status = new SimpleObjectProperty<>(status, "status", invoiceModel.getStatus());
        this.tenant = null;
        this.unit = null;
    }

    public Invoice getInvoiceModel() {
        return this.invoiceModel;
    }

    public Long getId() {
        return this.id.get();
    }


    public Double getAmount() {
        return this.amount.get();
    }

    public void setAmount(Double amount) {
        this.amount.set(amount);
    }


    public DoubleProperty amountProperty() {
        return this.amount;
    }


    public LocalDate getDate() {
        return date.get();
    }


    public void setDate(LocalDate date) {
        this.date.set(date);
    }


    public ObjectProperty<LocalDate> dateProperty() {
        return this.date;
    }


    public InvoiceStatus getStatus() {
        return status.get();
    }


    public void setStatus(InvoiceStatus newStatus) {
        this.invoiceModel.setStatus(newStatus);
        this.status.set(newStatus);
    }


    public ObjectProperty<InvoiceStatus> statusProperty() {
        return this.status;
    }


    public Tenant getTenant() {
        if (this.tenant == null)
            return this.invoiceModel.getTenant();

        else
            return this.tenant.get();
    }


    public void setTenant(Tenant newTenant) {
        this.invoiceModel.setTenant(newTenant);

        if (this.tenant != null)
            this.tenant.set(newTenant);
    }


    public ObjectProperty<Tenant> tenantProperty() {
        if (this.tenant == null)
            this.tenant = new SimpleObjectProperty<>(this, "tenant", this.invoiceModel.getTenant());

        return this.tenant;
    }


    public Unit getUnit() {
        if (this.unit == null)
            return this.invoiceModel.getUnit();

        else
            return this.unit.get();
    }


    public void setUnit(Unit unit) {
        this.invoiceModel.setUnit(unit);

        if (this.unit != null)
            this.unit.set(unit);
    }


    public ObjectProperty<Unit> unitProperty() {
        if (this.unit == null)
            this.unit = new SimpleObjectProperty<>(this, "unit", this.invoiceModel.getUnit());

        return this.unit;
    }


    public void sendViaSMS() throws ApiException {
        SMS.send("<MobileNumber>", "Invoice for " + getUnit().getName() + ": " + getAmount() + " due on " + getDate());
    }

    public void sendViaEmail() throws ApiException{
        Email.send("<DefaultEmail>", "INVOICE", "Invoice for" + getUnit().getName() + ": " + getAmount() + " due on " + getDate());
    }


    public static ObservableList<InvoiceFX> from(Collection<Invoice> invoices) {
        ObservableList<InvoiceFX> result = FXCollections.observableArrayList();
        for (Invoice i: invoices) {
            result.add(new InvoiceFX(i));
        }
        return result;

        
    }


}




