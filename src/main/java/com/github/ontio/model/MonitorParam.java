package com.github.ontio.model;

public class MonitorParam {
    public String contractAddress;
    public String functionName;
    public MonitorParam(String contractAddress, String functionName){
        this.contractAddress = contractAddress;
        this.functionName = functionName;
    }
}
