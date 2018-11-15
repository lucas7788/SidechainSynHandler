package com.github.ontio.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.common.Address;
import com.github.ontio.core.sidechaingovernance.SwapParam;
import com.github.ontio.model.*;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.network.rest.X509;
import com.github.ontio.paramBean.Result;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sidechain.smartcontract.ongx.Swap;
import com.github.ontio.utils.Common;
import com.github.ontio.utils.ConstantParam;
import com.github.ontio.shadowexeception.ShadowErrorCode;
import com.github.ontio.shadowexeception.ShadowException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Component("SendTransactionThread")
@Scope("prototype")
@EnableTransactionManagement(proxyTargetClass = true)
public class SendTransactionThread {
    private static final Logger logger = LoggerFactory.getLogger(SendTransactionThread.class);
    private static final String DEFAULT_CHARSET = "UTF-8";

    @Async
    public Future<String> asyncHandleEvent(SqlSession session, List<NotifyEventInfo> infoList) throws Exception {

        String threadName = Thread.currentThread().getName();
        logger.info("{} run-----------------------------------", threadName);

        ConcurrentHashMap<String, Object> paramMap = handleEvent(session, infoList);
        sendTx(session, paramMap);

        logger.info("{} end-----------------------------------", threadName);
        return new AsyncResult<String>("success");
    }

    private ConcurrentHashMap<String, Object> handleEvent(SqlSession session, List<NotifyEventInfo> infoList) throws ShadowException {
        ConcurrentHashMap map = new ConcurrentHashMap();
        List<Swap> swapList = new ArrayList();
        List<SwapParam> swapParamList = new ArrayList<>();
        for(NotifyEventInfo info : infoList) {
            if(info.getChainType().equals(Common.MAIN_CHAIN)) {
                if(info.States.get(0).equals("ongSwap")){
                    String sideChainId = (String) info.getStates().get(1);
                    String address = (String) info.States.get(2);
                    long amount = 0;
                    if(info.States.get(3).getClass().getName().equals("java.lang.Integer")){
                        amount = (int)info.States.get(3);
                        amount = (long) amount;
                    }else {
                        amount = (long)info.States.get(3);
                    }
                    try {
                        Swap swap = new Swap(Address.decodeBase58(address), amount);
                        swapList.add(swap);
                        insertSendDetail(session, "", info.blkHeight,Long.toString(swap.value), Common.SIDE_CHAIN,"",
                                "ongSwap",sideChainId,swap.address.toBase58());
                    } catch (SDKException e) {
                        e.printStackTrace();
                    }
                }else if(info.getStates().get(0).equals("commitDpos")) {
                    try {
                        String sideChainId2 = ConstantParam.ONT_SDKSERVICE.sidechainVm().governance().getSideChainId();
                        String sideChainData = ConstantParam.ONT_SDKSERVICE.getConnect().getSideChainData(sideChainId2);
                        map.put("commitPos", sideChainData);
//                        要从其他节点获得已经签过名的sidechaindata
//                        boolean b = verifySideChainData(sideChainData);
//                        if(b) {
//                            map.put("commitPos", sideChainData);
//                        }
                    } catch (ConnectorException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SDKException e) {
                        e.printStackTrace();
                    }

                }
            } else if(info.getChainType().equals(Common.SIDE_CHAIN)) {
                if(info.getStates().get(0).equals("ongxSwap")) {
                    String address = (String) info.getStates().get(1);
                    long amount = 0;
                    if(info.States.get(2).getClass().getName().equals("java.lang.Integer")){
                        amount = (int)info.States.get(2);
                    }else {
                        amount = (long)info.States.get(2);
                    }
                    try {
                        String sideChainId = ConstantParam.ONT_SDKSERVICE.sidechainVm().governance().getSideChainId();
                        SwapParam param = new SwapParam(sideChainId,Address.decodeBase58(address),amount);
                        swapParamList.add(param);
                        insertSendDetail(session, "", info.blkHeight,Long.toString(amount), Common.SIDE_CHAIN,"",
                                "ongSwap",sideChainId,address);
                    } catch (ConnectorException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SDKException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if(swapList.size() !=0) {
            Swap[] swaps = new Swap[swapList.size()];
            for(int i=0; i < swapList.size(); i++) {
                swaps[i] = swapList.get(i);
            }
            map.put("ongSwap", swaps);
        }
        if(swapParamList.size() != 0) {
            SwapParam[] params = new SwapParam[swapParamList.size()];
            for(int i=0; i < swapParamList.size(); i++) {
                params[i] = swapParamList.get(i);
            }
            map.put("ongxSwap", params);
        }
        return map;
    }

    public void sendTx(SqlSession session, ConcurrentHashMap<String, Object> paramMap) throws Exception {
        Object sideChainData = paramMap.get("commitPos");
        if(sideChainData != null) {
            String txhash = ConstantParam.ONT_SDKSERVICE.sidechainVm().governance().commitDpos(ConstantParam.ADMIN_ACCOUNT,
                    (String) sideChainData,ConstantParam.ADMIN_ACCOUNT,ConstantParam.GAS_LIMIT,ConstantParam.GAS_PRICE);
            Object res = Common.waitResult(ConstantParam.ONT_SDKSERVICE.getSideChainConnectMgr(), txhash);
            if(!Common.verifyResult(res)){
                System.out.println("failed transaction, txhash is : " + txhash);
                insertMsgInfo(session,"","commitPos",txhash,"failed","commitPos failed");
                throw new ShadowException(ShadowErrorCode.OtherError("commitDpos error"));
            }
            insertMsgInfo(session, "","commitPos",txhash,"success", "");
        }
        Object swaps = paramMap.get("ongSwap");
        if(swaps != null) {
            String txhash = ConstantParam.ONT_SDKSERVICE.sidechainVm().ongX().ongSwap(ConstantParam.ADMIN_ACCOUNT,(Swap[]) swaps,
                    ConstantParam.ADMIN_ACCOUNT, ConstantParam.GAS_LIMIT,ConstantParam.GAS_PRICE);
            Object res = Common.waitResult(ConstantParam.ONT_SDKSERVICE.getSideChainConnectMgr(), txhash);
            if(!Common.verifyResult(res)){
                insertMsgInfo(session, "","ongSwap",txhash,"success","");
                System.out.println("failed transaction, txhash is : " + txhash);
                throw new ShadowException(ShadowErrorCode.OtherError("ongSwap error"));
            }else {
                insertMsgInfo(session, "","ongSwap",txhash,"success","");
                updateSendDetail(session, txhash);
            }
        }
        Object swapParam = paramMap.get("ongxSwap");
        if(swapParam != null) {
            String txhash = ConstantParam.ONT_SDKSERVICE.nativevm().sideChainGovernance().ongxSwap(ConstantParam.ADMIN_ACCOUNT,(SwapParam[]) swapParam,
                    ConstantParam.ADMIN_ACCOUNT,ConstantParam.GAS_LIMIT,ConstantParam.GAS_PRICE);
            Object res = Common.waitResult(ConstantParam.ONT_SDKSERVICE.getConnect(), txhash);
            if(!Common.verifyResult(res)){
                System.out.println("failed transaction, txhash is : " + txhash);
                insertMsgInfo(session,"","ongxSwap",txhash,"failed","");
                throw new ShadowException(ShadowErrorCode.OtherError("commitDpos error"));
            }
            insertMsgInfo(session,"","ongxSwap",txhash,"success","");
            updateSendDetail(session, txhash);
        }
    }
    public void insertMsgInfo(SqlSession session, String nodeUrl,String functionName, String txhash,
                              String result, String description) {
        MsgInfo info = new MsgInfo();
        info.setNodeUrl(nodeUrl);
        info.setFunctionName(functionName);
        info.setTxhash(txhash);
        info.setResult(result);
        info.setDescription(description);
        session.insert("com.github.ontio.dao.SendTxMsgInfoMapper.insert", info);
    }

    private void insertSendDetail(SqlSession session, String txhash, int blkHeight, String amount, String chainType,
                                  String nodeUrl, String functionName, String sideChainId, String address){
        SendTxDetail txDetail = new SendTxDetail();
        txDetail.setTxhash(txhash);
        if(functionName.equals("ongSwap")) {
            txDetail.setSideChainId(sideChainId);
            txDetail.setAddress(address);
            txDetail.setAmount(amount);
            txDetail.setBlkHeight(blkHeight);
            txDetail.setChainType(chainType);
            txDetail.setFunctionName(functionName);
            txDetail.setNodeUrl(nodeUrl);
        }
        session.insert("com.github.ontio.dao.SendTxDetailMapper.insertSendTxDetail", txDetail);
    }
    private void updateSendDetail(SqlSession session, String txhash){
        session.insert("com.github.ontio.dao.SendTxDetailMapper.updateSendTxDetail", txhash);
    }

    private boolean verifySideChainData(String sideChainData) throws ShadowException {
        if(ConstantParam.MAINCHAIN_RPCLIST.size() < 7) {
            throw new ShadowException(ShadowErrorCode.OtherError("main chain rpc list less tha"));
        }
        HashSet<String> sideChainDataSet = new HashSet<String>();
        sideChainDataSet.add(sideChainData);
        for(String nodeUrl : ConstantParam.MAINCHAIN_RPCLIST) {
            String result = getSideChainData(nodeUrl);
            JSONObject obj = JSON.parseObject(result);
            JSONObject res = JSON.parseObject(obj.getString("Result"));
            String sideChainDataTemp = res.getString("sideChainData");
            String signature = res.getString("signature");
            try {
                boolean b = ConstantParam.ONT_SDKSERVICE.verifySignature(ConstantParam.ADMIN_ACCOUNT.serializePublicKey(),
                        sideChainDataTemp.getBytes(), signature.getBytes());
                if(b) {
                    sideChainDataSet.add(sideChainData);
                }
            } catch (SDKException e) {
                e.printStackTrace();
            }
        }
        if(sideChainDataSet.isEmpty()) {
            throw new ShadowException(ShadowErrorCode.OtherError("verifySignature failed"));
        }else if (sideChainDataSet.size() > 1){
            throw new ShadowException(ShadowErrorCode.OtherError("sidechaindata is different"));
        }
        return true;
    }

    private String getSideChainData(String nodeUrl)  {
        String URL_getSideChainData = "/api/v1/commitdpos/getsidechaindata";
        try {
            return get(URL_getSideChainData, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String get(String url  ,boolean https) throws Exception {
        URL u = new URL(url);
        HttpURLConnection http = (HttpURLConnection) u.openConnection();
        http.setConnectTimeout(50000);
        http.setReadTimeout(50000);
        http.setRequestMethod("GET");
        http.setRequestProperty("Content-Type","application/json");
        if(https) {
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, new TrustManager[]{new X509()}, new SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            ((HttpsURLConnection)http).setSSLSocketFactory(ssf);
        }
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        StringBuilder sb = new StringBuilder();
        try (InputStream is = http.getInputStream()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, DEFAULT_CHARSET))) {
                String str = null;
                while((str = reader.readLine()) != null) {
                    sb.append(str);
                    str = null;
                }
            }
        }
        if (http != null) {
            http.disconnect();
        }
        return sb.toString();
    }
}
