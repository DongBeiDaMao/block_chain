package com.damao;

import com.damao.Transaction.Input;
import com.damao.Transaction.OutPut;
import com.damao.Transaction.Transaction;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public Map<String,OutPut> UTXOs = new HashMap<String,OutPut>();
    
    public Wallet(){
        this.generateKeyPair();
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    private void generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG"); /* 伪随机数生成选用SHA1PRNG */
            ECGenParameterSpec spec = new ECGenParameterSpec("prime192v1"); /* 选用低阶曲线prime192v1 */

            generator.initialize(spec,random);
            KeyPair keyPair = generator.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    /**
     * 遍历blockChain中的UTXO集合，从中找出自己的UTXO并插入到本账户的UTXO集合
     * @return 本账户拥有的总金额
     */
    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, OutPut> item: BlockChain.UTXOs.entrySet()){
            OutPut UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) {
                this.UTXOs.put(UTXO.id,UTXO);
                total += UTXO.value ;
            }
        }
        return total;
    }

    /**
     * 创建一笔交易
     * @param recipient 接收地址
     * @param value 交易额
     * @return
     */
    public Transaction sendFunds(PublicKey recipient, float value ) {
        if(getBalance() < value) {
            System.out.println("钱不够");
            return null;
        }

        ArrayList<Input> inputs = new ArrayList<Input>(); /* 本次交易的input集合 */

        float total = 0;
        for (Map.Entry<String, OutPut> item: UTXOs.entrySet()){
            OutPut UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new Input(UTXO.id));
            if(total > value) break; /* 凑零钱，凑够了就不再查找 */
        }

        Transaction Transaction = new Transaction(publicKey, recipient , value, inputs);
        Transaction.generateSignature(privateKey);

        for(Input input: inputs){ /* 同步本账户UTXO集合 */
            UTXOs.remove(input.outPutId);
        }
        return Transaction;
    }


}
