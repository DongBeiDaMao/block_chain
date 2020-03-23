package com.damao.Transaction;

import com.damao.Utils.Encryption;

import java.security.PublicKey;

public class OutPut {
    public String id;
    public PublicKey reciepient;
    public float value;
    public String parentTransactionId;

    public OutPut(PublicKey reciepient, float value, String parentTransactionId) {
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = Encryption.getSHA256(Encryption.getStringFromKey(reciepient) + value + parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey) {
        return (publicKey == reciepient);
    }

}
