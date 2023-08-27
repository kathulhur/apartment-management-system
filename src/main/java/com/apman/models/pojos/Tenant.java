package com.apman.models.pojos;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="TENANTS")
public class Tenant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    
    
    @OneToMany(mappedBy = "tenant")
    private List<Invoice> invoices = new ArrayList<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.MERGE)
    private List<Unit> units = new ArrayList<>();

    @ManyToOne
    private User createdBy;

    private Boolean deleted=false;

    public Tenant() {}

    public Tenant(String name) {
        this.name = name;
    }


    @Override 
    public String toString() {
        return this.name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        Tenant t = (Tenant) obj;
        return this.id == t.getId();
    }

    


}
