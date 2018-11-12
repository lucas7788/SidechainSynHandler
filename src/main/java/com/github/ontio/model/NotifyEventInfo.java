package com.github.ontio.model;

import java.util.List;

public class NotifyEventInfo {
    public int blkHeight;
    public List<Object> States;
    public String ContractAddress;
    public String txhash;
    public String chainType;

    public NotifyEventInfo() {
    }

    public String getChainType() {
        return chainType;
    }

    public void setChainType(String chainType) {
        this.chainType = chainType;
    }

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

    public List<Object> getStates() {
        return this.States;
    }

    public void setStates(List<Object> states) {
        this.States = states;
    }

    public String getContractAddress() {
        return this.ContractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.ContractAddress = contractAddress;
    }
}
