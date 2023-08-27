package com.apman.models.fx;

import java.time.LocalDate;
import java.util.Collection;

import com.apman.App;
import com.apman.models.pojos.Invoice;
import com.apman.models.pojos.Tenant;
import com.apman.models.pojos.Unit;
import com.apman.utils.Dialogs.InfoDialog;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class UnitFX {
    
    private LongProperty id;
    private StringProperty name;
    private DoubleProperty rate;
    private ObjectProperty<LocalDate> dateOccupied;
    private ObjectProperty<LocalDate> nextInvoiceIssuance;
    private ObjectProperty<Tenant> tenant;
    private Unit unitModel;

    public static ObservableList<UnitFX> from(Collection<Unit> units) {
        ObservableList<UnitFX> result = FXCollections.observableArrayList();
        for(Unit u: units) {
            result.add(new UnitFX(u));
        }
        return result;
    }

    public UnitFX(Unit unit) {
        this.unitModel = unit;
        this.id = new SimpleLongProperty(this, "id", unit.getId());
        this.name = new SimpleStringProperty(this, "name", unit.getName());
        this.rate = new SimpleDoubleProperty(this, "rate", unit.getRate());
        this.nextInvoiceIssuance = new SimpleObjectProperty<>(this, "lastInvoiceIssuedAt", unit.getNextInvoiceIssuance());
        this.dateOccupied = new SimpleObjectProperty<>(this, "dateOccupied", unit.getDateOccupied());
        this.tenant = null;
    }



    public Long getId() {
        return this.id.get();
    }


    public String getName() {
        return this.name.get();
    }
    
    
    public void setName(String newName) {
        this.name.set(newName);
        this.unitModel.setName(newName);
    }
    

    public StringProperty nameProperty() {
        return this.name;
    }
    

    public Unit getUnitModel() {
        return unitModel;
    }


    public Tenant getTenant() {
        if (tenant == null)
            return this.unitModel.getTenant();
        else
            return this.tenant.get();
    }


    public void setTenant(Tenant newTenant) {
        
        this.unitModel.setTenant(newTenant);
        
        if (this.tenant != null)
            this.tenant.set(newTenant); 
    }

    public ObjectProperty<Tenant> tenantProperty() {
        if (this.tenant == null)
            this.tenant = new SimpleObjectProperty<>(this, "tenant", this.unitModel.getTenant());

        return this.tenant;
    }
    
    public void assignTenant(Tenant newTenant) {
        // set the tenant
        setTenant(newTenant);
        setNextInvoiceIssuance(LocalDate.now().plusMonths(1));
    }




    public LocalDate getDateOccupied() {
        return this.dateOccupied.get();
    }


    public void setDateOccupied(LocalDate date) {
        this.dateOccupied.set(date);
    }


    public ObjectProperty<LocalDate> dateOccupiedProperty() {
        return this.dateOccupied;
    }


    public Invoice issueInvoice() {
        setNextInvoiceIssuance(getNextInvoiceIssuance().plusMonths(1));
        Invoice newInvoice = new Invoice(getTenant(), this.unitModel, LocalDate.now(), InvoiceStatus.PENDING);
        newInvoice.setIssuedBy(App.getCurrentUser());

        
        
        
        return newInvoice;
    }


    public Double getRate() {
        return this.rate.get();
    }


    public void setRate(Double newRate) {
        this.rate.set(newRate);
    }

    
    public DoubleProperty rateProperty() {
        return this.rate;
    }


    public LocalDate getNextInvoiceIssuance() {
        return this.nextInvoiceIssuance.get();
    }


    public void setNextInvoiceIssuance(LocalDate date) {
        this.unitModel.setNextInvoiceIssuance(date);
        this.nextInvoiceIssuance.set(date);
    }


    public ObjectProperty<LocalDate> nextInvoiceIssuanceProperty() {
        return this.nextInvoiceIssuance;
    }

    public LocalDate nextInvoiceIssuance() {
        return getNextInvoiceIssuance().plusDays(30);
    }

    
    public void removeTenant() {    
        setTenant(null);
    }


    public void deleteUnit() {
        unitModel.setDeleted(true);
        if (getTenant() != null) {
            removeTenant();
        }
    }
    

    public void updateUnitModel() {
        unitModel.setName(getName());
        
    }
    
    
    public String toString() {
        return this.name.get();
    }
    

}
