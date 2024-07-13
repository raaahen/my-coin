package ru.stepanovgzh.mycoin.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.application.Platform;
import ru.stepanovgzh.mycoin.model.Transaction;
import ru.stepanovgzh.mycoin.servicedata.BlockchainData;
import ru.stepanovgzh.mycoin.servicedata.WalletData;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

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

    public void initialize()
    {
        Base64.Encoder encoder = Base64.getEncoder();
        from.setCellValueFactory(new PropertyValueFactory<>("fromFX"));
        to.setCellValueFactory(new PropertyValueFactory<>("toFX"));
        value.setCellValueFactory(new PropertyValueFactory<>("valueFX"));
        signature.setCellValueFactory(new PropertyValueFactory<>("signatureFX"));
        timeStamp.setCellValueFactory(new PropertyValueFactory<>("timeStampFX"));
        myCoins.setText(BlockchainData.getInstance().getWalletBallanceFX());
        publicKey.setText(encoder.encodeToString(
                WalletData.getInstance().getWallet().getPublicKey().getEncoded()));
        tableView.setItems(BlockchainData.getInstance().getTransactionLedgerFX());
        tableView.getSelectionModel().select(0);
    }

    @FXML
    public void toNewTransactionController()
    {
        Dialog<ButtonType> newTransactionController = new Dialog<>();
        newTransactionController.initOwner(borderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("src/main/fxml/view/MainWindow.fxml"));
        try
        {
            newTransactionController.getDialogPane().setContent(fxmlLoader.load());
        }
        catch (IOException e)
        {
            System.out.println("Can't load dialog");
            e.printStackTrace();
            return;
        }
        newTransactionController.getDialogPane().getButtonTypes().add(ButtonType.FINISH);
        Optional<ButtonType> result = newTransactionController.showAndWait();
        if (result.isPresent())
        {
            tableView.setItems(BlockchainData.getInstance().getTransactionLedgerFX());
            myCoins.setText(BlockchainData.getInstance().getWalletBallanceFX());
        }
    }

    @FXML
    public void refresh()
    {
        tableView.setItems(BlockchainData.getInstance().getTransactionLedgerFX());
        tableView.getSelectionModel().select(0);
        myCoins.setText(BlockchainData.getInstance().getWalletBallanceFX());
    }

    @FXML
    public void handleExit()
    {
        BlockchainData.getInstance().setExit(true);
        Platform.exit();
    }
}
