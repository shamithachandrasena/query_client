package com.samsl.queyclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class AsynchronousService {
    @Async("asyncExecutor")
    public CompletableFuture<Object> executeQuery(String uri, RestTemplate restTemplate, HttpEntity entity) throws InterruptedException {
        ResponseEntity<Object> results = restTemplate.exchange(uri, HttpMethod.POST,entity, Object.class);
        return CompletableFuture.completedFuture(results.getBody());
    }
}
