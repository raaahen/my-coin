package ru.stepanovgzh.mycoin.threads;

import lombok.AllArgsConstructor;
import ru.stepanovgzh.mycoin.model.Block;
import ru.stepanovgzh.mycoin.servicedata.BlockchainData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

@AllArgsConstructor
public class PeerRequestThread extends Thread
{
    private Socket socket;

    @Override
    public void run()
    {
        try
        {
            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
            LinkedList<Block> receivedBC = (LinkedList<Block>) objectInput.readObject();
            System.out.println("LedgerId = " + receivedBC.getLast().getLedgerId()
                + " Size = " + receivedBC.getLast().getTransactionLedger().size());
            objectOutput.writeObject(BlockchainData.getInstance().getBlockchainConsensus(receivedBC));
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
