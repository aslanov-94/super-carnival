package az.ingress.k8s.client.dto;

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
public class K8sResourceOwnerDto {

    private String apiVersion;

    private String kind;

    private String name;

}
