package com.apman.models.pojos;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="LOGS")
public class Log {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private LogType type;

    private String description;

    private LocalDateTime createdAt=LocalDateTime.now();
    
    @ManyToOne
    private User user;



    public Log() {}

    public Log(User user, LogType type, String description) {
        this.type = type;
        this.description = description;
        this.user = user;
        
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static class Factory {

        public static Log loginLog(User user) {
            Log log = new Log();
            log.setType(LogType.LOGIN);
            log.setDescription( user.getName() + " logged in");
            log.setUser(user);
            return log;
        }
    
        public static Log logoutLog(User user) {
            Log log = new Log();
            log.setType(LogType.LOGOUT);
            log.setDescription( user.getName() + " logged out");
            log.setUser(user);
            return log;
        }
    
        public static Log unitCreateLog(User user, Unit unit) {
            Log log = new Log();
            log.setType(LogType.UNIT_CREATE);
            log.setDescription( user.getName() + " created unit " + unit.getName() + " with id " + unit.getId());
            log.setUser(user);
            return log;
        }
    
        public static Log unitUpdateLog(User user, Unit unit) {
            Log log = new Log();
            log.setType(LogType.UNIT_UPDATE);
            log.setDescription( user.getName() + " updated unit " + unit.getName() + " with id " + unit.getId());
            log.setUser(user);
            return log;
        }
    
        
        public static Log unitDeleteLog(User user, Unit unit) {
            Log log = new Log();
            log.setType(LogType.UNIT_DELETE);
            log.setDescription( user.getName() + " deleted unit " + unit.getName() + " with id " + unit.getId());
            log.setUser(user);
            return log;
        }
    
    
        
        public static Log tenantCreateLog(User user, Tenant tenant) {
            Log log = new Log();
            log.setType(LogType.TENANT_CREATE);
            log.setDescription( user.getName() + " created tenant " + tenant.getName() + " with id " + tenant.getId());
            log.setUser(user);
            return log;
        }
    
    
        public static Log tenantUpdateLog(User user, Tenant tenant) {
            Log log = new Log();
            log.setType(LogType.TENANT_UPDATE);
            log.setDescription( user.getName() + " updated tenant " + tenant.getName() + " with id " + tenant.getId());
            log.setUser(user);
            return log;
        }
    
        public static Log tenantDeleteLog(User user, Tenant tenant) {
            Log log = new Log();
            log.setType(LogType.TENANT_DELETE);
            log.setDescription( user.getName() + " deleted tenant " + tenant.getName() + " with id " + tenant.getId());
            log.setUser(user);
            return log;
        }
    
        public static Log roleCreateLog(User user, Role createdRole) {
            Log log = new Log();
            log.setType(LogType.ROLE_CREATE);
            log.setDescription( user.getName() + " created role " + createdRole.getName() + " with id " + createdRole.getId());
            log.setUser(user);
            return log;
        }
    
        public static Log roleUpdateLog(User user, Role updatedRole) {
            Log log = new Log();
            log.setType(LogType.ROLE_UPDATE);
            log.setDescription( user.getName() + " updated role " + updatedRole.getName() + " with id " + updatedRole.getId());
            log.setUser(user);
            return log;
        }
    
    
        public static Log roleDeleteLog(User user, Role deletedRole) {
            Log log = new Log();
            log.setType(LogType.ROLE_DELETE);
            log.setDescription( user.getName() + " deleted role " + deletedRole.getName() + " with id " + deletedRole.getId());
            log.setUser(user);
            return log;
        }
    
    
        public static Log userCreateLog(User user, User createdUser) {
            Log log = new Log();
            log.setType(LogType.USER_CREATE);
            log.setDescription( user.getName() + " created user " + createdUser.getName() + " with id " + createdUser.getId());
            log.setUser(user);
            return log;
        }
    
        public static Log userUpdateLog(User user, User updatedUser) {
            Log log = new Log();
            log.setType(LogType.USER_UPDATE);
            log.setDescription( user.getName() + " updated user " + updatedUser.getName() + " with id " + updatedUser.getId());
            log.setUser(user);
            return log;
        }
    
        public static Log userDeleteLog(User user, User deletedUser) {
            Log log = new Log();
            log.setType(LogType.USER_DELETE);
            log.setDescription( user.getName() + " deleted user " + deletedUser.getName() + " with id " + deletedUser.getId());
            log.setUser(user);
            return log;
        }
    }



    public enum LogType {
        LOGIN,
        LOGOUT,
        USER_CREATE,
        USER_UPDATE,
        USER_DELETE,
        TENANT_CREATE,
        TENANT_UPDATE,
        TENANT_DELETE,
        UNIT_CREATE,
        UNIT_UPDATE,
        UNIT_DELETE,
        INVOICE_CREATE,
        INVOICE_UPDATE,
        INVOICE_DELETE,
        ROLE_CREATE,
        ROLE_UPDATE,
        ROLE_DELETE,
    }


}
