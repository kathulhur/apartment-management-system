package com.apman.models.pojos;

import java.time.LocalDate;

import com.apman.models.fx.InvoiceStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name="INVOICES")
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Double amount;
    private LocalDate date;
    private InvoiceStatus status;
    
    @ManyToOne
    private Tenant tenant;

    @ManyToOne
    private Unit unit;

    @ManyToOne
    private User issuedBy;

    public Invoice() {}


    public Invoice(
        Tenant tenant,
        Unit unit,
        LocalDate date, 
        InvoiceStatus status
    ) {
        
        this.tenant = tenant;
        this.unit = unit;
        this.amount = unit.getRate();
        this.date = date;
        this.status = status;
    }


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public Double getAmount() {
        return amount;
    }


    public void setAmount(Double amount) {
        this.amount = amount;
    }


    public LocalDate getDate() {
        return date;
    }


    public void setDate(LocalDate date) {
        this.date = date;
    }


    public InvoiceStatus getStatus() {
        return status;
    }


    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }


    public Tenant getTenant() {
        return tenant;
    }


    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }


    public Unit getUnit() {
        return unit;
    }


    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public User getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(User newIssuer) {
        issuedBy = newIssuer;
    }

    @Override
    public String toString() {
        return "Tenant - " + this.id;
    }

}




