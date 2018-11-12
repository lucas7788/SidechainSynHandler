package com.github.ontio.model;

public class SendTxDetail {
    public String txhash;
    public int blkHeight;
    public String amount;
    public String chainType;
    public String nodeUrl;
    public String functionName;
    public String sideChainId;
    public String address;


    public String getTxhash() {
        return txhash;
    }

    public void setTxhash(String txhash) {
        this.txhash = txhash;
    }

    public int getBlkHeight() {
        return blkHeight;
    }

    public void setBlkHeight(int blkHeight) {
        this.blkHeight = blkHeight;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getChainType() {
        return chainType;
    }

    public void setChainType(String chainType) {
        this.chainType = chainType;
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getSideChainId() {
        return sideChainId;
    }

    public void setSideChainId(String sideChainId) {
        this.sideChainId = sideChainId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
