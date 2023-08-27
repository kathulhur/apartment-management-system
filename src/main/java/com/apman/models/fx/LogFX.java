package com.apman.models.fx;

import java.time.LocalDateTime;
import java.util.Collection;

import com.apman.models.pojos.Log;
import com.apman.models.pojos.Role;
import com.apman.models.pojos.Tenant;
import com.apman.models.pojos.Unit;
import com.apman.models.pojos.User;
import com.apman.models.pojos.Log.LogType;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LogFX {
    
    private LongProperty id;
    private ObjectProperty<LogType> type;
    private StringProperty description;
    private ObjectProperty<LocalDateTime> createdAt;
    private ObjectProperty<User> user;
    private Log logModel;

    public LogFX() {}

    public LogFX(Log log) {
        this.logModel = log;
        this.id = new SimpleLongProperty(log.getId());
        this.type = new SimpleObjectProperty<>(log.getType());
        this.description = new SimpleStringProperty(log.getDescription());
        this.createdAt = new SimpleObjectProperty<>(log.getCreatedAt());
        this.user = null;
    }


    public Long getId() {
        return id.get();
    }

    public void setId(Long id) {
        this.id.set(id);
    }


    public LogType getType() {
        return type.get();
    }

    public void setType(LogType type) {
        this.type.set(type);
    }


    public String getDescription() {
        return description.get();
    }


    public void setDescription(String description) {
        this.description.set(description);
    }


    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }


    public User getUser() {
        if (this.user == null)
            return this.logModel.getUser();
        else
            return user.get();
    }


    public void setUser(User newUser) {
        this.logModel.setUser(newUser);

        if (this.user == null)
            this.user.set(newUser);
    }



    public static ObservableList<LogFX> from(Collection<Log> logs)  {
        ObservableList<LogFX> logsFX = FXCollections.observableArrayList();
        for (Log log : logs) {
            logsFX.add(new LogFX(log));
        }
        return logsFX;
    }

    public static Log createLoginLog(User user) {
        Log log = new Log();
        log.setType(LogType.LOGIN);
        log.setDescription( user.getName() + " logged in");
        log.setUser(user);
        return log;
    }

    public static Log createLogoutLog(User user) {
        Log log = new Log();
        log.setType(LogType.LOGOUT);
        log.setDescription( user.getName() + " logged out");
        log.setUser(user);
        return log;
    }

    public static Log createLogUserCreate(User user) {
        Log log = new Log();
        log.setType(LogType.USER_CREATE);
        log.setDescription( user.getName() + " created");
        log.setUser(user);
        return log;
    }

    public static Log createLogUserUpdate(User user) {
        Log log = new Log();
        log.setType(LogType.USER_UPDATE);
        log.setDescription( user.getName() + " updated");
        log.setUser(user);
        return log;
    }

    public static Log createLogUserDelete(User user) {
        Log log = new Log();
        log.setType(LogType.USER_DELETE);
        log.setDescription( user.getName() + " deleted");
        log.setUser(user);
        return log;
    }

    public static Log createLogUnitCreate(User user, Unit unit) {
        Log log = new Log();
        log.setType(LogType.UNIT_CREATE);
        log.setDescription( user.getName() + " created unit " + unit.getName() + " with id " + unit.getId());
        log.setUser(user);
        return log;
    }

    public static Log createLogUnitUpdate(User user, Unit unit) {
        Log log = new Log();
        log.setType(LogType.UNIT_UPDATE);
        log.setDescription( user.getName() + " updated unit " + unit.getName() + " with id " + unit.getId());
        log.setUser(user);
        return log;
    }

    
    public static Log createLogUnitDelete(User user, Unit unit) {
        Log log = new Log();
        log.setType(LogType.UNIT_DELETE);
        log.setDescription( user.getName() + " deleted unit " + unit.getName() + " with id " + unit.getId());
        log.setUser(user);
        return log;
    }


    
    public static Log createLogTenantCreate(User user, Tenant tenant) {
        Log log = new Log();
        log.setType(LogType.TENANT_CREATE);
        log.setDescription( user.getName() + " created tenant " + tenant.getName() + " with id " + tenant.getId());
        log.setUser(user);
        return log;
    }


    public static Log createLogTenantUpdate(User user, Tenant tenant) {
        Log log = new Log();
        log.setType(LogType.TENANT_UPDATE);
        log.setDescription( user.getName() + " updated tenant " + tenant.getName() + " with id " + tenant.getId());
        log.setUser(user);
        return log;
    }

    public static Log createLogTenantDelete(User user, Tenant tenant) {
        Log log = new Log();
        log.setType(LogType.TENANT_DELETE);
        log.setDescription( user.getName() + " deleted tenant " + tenant.getName() + " with id " + tenant.getId());
        log.setUser(user);
        return log;
    }

    public static Log createLogRoleCreate(User user, Role createdRole) {
        Log log = new Log();
        log.setType(LogType.ROLE_CREATE);
        log.setDescription( user.getName() + " created role " + createdRole.getName() + " with id " + createdRole.getId());
        log.setUser(user);
        return log;
    }

    public static Log createLogRoleUpdate(User user, Role updatedRole) {
        Log log = new Log();
        log.setType(LogType.ROLE_UPDATE);
        log.setDescription( user.getName() + " updated role " + updatedRole.getName() + " with id " + updatedRole.getId());
        log.setUser(user);
        return log;
    }


    public static Log createLogRoleDelete(User user, Role deletedRole) {
        Log log = new Log();
        log.setType(LogType.ROLE_DELETE);
        log.setDescription( user.getName() + " deleted role " + deletedRole.getName() + " with id " + deletedRole.getId());
        log.setUser(user);
        return log;
    }


    public static Log createLogUserCreate(User user, User createdUser) {
        Log log = new Log();
        log.setType(LogType.USER_CREATE);
        log.setDescription( user.getName() + " created user " + createdUser.getName() + " with id " + createdUser.getId());
        log.setUser(user);
        return log;
    }

    public static Log createLogUserUpdate(User user, User updatedUser) {
        Log log = new Log();
        log.setType(LogType.USER_UPDATE);
        log.setDescription( user.getName() + " updated user " + updatedUser.getName() + " with id " + updatedUser.getId());
        log.setUser(user);
        return log;
    }

    public static Log createLogUserDelete(User user, User deletedUser) {
        Log log = new Log();
        log.setType(LogType.USER_DELETE);
        log.setDescription( user.getName() + " deleted user " + deletedUser.getName() + " with id " + deletedUser.getId());
        log.setUser(user);
        return log;
    }










    
}
