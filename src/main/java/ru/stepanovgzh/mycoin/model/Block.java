package ru.stepanovgzh.mycoin.model;

import java.security.InvalidKeyException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sun.security.provider.DSAPublicKeyImpl;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Block
{
    private byte[] prevHash;
    @ToString.Exclude
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

    public Boolean isVerified(Signature signing) throws InvalidKeyException, SignatureException
    {
        signing.initVerify(new DSAPublicKeyImpl(this.minedBy));
        signing.update(this.toString().getBytes());
        return signing.verify(this.currHash);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof Block)) return false;
        Block block = (Block) obj;
        return Arrays.equals(getPrevHash(), block.getPrevHash());
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(getPrevHash());
    }
}
