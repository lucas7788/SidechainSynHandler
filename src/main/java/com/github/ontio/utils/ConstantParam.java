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


package com.github.ontio.utils;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.network.rpc.RpcClient;
import com.github.ontio.sdk.manager.ConnectMgr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouq
 * @version 1.0
 * @date 2018/2/27
 */
public final class ConstantParam {

    /**
     * 节点restfulurl列表
     */
    public static List<String> NODE_RESTFULURLLIST = new ArrayList<>();

    public static List<String> MAINCHAIN_RPCLIST = new ArrayList<>();
    public static List<String> SIDECHAIN_RPCLIST = new ArrayList<>();

    /**
     * 主节点restfulurl
     */
    public static String MAINCHAIN_RPCURL = "";
    public static String SIDECHAIN_RPCURL = "";

    /**
     * 主节点在列表中的序列号
     */
    public static int MAINNODE_INDEX = 0;
    public static int SIDENODE_INDEX = 0;

    /**
     * 尝试连接的最大次数
     */
    public static int NODE_RETRYMAXTIME = 0;

    /**
     * Ontology SDK object
     */
    public static OntSdk ONT_SDKSERVICE = null;
    public static ConnectMgr CONNECT_MGR = null;
    public static String ADMIN_ADDR = null;
    public static String ADMIN_PASSWORD = null;
    public static Account ADMIN_ACCOUNT = null;
    public static long GAS_LIMIT = 20000;
    public static long GAS_PRICE = 0;

    public static long SEND_TX_WAIT_TIME;
    public static long COLLECT_TX_NUM;



}
