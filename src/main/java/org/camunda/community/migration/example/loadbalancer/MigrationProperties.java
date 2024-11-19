package org.camunda.community.migration.example.loadbalancer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "migration")
public class MigrationProperties {

    private String createNewInstancesTarget;

    public String getCreateNewInstancesTarget() {
        return createNewInstancesTarget;
    }

    public void setCreateNewInstancesTarget(String createNewInstancesTarget) {
        this.createNewInstancesTarget = createNewInstancesTarget;
    }

    @Override
    public String toString() {
        return "MigrationProperties{" +
               "createNewInstancesTarget='" + createNewInstancesTarget + '\'' +
               '}';
    }
}
