package ru.stepanovgzh.mycoin.servicedata;

import ru.stepanovgzh.mycoin.model.Wallet;

public class WalletData {

    private Wallet wallet;
    //singleton class
    private static WalletData instance;

    static {
        instance = new WalletData();
    }

    public static WalletData getInstance() {
        return instance;
    }
    
}
