package com.github.ontio.dao;

import com.github.ontio.model.NotifyEventInfo;
import com.github.ontio.model.NotifyInfoDao;
import com.github.ontio.model.SmartCodeEvent;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component(value = "NotifyMapper")
public interface NotifyMapper {
    int insertMainNotify(NotifyInfoDao info);
    int insertSideNotify(NotifyInfoDao info);
    NotifyInfoDao selectByPrimaryKey(String txhash);
}
