package az.ingress.k8s.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StrategyDto {

    private String type;
    private RollingUpdateDto rollingUpdate;

}
