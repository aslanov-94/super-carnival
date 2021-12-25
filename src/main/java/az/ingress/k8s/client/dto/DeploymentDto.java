package az.ingress.k8s.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DeploymentDto {

    private String name;
    private String namespace;
    private Map<String, String> labels;
    private Map<String, String> labelSelector;
    private Integer replicas;
    private StrategyDto strategy;
    private Integer readyReplicas;

}
