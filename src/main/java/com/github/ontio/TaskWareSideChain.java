package com.github.ontio;

import com.github.ontio.thread.MainChainThread;
import com.github.ontio.thread.SideChainThread;
import com.github.ontio.utils.ConfigParam;
import com.github.ontio.utils.ConstantParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TaskWareSideChain {
    @Autowired
    private ApplicationContextProvider applicationContextProvider;

    @Autowired
    private ConfigParam configParam;

    @PostConstruct
    public void init() {
        try {
//            等待数据进行初始化
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        initParam();
        SideChainThread sideChainThread = applicationContextProvider.getBean("SideChainThread", SideChainThread.class);
        sideChainThread.start();
    }

    private void initParam(){
        if(ConstantParam.ONT_SDKSERVICE == null){
            OntSdk sdkService = OntSdk.getInstance();
            sdkService.setRpc(configParam.MAINCHAIN_RPC_URL);
            sdkService.setSideChainRpc(configParam.SIDECHAIN_RPC_URL);
            ConstantParam.ONT_SDKSERVICE = sdkService;
            ConstantParam.ONT_SDKSERVICE.openWalletFile(configParam.ADMIN_WALLET);
            try {
                ConstantParam.ADMIN_ACCOUNT = ConstantParam.ONT_SDKSERVICE.getWalletMgr().getAccount(configParam.ADMIN_ADDRESS,configParam.ADMIN_PASSWORD);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ConstantParam.SEND_TX_WAIT_TIME = Long.valueOf(configParam.SEND_TX_WAIT_TIME);
            ConstantParam.COLLECT_TX_NUM = Long.valueOf(configParam.COLLECT_TX_NUM);

            ConstantParam.MAINCHAIN_RPCURL = configParam.MAINCHAIN_RPC_URL;
            ConstantParam.SIDECHAIN_RPCURL = configParam.SIDECHAIN_RPC_URL;
        }
    }
}
