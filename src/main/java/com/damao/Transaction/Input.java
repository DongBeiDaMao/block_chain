package com.damao.Transaction;

public class Input {
    public String outPutId;
    public OutPut unspentOutput;

    public Input(String outputId) {
        this.outPutId = outputId;
    }
}
