package com.github.ontio.asyncService;

import com.github.ontio.dao.BlkHeightMainMapper;
import com.github.ontio.model.NotifyEventInfo;
import com.github.ontio.thread.SendTransactionThread;
import com.github.ontio.utils.Helper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Future;

@Service
public class EventHandleService {
    private static final Logger logger = LoggerFactory.getLogger(EventHandleService.class);

    @Autowired
    private SendTransactionThread sendTransactionThread;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;



    /**
     * handle the block and the transactions in this block
     *
     * @param infoList
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleEventList(List<NotifyEventInfo> infoList) throws Exception {

        //设置一个模式为BATCH，自动提交为false的session，最后统一提交，需防止内存溢出
        SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);

        logger.info("{} run-------", Helper.currentMethod());

        try {
            //asynchronize handle transaction
            //future.get() 主线程阻塞等待
            Future future = sendTransactionThread.asyncHandleEvent(session, infoList);
            future.get();
            // 手动提交
            session.commit();
            // 清理缓存，防止溢出
            session.clearCache();
            logger.info("###batch insert success!!");
        } catch (Exception e) {
            logger.error("error...session.rollback", e);
            session.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
