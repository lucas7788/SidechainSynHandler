package com.github.ontio.dao;

import com.github.ontio.model.MsgInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component(value = "SendTxMsgInfoMapper")
public interface SendTxMsgInfoMapper {
    int insert(MsgInfo info);
    MsgInfo selectByTxhash(String txhash);
}
