package com.damao.Model;

import com.damao.Transaction.*;

import java.util.List;

public class Block{
    public BlockHeader header;
    public String data;
    public List<Transaction> transactions;
    public MerkleTree merkleTree;
    public String hash;
    public long nonce;

    public Block(BlockHeader header, String data, List<Transaction> transactions, String hash, long nonce) {
        this.header = header;
        this.data = data;
        this.transactions = transactions;
        this.merkleTree = new MerkleTree(transactions);
        this.hash = hash;
        this.nonce = nonce;
    }

    @Override
    public String toString() {
        return "com.damao.Model.Block{" +
                "header=" + header +
                ", data='" + data + '\'' +
                ", transactions=" + transactions +
                ", merkleTree=" + merkleTree +
                ", hash='" + hash + '\'' +
                ", nonce=" + nonce +
                '}';
    }
}