package com.github.ontio.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.dao.NotifyMapper;
import com.github.ontio.model.*;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.manager.ConnectMgr;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Common {

    public static long START_TIME_MAIN = 0;
    public static long START_TIME_SIDE = 0;
    public static String MAIN_CHAIN = "mainChain";
    public static String SIDE_CHAIN = "sideChain";
    public static BlockingQueue<NotifyEventInfo> QueueMainChain = new ArrayBlockingQueue<NotifyEventInfo>(100);
    public static BlockingQueue<NotifyEventInfo> QueueSideChain = new ArrayBlockingQueue<NotifyEventInfo>(100);
    public static BlockingQueue<NotifyEventInfo> QueueCommitPos = new ArrayBlockingQueue<NotifyEventInfo>(1);


    public static void monitor(Logger logger, NotifyMapper notifyMapper, String chainType, int height, MonitorParam[] params) throws ConnectorException, IOException, InterruptedException {
        Object event = null;
        if(chainType.equals(Common.MAIN_CHAIN)){
            event = ConstantParam.ONT_SDKSERVICE.getConnect().getSmartCodeEvent(height);
        } else {
            event = ConstantParam.ONT_SDKSERVICE.getSideChainConnectMgr().getSmartCodeEvent(height);
        }

        if(event != null){
            for (Object obj: (JSONArray)event) {
                SmartCodeEvent smartCodeEvent = JSONObject.toJavaObject((JSONObject)obj,SmartCodeEvent.class);
                if(smartCodeEvent.getState() !=1||smartCodeEvent.getNotify() == null|| smartCodeEvent.getNotify().size() == 0){
                    continue;
                }
                for(NotifyEventInfo info : smartCodeEvent.getNotify()){
                    for(MonitorParam param:params) {
                        if(info.ContractAddress.equals(param.contractAddress) && info.getStates().get(0).equals(param.functionName)){
                            info.setTxhash(smartCodeEvent.TxHash);
                            info.setChainType(chainType);
                            info.setBlkHeight(height);
                            logger.info("**************Notify:" + JSON.toJSONString(info));
                            if(chainType.equals(Common.MAIN_CHAIN)){
                                if(Common.START_TIME_MAIN == 0){
                                    Common.START_TIME_MAIN= System.currentTimeMillis();
                                }
                                insertMainEvent(notifyMapper, info);
                                if(info.ContractAddress.equals("0700000000000000000000000000000000000000") && info.getStates().get(0).equals("commitDpos")){
                                    Common.QueueCommitPos.put(info);
                                } else {
                                    Common.QueueMainChain.put(info);
                                }
                            }else {
                                if(Common.START_TIME_SIDE == 0) {
                                    Common.START_TIME_SIDE = System.currentTimeMillis();
                                }
                                insertSideEvent(notifyMapper, info);
                                Common.QueueSideChain.put(info);
                            }
                        }
                    }
                }
            }
        }
    }

    public static long convertAmount(Object amountEvent){
        long amount = 0;
        if(amountEvent.getClass().getName().equals("java.lang.Integer")){
            int amountTemp = (int) amountEvent;
            amount = (long) amountTemp;
        }else {
            amount = (long) amountEvent;
        }
        return amount;
    }

    public static void insertMainEvent(NotifyMapper notifyMapper,NotifyEventInfo info){
        NotifyInfoDao dao = new NotifyInfoDao();
        dao.setBlkHeight(info.blkHeight);
        dao.setContractAddress(info.ContractAddress);
        if(info.getStates().get(0).equals("ongSwap")) {
            dao.setFuncName("ongSwap");
            dao.setSideChainId((String) info.getStates().get(1));
            dao.setAddress((String) info.getStates().get(2));
            dao.setAmount(Long.toString(convertAmount(info.getStates().get(3))));
        }else if(info.getStates().get(0).equals("commitDpos")) {
            dao.setFuncName("commitDpos");
        }
        dao.setTxHash(info.txhash);
        notifyMapper.insertMainNotify(dao);
    }

    private static void insertSideEvent(NotifyMapper notifyMapper, NotifyEventInfo info){
        NotifyInfoDao dao = new NotifyInfoDao();
        dao.setBlkHeight(info.blkHeight);
        dao.setContractAddress(info.ContractAddress);
        if(info.getStates().get(0).equals("ongxSwap")) {
            dao.setFuncName("ongSwap");
            dao.setAddress((String) info.getStates().get(1));
            dao.setAmount(Long.toString(convertAmount(info.getStates().get(2))));
        }
        dao.setTxHash(info.txhash);
        notifyMapper.insertSideNotify(dao);
    }

    public static Object waitResult(ConnectMgr rpcClient, String hash) throws Exception {
        Object objEvent = null;
        Object objTxState = null;
        int notInpool = 0;
        for (int i = 0; i < 20; i++) {
            try {
                Thread.sleep(3000);
                objEvent = rpcClient.getSmartCodeEvent(hash);
                if (objEvent == null || objEvent.equals("")) {
                    Thread.sleep(1000);
                    objTxState = rpcClient.getMemPoolTxState(hash);
                    continue;
                }
                if (((Map) objEvent).get("Notify") != null) {
                    return objEvent;
                }
            } catch (Exception e) {
                if (e.getMessage().contains("UNKNOWN TRANSACTION") && e.getMessage().contains("getmempooltxstate")) {
                    notInpool++;
                    if ((objEvent.equals("") || objEvent == null) && notInpool >1){
                        throw new SDKException(e.getMessage());
                    }
                } else {
                    continue;
                }
            }
        }
        throw new SDKException(ErrorCode.OtherError("time out"));
    }
    public static boolean verifyResult(Object event){
        if((int)((Map)event).get("State") == 1){
            return true;
        } else {
            return false;
        }
    }
}
