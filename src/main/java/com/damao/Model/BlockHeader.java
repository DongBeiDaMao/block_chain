package com.damao.Model;

public class BlockHeader {
    public int index;
    public String preBlockHash;
    public String merkleRoot;
    public long timeStamp;
    public int difficulty;

    public BlockHeader(int index, String preBlockHash, String merkleRoot, long timeStamp, int difficulty) {
        this.index = index;
        this.preBlockHash = preBlockHash;
        this.merkleRoot = merkleRoot;
        this.timeStamp = timeStamp;
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return "com.damao.Model.BlockHeader{" +
                "index=" + index +
                ", preBlockHash='" + preBlockHash + '\'' +
                ", merkleRoot='" + merkleRoot + '\'' +
                ", timeStamp=" + timeStamp +
                ", difficulty=" + difficulty +
                '}';
    }
}
