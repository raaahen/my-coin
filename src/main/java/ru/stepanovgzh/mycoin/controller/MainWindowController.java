package ru.stepanovgzh.mycoin.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import ru.stepanovgzh.mycoin.model.Transaction;

public class MainWindowController 
{
    //this is read-only UI table
    @FXML
    public TableView<Transaction> tableView = new TableView<>();
    @FXML
    private TableColumn<Transaction, String> from;
    @FXML
    private TableColumn<Transaction, String> to;
    @FXML
    private TableColumn<Transaction, Integer> value;
    @FXML
    private TableColumn<Transaction, String> timeStamp;
    @FXML
    private TableColumn<Transaction, String> signature;
    @FXML
    private BorderPane borderPane;
    @FXML
    private TextField myCoins;
    @FXML
    private TextArea publicKey;
    
}
