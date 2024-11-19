package org.camunda.community.migration.example.loadbalancer;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.CorrelateMessageResponse;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class LoadbalancerService {

    public static final String PROCESS_ID = "BenchmarkProcess";

    @Autowired
    private ClusterProperties clusterProperties;

    @Autowired
    private MigrationProperties migrationProperties;

    private ZeebeClient oldZeebeClient;
    private ZeebeClient newZeebeClient;

    @PostConstruct
    public void init() {
        // create clients
        oldZeebeClient = createZeebeClient("old");
        newZeebeClient = createZeebeClient("new");

        // test config
        oldZeebeClient.newTopologyRequest().send().join();
        newZeebeClient.newTopologyRequest().send().join();
    }

    private ZeebeClient createZeebeClient(String oldNew) {
        OAuthCredentialsProvider credentialsProvider = new OAuthCredentialsProviderBuilder()
                .authorizationServerUrl(clusterProperties.getClusters().get(oldNew).getAuthUrl())
                .audience("zeebe-api")
                .clientId(clusterProperties.getClusters().get(oldNew).getClientId())
                .clientSecret(clusterProperties.getClusters().get(oldNew).getClientSecret()).build();

        URI grpcAddress = URI.create(clusterProperties.getClusters().get(oldNew).getZeebeGrpc());
        return ZeebeClient.newClientBuilder().grpcAddress(grpcAddress).credentialsProvider(credentialsProvider).build();
    }

    // new process instances will get created on old or new cluster based on property `migration.createNewInstancesTarget`
    public CompletableFuture<?> createProcessInstance(Map<String, Object> variables) {
        if( migrationProperties.getCreateNewInstancesTarget().equals("old")) {
            return oldZeebeClient.newCreateInstanceCommand().bpmnProcessId(PROCESS_ID).latestVersion().variables(variables).send().toCompletableFuture();
        } else if( migrationProperties.getCreateNewInstancesTarget().equals("new")) {
            return newZeebeClient.newCreateInstanceCommand().bpmnProcessId(PROCESS_ID).latestVersion().variables(variables).send().toCompletableFuture();
        } else {
            throw new RuntimeException("Please configure target cluster for new instances");
        }
    }

    // PublishMessageResponse doesn't tell, if message has been consumed. Therefore, simply send message to both clusters.
    public CompletableFuture<PublishMessageResponse> publishMessage(String correlationKey, String message) {
        return oldZeebeClient.newPublishMessageCommand().messageName(message).correlationKey(correlationKey).send().toCompletableFuture()
                .thenCompose( response ->
                        newZeebeClient.newPublishMessageCommand().messageName(message).correlationKey(correlationKey).send().toCompletableFuture());
    }

    // CorrelateMessageResponse uses REST, so a sync response will be returned
    public CorrelateMessageResponse correlateMessage(String correlationKey, String message) {
        try {
            return oldZeebeClient.newCorrelateMessageCommand().messageName(message).correlationKey(correlationKey).send().join();
        } catch( Exception e) {
            return newZeebeClient.newCorrelateMessageCommand().messageName(message).correlationKey(correlationKey).send().join();
        }

    }
}
