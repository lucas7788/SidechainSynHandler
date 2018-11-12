package com.github.ontio.model;

import com.alibaba.fastjson.JSON;

import java.util.HashSet;
import java.util.Set;

public class MsgQueue {
    private Set<String> dataSet = new HashSet<>();
    public MsgQueue(){

    }
    public void addData(MsgInfo info){
        dataSet.add(JSON.toJSONString(info));
    }

    public Set<String> getDataSet() {
        return dataSet;
    }

    public  void removeData(String data){
        dataSet.remove(data);
    }
    public  void clear(){
        dataSet.clear();
    }
    public  int size() {
        return dataSet.size();
    }
}
