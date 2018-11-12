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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * @author zhouq
 * @date 2018/2/27
 */

@Service("ConfigParam")
public class ConfigParam {

    /**
     * ontology blockchain restful url
     */
    @Value("${mainchain.rpc.url}")
    public String MAINCHAIN_RPC_URL;

    @Value("${sidechain.rpc.url}")
    public String SIDECHAIN_RPC_URL;

    @Value("${admin.wallet.file}")
    public String ADMIN_WALLET;
    @Value("${admin.address}")
    public String ADMIN_ADDRESS;
    @Value("${admin.password}")
    public String ADMIN_PASSWORD;
    @Value("${gaslimit}")
    public String GASLIMIT;
    @Value("${gasprice}")
    public String GASPRICE;

    @Value("${sendtx.wait.time}")
    public String SEND_TX_WAIT_TIME;

    @Value("${collect_tx.num}")
    public String COLLECT_TX_NUM;

    /**
     * the amount of the ontology blockchain nodes in properties
     */
    @Value("${node.amount}")
    public int NODE_AMOUNT;

    /**
     * the interval for waiting block generation
     */
    @Value("${block.interval}")
    public int BLOCK_INTERVAL;

    /**
     * each node fault tolerance maximum time.
     */
    @Value("${node.interruptTime.max}")
    public int NODE_INTERRUPTTIME_MAX;

    /**
     * the maximum time of each node for waiting for generating block
     */
    @Value("${node.waitForBlockTime.max}")
    public int NODE_WAITFORBLOCKTIME_MAX;



    /**
     * ontology blockchain ONG asset smartcontract codehash
     */
    @Value("${asset.ong.codeHash}")
    public String ASSET_ONG_CODEHASH;


    @Value("${threadPoolSize.max}")
    public int THREADPOOLSIZE_MAX;

    @Value("${threadPoolSize.core}")
    public int THREADPOOLSIZE_CORE;

    @Value("${threadPoolSize.queue}")
    public int THREADPOOLSIZE_QUEUE;

    @Value("${threadPoolSize.keepalive.second}")
    public int THREADPOOLSIZE_KEEPALIVE_SECOND;

}