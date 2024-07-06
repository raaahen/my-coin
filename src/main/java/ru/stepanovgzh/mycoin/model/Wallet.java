package ru.stepanovgzh.mycoin.model;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import lombok.Getter;

@Getter
public class Wallet 
{
    private KeyPair keyPair;

    /* Constructor for generating new keyPair
    * This no parameters constructor will call the second constructor
    * with a default keySize and a KeyPairGenerator instance set to generate
    * keys using the DSA algorithm. The second constructor receives these input
    * parameters either from the first or from other parts of the application
    * and simply sets the size of the keys on line 15 and generates the keys
    * themselves on line 16.
    */
    public Wallet() throws NoSuchAlgorithmException
    {
        this(2048, KeyPairGenerator.getInstance("DSA"));
    }

    public Wallet(Integer keySize, KeyPairGenerator keyPairGen)
    {
        keyPairGen.initialize(keySize);
        this.keyPair = keyPairGen.generateKeyPair();
    }

    public Wallet(PublicKey publicKey, PrivateKey privateKey)
    {
        this.keyPair = new KeyPair(publicKey, privateKey);
    }

    public PublicKey getPublicKey()
    {
        return keyPair.getPublic();
    }

    public PrivateKey getPrivateKey()
    {
        return keyPair.getPrivate();
    }
}
