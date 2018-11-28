package com.ymkj.credit.task;

import com.ymkj.credit.service.DictionaryService;
import java.util.Date;
import javax.annotation.PostConstruct;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时同步字典表数据到sysSysParameterMap
 * <p/>
 * Author: tianx
 * Date: 2017/8/30 14:23
 */
@Log4j
@Component
public class SyncSysParamTask {

    @Autowired
    private DictionaryService dictionaryService;

    @PostConstruct
    @Scheduled(cron = "0 0 0/1 * * ? ")
    public void updateSysParamMap() {// 定时器的任务方法不能有返回值
        log.info("执行更新系统字典参数任务开始:"+new Date());
        dictionaryService.init();
        log.info("执行更新系统字典参数任务结束:"+new Date());
    }
}