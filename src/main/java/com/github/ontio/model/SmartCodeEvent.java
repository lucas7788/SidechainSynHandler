package com.github.ontio.model;

import java.util.List;

public class SmartCodeEvent {
    public String TxHash;
    public int State;
    public long GasConsumed;
    public List<NotifyEventInfo> Notify;

    public SmartCodeEvent() {
    }

    public String getTxHash() {
        return this.TxHash;
    }

    public void setTxHash(String txHash) {
        this.TxHash = txHash;
    }

    public int getState() {
        return this.State;
    }

    public void setState(int state) {
        this.State = state;
    }

    public long getGasConsumed() {
        return this.GasConsumed;
    }

    public void setGasConsumed(long gasConsumed) {
        this.GasConsumed = gasConsumed;
    }

    public List<NotifyEventInfo> getNotify() {
        return this.Notify;
    }

    public void setNotify(List<NotifyEventInfo> notify) {
        this.Notify = notify;
    }
}
