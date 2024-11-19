package org.camunda.community.migration.example.loadbalancer;

import io.camunda.zeebe.client.api.response.CorrelateMessageResponse;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/process")
public class ProcessController {

    private static final Logger LOG = LoggerFactory.getLogger( ProcessController.class );

    @Autowired
    private LoadbalancerService loadbalancerService;

    @PostMapping("/create")
    public CompletableFuture<?> createProcessInstance(@RequestBody Map<String, Object> variables) {
        return loadbalancerService.createProcessInstance(variables);
    }

    @PostMapping("/publish/{correlationKey}/{messageName}")
    public CompletableFuture<PublishMessageResponse> publishMessage(@PathVariable("correlationKey") String correlationKey,
            @PathVariable("messageName") String messageName) {
        return loadbalancerService.publishMessage(correlationKey, messageName);
    }

    @PostMapping("/correlate/{correlationKey}/{messageName}")
    public CorrelateMessageResponse  correlateMessage(@PathVariable("correlationKey") String correlationKey, @PathVariable("messageName") String messageName) {
        return loadbalancerService.correlateMessage(correlationKey, messageName);
    }

}
