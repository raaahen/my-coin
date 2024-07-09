package ru.stepanovgzh.mycoin;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.stepanovgzh.mycoin.model.Block;
import ru.stepanovgzh.mycoin.model.Transaction;
import ru.stepanovgzh.mycoin.model.Wallet;
import ru.stepanovgzh.mycoin.servicedata.WalletData;
import ru.stepanovgzh.mycoin.servicedata.BlockchainData;
import ru.stepanovgzh.mycoin.threads.MiningThread;
import ru.stepanovgzh.mycoin.threads.PeerClient;
import ru.stepanovgzh.mycoin.threads.PeerServer;
import ru.stepanovgzh.mycoin.threads.UI;

public class MyCoin extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        new UI().start(primaryStage);
        new PeerClient().start();
        new PeerServer(6000).start();
        new MiningThread().start();
    }

    @Override
    public void init()
    {
        try
        {
            //This creates your wallet if there is none and gives you a KeyPair.
            //We will create it in separate db for better security and ease of portability
            Connection walletConnection = DriverManager.getConnection(
                "jdbc:sqlite:/Users/sgzh1/projects/my-coin/src/main/sql/wallet.db");
            Statement walletStatement = walletConnection.createStatement();
            walletStatement.executeUpdate("CREATE TABLE IF NOT EXISTS WALLET ( "
                + " PRIVATE_KEY BLOB NOT NULL UNIQUE, " 
                + " PUBLIC_KEY BLOB NOT NULL UNIQUE, "
                + " PRIMARY KEY (PRIVATE_KEY, PUBLIC_KEY) ) ");
            ResultSet resultSet = walletStatement.executeQuery(" SELECT * FROM WALLET ");
            if (!resultSet.next())
            {
                Wallet newWallet = new Wallet();
                byte[] pubBlob = newWallet.getPublicKey().getEncoded();
                byte[] prvBlob = newWallet.getPrivateKey().getEncoded();
                PreparedStatement pstmt = walletConnection.prepareStatement(
                    " INSERT INTO WALLET(PRIVATE_KEY, PUBLIC_KEY) VALUES (?, ?) ");
                pstmt.setBytes(1, prvBlob);
                pstmt.setBytes(2, pubBlob);
                pstmt.executeUpdate();
            }
            resultSet.close();
            walletStatement.close();
            walletConnection.close();
            WalletData.getInstance().loadWallet();
            //This will create the db tables with columns for the Blockchain
            Connection blockchainConnection = DriverManager.getConnection(
                "jdbc:sqlite:/Users/sgzh1/projects/my-coin/src/main/sql/blockchain.db");
            Statement blockchainStmt = blockchainConnection.createStatement();
            blockchainStmt.executeUpdate(" CREATE TABLE IF NOT EXISTS BLOCKCHAIN ( "
                + " ID INTEGER NOT NULL UNIQUE, "
                + " PREVIOUS_HASH BLOB UNIQUE, "
                + " CURRENT_HASH BLOB UNIQUE, "
                + " LEDGER_ID INTEGER NOT NULL UNIQUE, "
                + " CREATED_ON TEXT, "
                + " CREATED_BY BLOB, "
                + " MINING_POINTS TEXT, "
                + " LUCK NUMERIC, "
                + " PRIMARY KEY(ID AUTOINCREMENT) ) ");
            ResultSet resultSetBlockchain = blockchainStmt.executeQuery(
                " SELECT * FROM BLOCKCHAIN ");
            Transaction initBlockRewardTransaction = null;
            if (!resultSetBlockchain.next())
            {
                Block firstBlock = new Block();
                firstBlock.setMinedBy(
                    WalletData.getInstance().getWallet().getPublicKey().getEncoded());
                firstBlock.setTimeStamp(LocalDateTime.now().toString());
                //helper class
                Signature signing = Signature.getInstance("SHA256withDSA");
                signing.update(firstBlock.toString().getBytes());
                signing.initSign(WalletData.getInstance().getWallet().getPrivateKey());
                signing.update(firstBlock.toString().getBytes());
                firstBlock.setCurrHash(signing.sign());
                PreparedStatement pstmt = blockchainConnection.prepareStatement(
                    " INSERT INTO BLOCKCHAIN ( PREVIOUS_HASH, CURRENT_HASH, LEDGER_ID, "
                    + " CREATED_ON, CREATED_BY, MINING_POINTS, LUCK ) " 
                    + " VALUES (?, ?, ?, ?, ?, ?, ?) ");
                pstmt.setBytes(1, firstBlock.getPrevHash());
                pstmt.setBytes(2, firstBlock.getCurrHash());
                pstmt.setInt(3, firstBlock.getLedgerId());
                pstmt.setString(4, firstBlock.getTimeStamp());
                pstmt.setBytes(
                    5, WalletData.getInstance().getWallet().getPublicKey().getEncoded());
                pstmt.setInt(6, firstBlock.getMiningPoints());
                pstmt.setDouble(7, firstBlock.getLuck());
                pstmt.executeUpdate();
                Signature transSignature = Signature.getInstance("SHA256withDSA");
                initBlockRewardTransaction = new Transaction(WalletData.getInstance().getWallet(),
                    WalletData.getInstance().getWallet().getPublicKey().getEncoded(), 
                    100, 1, transSignature);
            }
            resultSetBlockchain.close();
            blockchainStmt.executeUpdate("CREATE TABLE IF NOT EXISTS TRANSACTIONS ( "
            + " ID INTEGER NOT NULL UNIQUE, "
            + " \"FROM\" BLOB, "
            + " \"TO\" BLOB, "
            + " LEDGER_ID INTEGER, "
            + " VALUE INTEGER, "
            + " SIGNATURE BLOB UNIQUE, "
            + " CREATED_ON TEXT, "
            + " PRIMARY KEY(ID AUTOINCREMENT) ) ");
            if (initBlockRewardTransaction != null)
            {
                BlockchainData.getInstance().addTransaction(initBlockRewardTransaction, true);
                BlockchainData.getInstance().addTransactionState(initBlockRewardTransaction);
            }
            blockchainStmt.close();
            blockchainConnection.close();
        }
        catch (SQLException | NoSuchAlgorithmException 
            | InvalidKeyException | SignatureException e)
        {
            System.out.println("db failed: " + e.getMessage());
        }
        catch (GeneralSecurityException e)
        {
            e.printStackTrace();
        }
        BlockchainData.getInstance().loadBlockChain();
    }
}
