package com.apman.models.pojos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name="UNITS")
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long  id;
    private String  name;
    private Double  rate;
    private LocalDate dateOccupied;
    private LocalDate nextInvoiceIssuance;
    private Boolean deleted = false;

    @ManyToOne
    private User createdBy;

    @OneToMany(mappedBy = "unit")
    private List<Invoice> invoices = new ArrayList<>();


    @ManyToOne
    private Tenant tenant;
    
    public Unit() {}

    public Unit(
        String name,
        Double rate
    ) {
        this.name = name;
        this.rate = rate;
        this.deleted = false;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Double getRate() {
        return this.rate;
    }


    public void setRate(Double newRate) {
        this.rate = newRate;
    }

     
    public LocalDate getDateOccupied() {
        return this.dateOccupied;
    }
    
    
    public void setDateOccupied(LocalDate date) {
        this.dateOccupied = date;
    }


    public LocalDate getNextInvoiceIssuance() {
        return this.nextInvoiceIssuance;
    }
    
    
    public void setNextInvoiceIssuance(LocalDate date) {
        this.nextInvoiceIssuance = date;
    }
    
    public Tenant getTenant() {
        return this.tenant;
    }

    public void setTenant(Tenant newTenant) {
        this.tenant = newTenant; // set the tenant
    }

    

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Unit) {
            Unit unit = (Unit) obj;
            return unit.getId() == this.getId();
        }
        return false;
    }

    

    

}
