package az.ingress.k8s.client.dto;

import az.ingress.k8s.client.enums.ResourceKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class K8sResourceRelationDto {

    private ResourceKind kind;

    private String name;

}
