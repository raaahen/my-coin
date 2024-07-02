package ru.stepanovgzh.mycoin.model;

import java.util.ArrayList;
import java.util.LinkedList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sun.security.provider.DSAPublicKeyImpl;

@Getter
@Setter
@AllArgsConstructor
public class Block
{
    private byte[] prevHash;
    private byte[] currHash;
    private String timeStamp;
    private byte[] minedBy;
    private Integer ledgerId = 1;
    private Integer miningPoints = 0;
    private Double luck = 0.0;

    private ArrayList<Transaction> transactionLedger = new ArrayList<>();

    //This constructor is used when we initiate it after retrieve.
    public Block(LinkedList<Block> currentBlockChain)
    {
        Block lastBlock = currentBlockChain.getLast();
        prevHash = lastBlock.getCurrHash();
        ledgerId = lastBlock.getLedgerId() + 1;
        luck = Math.random() * 10000000;
    }

    //This constructor is used only for creating the first block in the blockchain.
    public Block()
    {
        prevHash = new byte[]{0};
    }
}
