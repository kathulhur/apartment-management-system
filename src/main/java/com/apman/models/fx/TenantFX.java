package com.apman.models.fx;

import java.util.Collection;
import java.util.List;

import com.apman.models.pojos.Invoice;
import com.apman.models.pojos.Tenant;
import com.apman.models.pojos.Unit;

import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TenantFX {
    
    private Tenant tenantModel;
    private LongProperty id;
    private StringProperty name;
    private ListProperty<Unit> units;
    private ListProperty<Invoice> invoices;
    

    public TenantFX(Tenant tenant) {
        this.tenantModel = tenant;
        this.id = new SimpleLongProperty(this, "id", tenant.getId());
        this.name = new SimpleStringProperty(this, "name", tenant.getName());
        this.units = null;
        this.invoices = null;
    }


    public Tenant getTenantModel() {
        return tenantModel;
    }


    public Long getId() {
        return id.get();
    }


    public String getName() {
        return name.get();
    }


    public void setName(String newName) {
        this.tenantModel.setName(newName);
        this.name.set(newName);

    }


    public StringProperty nameProperty() {
        return name;
    }


    public List<Unit> getUnits() {
        if (this.units == null) 
            return this.tenantModel.getUnits();
        
        else
            return this.units.get();
    }


    public void setUnits(List<Unit> units) {
        this.tenantModel.setUnits(units);
        
        if (this.units != null)
            this.units.setAll(units);
    }

    public ListProperty<Unit> unitsProperty() {
        if (this.units == null) {
            this.units = new SimpleListProperty<Unit>(FXCollections.observableArrayList(this.tenantModel.getUnits()));
        }
        return this.units;
    }

    public void addUnit(Unit newUnit) {
        newUnit.setTenant(this.tenantModel);
        this.tenantModel.getUnits().add(newUnit);
        
        if (this.units == null) {
            this.units = new SimpleListProperty<Unit>(FXCollections.observableArrayList(this.tenantModel.getUnits()));
        }
        this.units.add(newUnit);
    }
    
    public List<Invoice> getInvoices() {
        if (this.invoices == null)
            return this.tenantModel.getInvoices();

        else
            return invoices.get();
    }

    public void setInvoices(List<Invoice> newInvoices) {
        this.tenantModel.setInvoices(newInvoices);

        if (this.invoices != null)
            this.invoices.setAll(newInvoices);
    }

    public ListProperty<Invoice> invoicesProperty() {
        if (this.invoices == null) {
            this.invoices = new SimpleListProperty<Invoice>(FXCollections.observableArrayList(this.tenantModel.getInvoices()));
        }
        
        return this.invoices;
    }

    public static ObservableList<TenantFX> from(Collection<Tenant> tenants) {
        ObservableList<TenantFX> result = FXCollections.observableArrayList();
        for (Tenant t: tenants) {
            result.add(new TenantFX(t));
        }

        return result;
    }

    public void delete() {
        this.tenantModel.setDeleted(true);
        this.getUnits().forEach(u -> u.setTenant(null));
    }
    

    @Override 
    public String toString() {
        return this.name.get();
    }


}
