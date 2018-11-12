package com.github.ontio.dao;


import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component(value = "BlkHeightMainMapper")
public interface BlkHeightMainMapper {
    int update(Integer height);
    int selectDBHeight();
}
