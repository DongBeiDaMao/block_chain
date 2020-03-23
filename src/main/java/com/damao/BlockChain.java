package com.damao;

import com.damao.Model.Block;
import com.damao.Model.BlockHeader;
import com.damao.Transaction.MerkleTree;
import com.damao.Transaction.OutPut;
import com.damao.Transaction.Transaction;
import com.damao.Utils.Encryption;
import com.damao.Utils.POW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockChain {
    public static Wallet coinBase;
    public static Map<String,OutPut> UTXOs = new HashMap<String, OutPut>(); /* 全部结点的UTXO集合 */
    public static float minimumTransaction = 0.1f; /* 规定一次交易的最小交易额 */

    public static Wallet A;
    public static Wallet B;

    public static List<Transaction> transactionsForAdd = new ArrayList<>(); /* 用于记录未被打包进区块中的交易 */

    static{
         A = new Wallet();
         B = new Wallet();
         coinBase = new Wallet();
         OutPut outPut = new OutPut(coinBase.getPublicKey(),Integer.MAX_VALUE,"-1");
         UTXOs.put("-1",outPut);
    }

    public static List<Block> blockChain = new ArrayList<>();

    public static void main(String[] args) {
        long nonce;
        Block block;
        MerkleTree tree;
        BlockHeader header;
        POW pow = new POW();

        //创世块加入区块链
        BlockHeader GenesisHeader = new BlockHeader(0,"-1","-1",System.currentTimeMillis(),5);
        Block GenesisBlock = new Block(GenesisHeader, "创世块", null,Encryption.getSHA256(GenesisHeader.toString()),0);
        blockChain.add(GenesisBlock);

        tree = new MerkleTree(transactionsForAdd); /* 当前待打包交易打包 */
        header = new BlockHeader(getCurrentIndex(),getPreHash(),tree.getMerkleRoot(),System.currentTimeMillis(),3);
        nonce = pow.calculateNonce(header, A.getPublicKey());
        block = new Block(header,"挖到矿，有钱了！",transactionsForAdd,Encryption.getSHA256(header.toString()),nonce);
        blockChain.add(block); /* 新区块加入区块链 */
        transactionsForAdd.clear(); /* 待打包交易已被打包，清空当前集合 */
        System.out.println("A:" + A.getBalance() + " B:" + B.getBalance());

        /* 产生五个A转给B2个币的交易 */
        for(int i = 0; i < 5; i ++){
            transactionsForAdd.add(A.sendFunds(B.getPublicKey(),1));
        }

        tree = new MerkleTree(transactionsForAdd);
        header = new BlockHeader(getCurrentIndex(),getPreHash(),tree.getMerkleRoot(),System.currentTimeMillis(),3);
        nonce = pow.calculateNonce(header,B.getPublicKey());
        block = new Block(header,"挖到矿，有钱了！",transactionsForAdd,Encryption.getSHA256(header.toString()),nonce);
        blockChain.add(block);
        transactionsForAdd.clear();
        System.out.println("A:" + A.getBalance() + " B:" + B.getBalance());
    }

    public static int getCurrentIndex(){
        return blockChain.size();
    }

    public static String getPreHash(){
        return blockChain.get(getCurrentIndex() - 1).hash;
    }
}
