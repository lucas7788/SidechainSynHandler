package com.github.ontio.model;

import java.util.List;

public class NotifyInfoDao {
    public String TxHash;
    public int blkHeight;
    public String funcName;
    public String sideChainId;
    public String address;
    public String amount;
    public String ContractAddress;

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTxHash() {
        return TxHash;
    }

    public void setTxHash(String txHash) {
        this.TxHash = txHash;
    }

    public int getBlkHeight() {
        return blkHeight;
    }

    public void setBlkHeight(int blkHeight) {
        this.blkHeight = blkHeight;
    }

    public String getContractAddress() {
        return ContractAddress;
    }

    public void setContractAddress(String contractAddress) {
        ContractAddress = contractAddress;
    }
}
