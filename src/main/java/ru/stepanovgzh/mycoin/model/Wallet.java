package ru.stepanovgzh.mycoin.model;

import java.security.PrivateKey;
import java.security.PublicKey;

import lombok.Getter;

@Getter
public class Wallet 
{
    PublicKey publicKey;
    PrivateKey privateKey;
}
