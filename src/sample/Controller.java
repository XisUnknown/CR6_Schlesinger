package sample;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class Controller {
    @FXML private Button deleteButton;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private TableView teachers;
    @FXML private TableView classes;
    @FXML private TextField name;
    @FXML private TextField surname;
    @FXML private TextField email;
    @FXML private TextField idText;
    ResultSet resultset = null;
    Connection con;
    Statement stmt;
    TableColumn<String, String> teachersCol = new TableColumn<String, String>("Teachers");
    TableColumn<String, String> classesCol = new TableColumn<String, String>("Classes");
    ObservableList teachersList = FXCollections.observableArrayList();
    ObservableList classesList = FXCollections.observableArrayList();
    String buff;
    String buff2;
    @FXML
    public void initialize() throws SQLException {
        classes.getColumns().add(classesCol);
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/school?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin", "root","");
        stmt = con.createStatement();
        resultset = stmt.executeQuery("SELECT name,surname FROM teacher WHERE 1");
        teachers.getColumns().add(teachersCol);
        teachersCol.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));
        while (resultset.next()) {
            buff = resultset.getString("surname")+", "+resultset.getString("name");
                System.out.println(buff);
            teachersList.add(buff);
        }
        teachers.getItems().clear();
        teachers.setItems(teachersList);
    }

    public void teacherSelect() throws SQLException {
        classes.getItems().clear();
        classes.refresh();
        String selectedItem = teachers.getSelectionModel().getSelectedItem().toString();
        String nameSplit[] = selectedItem.split(", ");
        stmt = con.createStatement();
        ResultSet rs;
        rs = stmt.executeQuery("SELECT teacherID,name,surname,email FROM teacher WHERE name LIKE ('"+nameSplit[1]+"') AND surname LIKE ('"+nameSplit[0]+"')");
        while (rs.next()) {
            name.setText(rs.getString("name"));
            surname.setText(rs.getString("surname"));
            email.setText(rs.getString("email"));
            idText.setText(rs.getString("teacherID"));
        }
        //rs = stmt.executeQuery("SELECT c.class FROM teacher t INNER JOIN teacherclass tc ON t.teacherID = tc.teacherID INNER JOIN class c ON c.classID=tc.classID WHERE name LIKE ('"+nameSplit[1]+"') AND surname LIKE ('"+nameSplit[0]+"')");
        rs = stmt.executeQuery("SELECT c.class FROM teacher t INNER JOIN teacherclass tc ON t.teacherID = tc.teacherID INNER JOIN class c ON c.classID=tc.classID WHERE t.teacherID ="+idText.getText());
        classesCol.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));
        while (rs.next()) {
            buff = rs.getString("c.class");
            System.out.println(buff);
            classesList.add(buff);
        }
        classes.setItems(classesList);
        classes.refresh();
    }
    public void addTeacher() throws SQLException {
        stmt = con.createStatement();
        stmt.execute("INSERT INTO `teacher`(`name`, `surname`, `email`) VALUES ('"+name.getText()+"', '"+surname.getText()+"', '"+email.getText()+"')");
        teachers.getItems().clear();
        resultset = stmt.executeQuery("SELECT name,surname FROM teacher WHERE 1");
        while (resultset.next()) {
            buff = resultset.getString("surname")+", "+resultset.getString("name");
            teachersList.add(buff);
        }
        teachers.setItems(teachersList);
        teachers.refresh();
    }
    public void deleteTeacher() throws SQLException {
        teachers.getItems().clear();
        stmt = con.createStatement();
        stmt.execute("DELETE FROM `teacher` WHERE teacherID = "+idText.getText());
        resultset = stmt.executeQuery("SELECT name,surname FROM teacher WHERE 1");
        while (resultset.next()) {
            buff = resultset.getString("surname")+", "+resultset.getString("name");
            teachersList.add(buff);
        }
        teachers.setItems(teachersList);
        teachers.refresh();
    }
    public void updateTeacher() throws SQLException {
        teachers.getItems().clear();
        stmt = con.createStatement();
        stmt.execute("UPDATE `teacher` SET `name`='"+name.getText()+"',`surname`='"+surname.getText()+"',`email`='"+email.getText()+"' WHERE teacherID = "+idText.getText());
        resultset = stmt.executeQuery("SELECT name,surname FROM teacher WHERE 1");
        while (resultset.next()) {
            buff = resultset.getString("surname")+", "+resultset.getString("name");
            teachersList.add(buff);
        }
        teachers.setItems(teachersList);
        teachers.refresh();
    }
}
