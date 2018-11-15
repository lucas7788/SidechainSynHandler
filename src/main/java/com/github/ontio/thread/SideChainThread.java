package com.github.ontio.thread;


import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.dao.BlkHeightSideMapper;
import com.github.ontio.dao.NotifyMapper;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.manager.ConnectMgr;
import com.github.ontio.asyncService.EventHandleService;
import com.github.ontio.model.*;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.utils.Common;
import com.github.ontio.utils.ConfigParam;
import com.github.ontio.utils.ConstantParam;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component("SideChainThread")
@Scope("prototype")
public class SideChainThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(SideChainThread.class);

    private final String CLASS_NAME = this.getClass().getSimpleName();

    @Autowired
    private ConfigParam configParam;
    @Autowired
    private EventHandleService msgHandleService;
    @Autowired
    private BlkHeightSideMapper blkHeightSideMapper;
    @Autowired
    private Environment env;
    @Autowired
    private NotifyMapper notifyMapper;


    @Override
    public void run() {

        logger.info("========{}.run=======", CLASS_NAME);
        try {

            ConstantParam.MAINCHAIN_RPCURL = configParam.MAINCHAIN_RPC_URL;
            //初始化node列表
            initNodeRestfulList();

            int oneBlockTryTime = 1;

            while (true) {

                int remoteBlockHieght = getRemoteBlockHeight();
                logger.info("######remote blockheight:{}", remoteBlockHieght);

                int dbBlockHeight = blkHeightSideMapper.selectDBHeight();
                dbBlockHeight = dbBlockHeight + 1;
                logger.info("######db blockheight:{}", dbBlockHeight);

                //wait for generating block
                if (dbBlockHeight >= remoteBlockHieght) {
                    logger.info("+++++++++wait for block+++++++++");
                    try {
                        Thread.sleep(configParam.BLOCK_INTERVAL);
                    } catch (InterruptedException e) {
                        logger.error("error...", e);
                        e.printStackTrace();
                    }
                    oneBlockTryTime++;
                    if (oneBlockTryTime >= configParam.NODE_WAITFORBLOCKTIME_MAX) {
                        switchNode();
                        oneBlockTryTime = 1;
                    }
                    continue;
                }

                oneBlockTryTime = 1;

                //每次删除当前current表height+1的交易，防止上次程序异常退出时，因为多线程事务插入了height+1的交易而current表height未更新
                //本次同步再次插入会报主键重复异常
//              根据高度获得事件
                MonitorParam param = new MonitorParam(configParam.ASSET_ONG_CODEHASH,"ongxSwap");
                Common.monitor(logger,notifyMapper,Common.SIDE_CHAIN,dbBlockHeight,new MonitorParam[]{param});
                long currTime = System.currentTimeMillis();
                if(Common.QueueSideChain != null && Common.QueueSideChain.size() >0 &&(
                        Common.QueueSideChain.size() >= ConstantParam.COLLECT_TX_NUM || currTime-Common.START_TIME_SIDE >= ConstantParam.SEND_TX_WAIT_TIME)){
                    Common.START_TIME_SIDE = 0;
                    List<NotifyEventInfo> infoList = new ArrayList<>();
                    int size = Common.QueueSideChain.size();
                    for(int i=0;i < size; i++) {
                        infoList.add(Common.QueueSideChain.take());
                    }
                    msgHandleService.handleEventList(infoList);
                }
                blkHeightSideMapper.update(dbBlockHeight);
            }
        } catch (Exception e) {
            logger.error("Exception occured，Synchronization thread can't work,error ...", e);
        }

    }
    private int getRemoteBlockHeight() throws Exception {

        int remoteHeight = 0;
        int tryTime = 1;
        while (true) {
            try {
                remoteHeight = ConstantParam.ONT_SDKSERVICE.getSideChainConnectMgr().getBlockHeight();
                break;
            } catch (ConnectorException ex) {
                logger.error("getBlockHeight error, try again...restful:{},error:", ConstantParam.SIDECHAIN_RPCLIST, ex);
                if (tryTime % configParam.NODE_INTERRUPTTIME_MAX == 0) {
                    switchNode();
                    tryTime++;
                    continue;
                } else {
                    tryTime++;
                    Thread.sleep(1000);
                    continue;
                }
            } catch (IOException e) {
                logger.error("get blockheight thread can't work,error {} ", e);
                throw new Exception(e);
            }
        }

        return remoteHeight;
    }
    private void initNodeRestfulList() {

        for (int i = 0; i < configParam.NODE_AMOUNT; i++) {
            ConstantParam.SIDECHAIN_RPCLIST.add(env.getProperty("sidechain.restful.url_" + i));
        }
    }
    private void switchNode() {
        ConstantParam.SIDENODE_INDEX++;
        if (ConstantParam.SIDENODE_INDEX >= configParam.NODE_AMOUNT) {
            ConstantParam.SIDENODE_INDEX = 0;
        }
        ConstantParam.SIDECHAIN_RPCURL = ConstantParam.SIDECHAIN_RPCLIST.get(ConstantParam.SIDENODE_INDEX);
        logger.warn("####switch node restfulurl to {}####", ConstantParam.SIDECHAIN_RPCURL);

        ConstantParam.ONT_SDKSERVICE.setSideChainRpc(ConstantParam.SIDECHAIN_RPCURL);
    }
}
