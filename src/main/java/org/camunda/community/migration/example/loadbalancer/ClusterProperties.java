package org.camunda.community.migration.example.loadbalancer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "clusters")
public class ClusterProperties {

    private Map<String, ClusterConfig> clusters;

    public Map<String, ClusterConfig> getClusters() {
        return clusters;
    }

    public void setClusters(Map<String, ClusterConfig> clusters) {
        this.clusters = clusters;
    }

    public static class ClusterConfig {
        private String zeebeGrpc;
        private String zeebeRest;
        private String authUrl;
        private String clientId;
        private String clientSecret;

        public String getZeebeGrpc() {
            return zeebeGrpc;
        }

        public void setZeebeGrpc(String zeebeGrpc) {
            this.zeebeGrpc = zeebeGrpc;
        }

        public String getAuthUrl() {
            return authUrl;
        }

        public void setAuthUrl(String authUrl) {
            this.authUrl = authUrl;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        @Override
        public String toString() {
            return "ClusterConfig{" +
                    "zeebeGrpc='" + zeebeGrpc + '\'' +
                    ", authUrl='" + authUrl + '\'' +
                    ", clientId='" + clientId + '\'' +
                    ", clientSecret='[PROTECTED]'}";
        }

        public String getZeebeRest() {
            return zeebeRest;
        }

        public ClusterConfig setZeebeRest(String zeebeRest) {
            this.zeebeRest = zeebeRest;
            return this;
        }
    }
}
