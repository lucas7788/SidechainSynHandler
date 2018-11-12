/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 * The ontology is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ontology is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 */


package com.github.ontio;

import com.alibaba.fastjson.JSON;
import com.github.ontio.thread.MainChainThread;
import com.github.ontio.thread.SideChainThread;
import com.github.ontio.utils.ConfigParam;
import com.github.ontio.utils.ConstantParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author zhouq
 * @version 1.0
 * @date 2018/3/13
 */
@Component
public class TaskWareMainChain {

    @Autowired
    private ApplicationContextProvider applicationContextProvider;

    @Autowired
    private ConfigParam configParam;

    @PostConstruct
    public void init() {
        initParam();
        MainChainThread mainChainThread = applicationContextProvider.getBean("MainChainThread", MainChainThread.class);
        mainChainThread.start();
    }


    private void initParam(){
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