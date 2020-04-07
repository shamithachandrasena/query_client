package com.samsl.queyclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



@Component
@Scope("prototype")
public class QueryExecutorThread implements Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryExecutorThread.class);

    @Override
    public void run() {
        LOGGER.info("Called from thread");

    }
}
