package com.samsl.queyclient;

import com.samsl.Limiter;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class ClientController {

    @Value("${hosts}")
    public String hosts;

    @Value("${ports}")
    public String ports;

    @Value("${services}")
    public int noOfServices;

    @Autowired
    AsynchronousService asynchronousService;

    private final static Logger logger = LoggerFactory.getLogger(ClientController.class);
    private final StopWatch watch = new StopWatch();

    @RequestMapping("client")
    public Object clientExecutor(@RequestBody String query) throws ExecutionException, InterruptedException {
        if(!watch.isStarted()){
            watch.start();
        }else{
            watch.stop();
            watch.reset();
            watch.start();
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        List<CompletableFuture<Object>> resultList = new ArrayList<CompletableFuture<Object>>();

        for(int i=1; i<=noOfServices; i++){
            try {
                resultList.add(asynchronousService.executeQuery("http://"+Limiter.getSeperatedHostName(hosts,i)+":"+Limiter.getSeperatedPortNumber(ports,i)+"/query", restTemplate, new HttpEntity<>(Limiter.querySplit(query,i,noOfServices),headers)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Wait until they are all done
        CompletableFuture.allOf(resultList.toArray(new CompletableFuture[resultList.size()])).join();
        List jsonList = new ArrayList<>();
        for(int i=0; i<noOfServices; i++){
            try {
                jsonList.addAll((Collection) resultList.get(i).get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(watch.isStarted()) {
            watch.stop();
        }
        logger.info("Services:"+noOfServices+":No of Rows: "+Limiter.getTotalCount(query)+":Time Elapsed: " + watch.getTime());
        watch.reset();
        return "success";
    }
}
