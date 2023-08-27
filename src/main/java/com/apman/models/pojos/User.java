package com.apman.models.pojos;

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
@Table(name="USERS")
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    private String name;
    private String email;
    private String password;
    private Boolean deleted=false;

    
    @OneToMany(mappedBy = "user")
    private List<Log> logs = new ArrayList<>();

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @OneToMany(mappedBy = "createdBy")
    private List<Tenant> tenants = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy")
    private List<Unit> units = new ArrayList<>();
    
    @OneToMany(mappedBy = "issuedBy")
    private List<Invoice> issuedInvoices = new ArrayList<>();

    @ManyToOne
    private Role role;

    @OneToMany(mappedBy = "createdBy")
    private List<Role> roles = new ArrayList<>();
    
    public User() {}


    public User(
        String name,
        String email,
        String password
    ) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(
        String name,
        String email,
        String password,
        Role role
    ) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
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

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


    public List<Tenant> getTenants() {
        return tenants;
    }


    public void setTenants(List<Tenant> tenants) {
        this.tenants = tenants;
    }

    public List<Invoice> getIssuedInvoices() {
        return issuedInvoices;
    }

    public void setIssuedInvoices(List<Invoice> invoices) {
        this.issuedInvoices = invoices;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }


    @Override
    public String toString(){
        return this.id + " " + this.name;
    }

    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> log) {
        this.logs = log;
    }

    

}
