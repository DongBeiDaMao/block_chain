package com.damao.Utils;

import com.damao.BlockChain;
import com.damao.Model.BlockHeader;
import com.damao.Transaction.OutPut;
import com.damao.Transaction.Transaction;

import java.security.PublicKey;

public class POW {

    /* difficulty = 1 时的target值 */
    public static final int difficulty_1_target = 10;

    /**
     * 组装一个BlockHeader，并返回他的字符串
     * @param index headerParam
     * @param preBlockHash headerParam
     * @param merkleRoot headerParam
     * @param timeStamp headerParam
     * @param difficulty headerParam
     * @return blockHeader.toString()
     */
    public static BlockHeader getHeader(int index, String preBlockHash, String merkleRoot, long timeStamp, int difficulty){
        BlockHeader header = new BlockHeader(index, preBlockHash, merkleRoot, timeStamp, difficulty);
        return header;
    }

    /**
     * 根据给定blockHeader计算nonce值
     * @param blockHeader
     * @return nonce值
     */
    public long calculateNonce(BlockHeader blockHeader , PublicKey recipient){
        String header = blockHeader.toString();

        int target = difficulty_1_target / blockHeader.difficulty;
        int nBits = 64 - target;

        String answer = "";
        long nonce = 0;
        for(int i = 0; i < nBits; i ++){
            answer += "0";
        }
        String result = header + nonce;
        while(!Encryption.getSHA256(result).startsWith(answer)){
            result = header + (++ nonce);
        }
        //挖矿成功后给挖矿者发钱
        OutPut outPut = new OutPut(recipient, 10, "-1");
        BlockChain.UTXOs.put(Encryption.getSHA256(outPut.toString()),outPut);
        return nonce;
    }



}
