package com.github.ontio.dao;


import com.github.ontio.model.NotifyInfoDao;
import com.github.ontio.model.SendTxDetail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component(value = "SendTxDetailMapper")
public interface SendTxDetailMapper {
    int insertSendTxDetail(SendTxDetail info);
    int updateSendTxDetail(String txhash);
}
