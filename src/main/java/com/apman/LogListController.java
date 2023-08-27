package com.apman;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

import org.controlsfx.glyphfont.FontAwesome;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import com.apman.models.pojos.Log;
import com.apman.models.pojos.User;
import com.apman.models.pojos.Log.LogType;
import com.apman.utils.Formatter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.*;
import javafx.util.Callback;


public class LogListController implements Initializable {
    
    private SessionFactory sessionFactory = App.getSessionFactory();
    
    private final int rowsPerPage = 25;
    private Long totalLogs = 0L;
    private TableView<Log> table = new TableView<>();
    private TableColumn<Log, Long> idColumn = new TableColumn<>("ID");
    private TableColumn<Log, LogType> typeColumn = new TableColumn<>("Type");
    private TableColumn<Log, String> descriptionColumn = new TableColumn<>("Description");
    private TableColumn<Log, LocalDateTime> createdAtColumn = new TableColumn<>("Date and Time");
    private TableColumn<Log, User> userColumn = new TableColumn<>("User involved");
    {

        idColumn.setMinWidth(100);
        idColumn.setCellValueFactory(new PropertyValueFactory<Log, Long>("id"));
        
        typeColumn.setMinWidth(150);
        typeColumn.setCellValueFactory(new PropertyValueFactory<Log, LogType>("type"));
        
        descriptionColumn.setMinWidth(300);
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<Log, String>("description"));
        
        createdAtColumn.setMinWidth(200);
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<Log, LocalDateTime>("createdAt"));
        createdAtColumn.setCellFactory(buildCreatedAtColumnCellFactory());
        
        
        userColumn.setMinWidth(200);
        userColumn.setCellValueFactory(new PropertyValueFactory<Log, User>("user"));
        table.getColumns().add(idColumn);
        table.getColumns().add(typeColumn);
        table.getColumns().add(descriptionColumn);
        table.getColumns().add(createdAtColumn);
        table.getColumns().add(userColumn);
    }
    
    @FXML
    private Pagination pagination;
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            totalLogs = getTotalRecords();
            pagination.setPageCount((int) Math.ceil((double) totalLogs / rowsPerPage));
    
            pagination.setPageFactory(pageIndex -> {
                int fromIndex = pageIndex * rowsPerPage;
                int toIndex = (int) Math.min(fromIndex + rowsPerPage, totalLogs);
                
                
                table.setItems(FXCollections.observableArrayList(loadLogs(fromIndex, toIndex)));
                return table;
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Callback<TableColumn<Log, LocalDateTime>, TableCell<Log, LocalDateTime>> buildCreatedAtColumnCellFactory() {
        return e -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(Formatter.formatDateTime(item));
                    }
                }
            };
        };
    }

    private List<Log> loadLogs(int fromIndex, int toIndex) {
        Session session = sessionFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Log> cq = cb.createQuery(Log.class);
        Root<Log> root = cq.from(Log.class);
        cq.orderBy(cb.desc(root.get("createdAt")));

        Query<Log> query = session.createQuery(cq);
        query.setFirstResult(fromIndex);
        query.setMaxResults(toIndex);

        List<Log> logs = query.getResultList();
        
        session.close();

        return logs;
        

    }



    private Long getTotalRecords() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Log> root = cq.from(Log.class);
            cq.select(cb.count(root));

            Query<Long> query = session.createQuery(cq);
            
            Long totalRecords = query.getSingleResult();

            return totalRecords;
        } catch(Exception e) {
            e.printStackTrace();
            return 0L;
        } finally {
            if(session != null) {
                session.close();
            }
        }

    }





}
