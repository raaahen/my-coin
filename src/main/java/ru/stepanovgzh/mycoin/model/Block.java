package ru.stepanovgzh.mycoin.model;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

    public Boolean isVerified(Signature signing) throws InvalidKeyException, SignatureException,
        NoSuchAlgorithmException, InvalidKeySpecException
    {
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(this.minedBy);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        signing.initVerify(publicKey);
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
