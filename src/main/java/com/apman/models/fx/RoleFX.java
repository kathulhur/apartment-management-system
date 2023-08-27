package com.apman.models.fx;

import java.util.Collection;
import java.util.List;

import com.apman.models.pojos.Role;
import com.apman.models.pojos.User;

import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RoleFX {
    private LongProperty id;
    private StringProperty name;
    private ListProperty<User> users;
    private ObjectProperty<User> createdBy;
    private Role roleModel;


    public RoleFX(Role role){
        this.roleModel = role;
        this.id = new SimpleLongProperty(this, "id", role.getId());
        this.name = new SimpleStringProperty(this, "name", role.getName());
        this.createdBy = null;
        this.users = null;
    }


    public Role getRoleModel() {
        return roleModel;
    }


    public Long getId() {
        return id.get();
    }


    public String getName() {
        return name.get();
    }


    public void setName(String newName) {
        this.roleModel.setName(newName);
        this.name.set(newName);
    }


    public StringProperty nameProperty() {
        return name;
    }

    public User getCreatedBy() {

        if (createdBy == null) {
            return this.roleModel.getCreatedBy();
        }
        else 
            return this.createdBy.get();
    }

    public void setCreatedBy(User createdBy) {
        this.roleModel.setCreatedBy(createdBy);

        if (this.createdBy != null)
            this.createdBy.set(createdBy);
    }

    public ObjectProperty<User> createdByProperty() {
        if (this.createdBy == null) {
            this.createdBy = new SimpleObjectProperty<User>(this, "createdBy", this.roleModel.getCreatedBy());
        }

        return this.createdBy;
    }

    public List<User> getUsers() {
        if (this.users == null)
            return this.roleModel.getUsers();
        else
            return this.users.get();
    }


    public void setUsers(ObservableList<User> newUsers) {
        this.roleModel.setUsers(newUsers);

        if (this.users != null)
            this.users.set(users);
    }


    public ListProperty<User> usersProperty() {
        if (this.users == null) {
            this.users = new SimpleListProperty<>(this, "users", FXCollections.observableArrayList(this.roleModel.getUsers()));
        }

        return this.users;
    }


    public static ObservableList<RoleFX> from(Collection<Role> roles) {
        ObservableList<RoleFX> roleFXs = FXCollections.observableArrayList();
        roles.forEach(role -> {
            roleFXs.add(new RoleFX(role));
        });
        return roleFXs;
    }

    public static RoleFX from(Role role) {
        return new RoleFX(role);
    }

    public void deleteRole () {
        this.roleModel.setDeleted(true);
        this.getUsers().forEach(u -> u.setRole(null));
    }

    @Override
    public String toString() {
        return this.getName();
    }

    

}
