package com.github.ontio.thread;


import com.github.ontio.OntSdk;
import com.github.ontio.asyncService.EventHandleService;
import com.github.ontio.dao.BlkHeightMainMapper;
import com.github.ontio.dao.NotifyMapper;
import com.github.ontio.model.*;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.utils.Common;
import com.github.ontio.utils.ConfigParam;
import com.github.ontio.utils.ConstantParam;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Component("MainChainThread")
@Scope("prototype")
@EnableTransactionManagement(proxyTargetClass = true)
public class MainChainThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(MainChainThread.class);

    private final String CLASS_NAME = this.getClass().getSimpleName();

    @Autowired
    private ConfigParam configParam;
    @Autowired
    private EventHandleService msgHandleService;
    @Autowired
    private Environment env;

    @Autowired
    private BlkHeightMainMapper blkHeightMainMapper;
    @Autowired
    private NotifyMapper notifyMapper;

    @Override
    public void run() {

        logger.info("========{}.run=======", CLASS_NAME);
        try {


            //初始化node列表
            initNodeRpcList();

            long startTime = System.currentTimeMillis();

            int oneBlockTryTime = 1;
            while (true) {

                int remoteBlockHieght = getRemoteBlockHeight();
                logger.info("######remote blockheight:{}", remoteBlockHieght);

                int dbBlockHeight = blkHeightMainMapper.selectDBHeight();
                logger.info("######db blockheight:{}", dbBlockHeight);
                dbBlockHeight = dbBlockHeight +1;
                logger.info("#####################startTime:{}", startTime);
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

//              根据高度获得事件
                MonitorParam param1 = new MonitorParam("0800000000000000000000000000000000000000","ongSwap");
                MonitorParam param2 = new MonitorParam("0700000000000000000000000000000000000000","commitDpos");
                Common.monitor(logger,notifyMapper,Common.MAIN_CHAIN,dbBlockHeight,new MonitorParam[]{param1,param2});
                long currTime = System.currentTimeMillis();
                if(Common.QueueCommitPos!=null && Common.QueueCommitPos.size() != 0){
                    List<NotifyEventInfo> infoList = new ArrayList<>();
                    infoList.add(Common.QueueCommitPos.take());
                    msgHandleService.handleEventList(infoList);
                } else if(Common.QueueMainChain != null && Common.QueueMainChain.size() >0 &&(
                        Common.QueueMainChain.size() >= ConstantParam.COLLECT_TX_NUM || currTime-startTime >= ConstantParam.SEND_TX_WAIT_TIME)){
                    startTime = currTime;
                    List<NotifyEventInfo> infoList = new ArrayList<>();
                    int size = Common.QueueMainChain.size();
                    for(int i=0;i < size; i++) {
                        infoList.add(Common.QueueMainChain.take());
                    }
                    msgHandleService.handleEventList(infoList);
                }
                blkHeightMainMapper.update(dbBlockHeight);
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
                remoteHeight = ConstantParam.ONT_SDKSERVICE.getConnect().getBlockHeight();
                break;
            } catch (ConnectorException ex) {
                logger.error("getBlockHeight error, try again...restful:{},error:", ConstantParam.MAINCHAIN_RPCURL, ex);
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
    private void initNodeRpcList() {
        for (int i = 0; i < configParam.NODE_AMOUNT; i++) {
            ConstantParam.MAINCHAIN_RPCLIST.add(env.getProperty("mainchain.rpc.url_" + i));
        }
    }
    private void switchNode() {
        ConstantParam.MAINNODE_INDEX++;
        if (ConstantParam.MAINNODE_INDEX >= configParam.NODE_AMOUNT) {
            ConstantParam.MAINNODE_INDEX = 0;
        }
        ConstantParam.MAINCHAIN_RPCURL = ConstantParam.NODE_RESTFULURLLIST.get(ConstantParam.MAINNODE_INDEX);
        logger.warn("####switch node rpc to {}####", ConstantParam.MAINCHAIN_RPCURL);

        ConstantParam.ONT_SDKSERVICE.setRpc(ConstantParam.MAINCHAIN_RPCURL);
    }
}
