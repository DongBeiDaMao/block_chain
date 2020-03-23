package com.damao.Transaction;

import com.damao.BlockChain;
import com.damao.Utils.*;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
    public String transactionId;
    public PublicKey sender;
    public PublicKey recipient;
    public float value;
    public byte[] signature;

    public ArrayList<Input> inputs = new ArrayList<Input>();
    public ArrayList<OutPut> outputs = new ArrayList<OutPut>();

    private static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float value,  ArrayList<Input> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    /**
     * 计算该交易的哈希值
     * @return
     */
    private String calculateHash() {
        sequence++;
        return Encryption.getSHA256(
                Encryption.getStringFromKey(sender) +
                        Encryption.getStringFromKey(recipient) +
                        value + sequence
        );
    }


    /**
     * 产生签名，【sender发送给recipient价值value的币】
     * @param privateKey
     */
    public void generateSignature(PrivateKey privateKey) {
        String data = Encryption.getStringFromKey(sender) + Encryption.getStringFromKey(recipient) + value;
        signature = Encryption.applyECDSASig(privateKey,data);
    }

    /**
     * 验证交易是否由签名人发出,是否被篡改
     * @return
     */
    public boolean verifySignature() {
        String data = Encryption.getStringFromKey(sender) + Encryption.getStringFromKey(recipient) + value;
        return Encryption.verifyECDSASig(sender, data, signature);
    }

    /**
     * 执行本次交易
     * @return
     */
    public boolean processTransaction() {

        if(verifySignature() == false) {
            System.out.println("签名不对，无法交易");
            return false;
        }

        //根据outputId去UTXO集合中找对应的output
        for(Input i : inputs) {
            i.unspentOutput = BlockChain.UTXOs.get(i.outPutId);
        }

        //钱加起来是否足以支付一次最小额交易
        if(getInputsValue() < BlockChain.minimumTransaction) {
            System.out.println("穷B ，就这点钱：" + getInputsValue());
            return false;
        }

        //交易多出来的钱还要转给自己
        float leftOver = getInputsValue() - value;
        transactionId = calculateHash();
        outputs.add(new OutPut( this.recipient, value,transactionId));
        outputs.add(new OutPut( this.sender, leftOver,transactionId));

        //新产生的output放入UTXO集合里
        for(OutPut o : outputs) {
            BlockChain.UTXOs.put(o.id , o);
        }

        //将已经用于交易的output从UTXO集合中删除
        for(Input i : inputs) {
            if(i.unspentOutput == null) continue;
            BlockChain.UTXOs.remove(i.unspentOutput.id);
        }

        return true;
    }

    /**
     * 获取输入的总金额
     * @return 输入的总金额
     */
    public float getInputsValue() {
        float total = 0;
        for(Input i : inputs) {
            if(i.unspentOutput == null) continue;
            total += i.unspentOutput.value;
        }
        return total;
    }

    /**
     * 获取输出的总金额
     * @return 输出的总金额
     */
    public float getOutputsValue() {
        float total = 0;
        for(OutPut o : outputs) {
            total += o.value;
        }
        return total;
    }

}
