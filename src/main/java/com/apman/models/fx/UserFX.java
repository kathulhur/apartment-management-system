package com.apman.models.fx;

import java.util.Collection;

import com.apman.models.pojos.Role;
import com.apman.models.pojos.User;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserFX {
    private LongProperty id;
    private StringProperty name;
    private StringProperty email;
    private StringProperty password;
    private ObjectProperty<Role> role;
    private User userModel;


    public UserFX(User user) {
        this.userModel = user;
        this.id = new SimpleLongProperty(this, "id", user.getId());
        this.name = new SimpleStringProperty(this, "name", user.getName());
        this.email = new SimpleStringProperty(this, "email", user.getEmail());
        this.password = new SimpleStringProperty(this, "password", user.getPassword());
        this.role = null;
    }

    public User getUserModel() {
        return userModel;
    }   
    

    public Long getId() {
        return this.id.get();
    }


    public String getName() {
        return this.name.get();
    }


    public void setName(String name) {
        this.userModel.setName(name);
        this.name.set(name);
    }


    public StringProperty nameProperty() {
        return this.name;
    }


    public String getEmail() {
        return this.email.get();
    }


    public void setEmail(String email) {
        this.userModel.setEmail(email);
        this.email.set(email);;
    }


    public StringProperty emailProperty() {
        return this.email;
    }


    public String getPassword() {
        return this.password.get();
    }


    public void setPassword(String password) {
        this.password.set(password);;
    }


    public StringProperty passwordProperty() {
        return this.password;
    }

    public Role getRole() {
        if (role == null)
            return this.userModel.getRole();
        else
            return this.role.get();
    }

    public void setRole(Role newRole) {
        if (role == null)
            this.userModel.setRole(newRole);
        else
            role.set(newRole);
    }

    public ObjectProperty<Role> roleProperty() {
        if (role == null) 
            role = new SimpleObjectProperty<Role>(this, "role", this.userModel.getRole());
        return role;
    }

    public void deleteUser() {
        getUserModel().setDeleted(true);
    }


    public static UserFX from(User newUser) {
        return new UserFX(newUser);
    }

    public static ObservableList<UserFX> from(Collection<User> users) {
        ObservableList<UserFX> result = FXCollections.observableArrayList();
        for(User u: users) {
            result.add(new UserFX(u));
        }
        return result;
    }


}
