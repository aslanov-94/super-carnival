package az.ingress.k8s.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KubernetesResourceDto {

    private String name;
    private String namespace;
    private String kind;
    private String age;
    private Map <String, String> labels;
    private List<K8sResourceOwnerDto> resourceOwners;

    private String status;
    private Integer count; //replica count, container count, data count, or nese

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KubernetesResourceDto that = (KubernetesResourceDto) o;
        return Objects.equals(name, that.name)
                && Objects.equals(namespace, that.namespace)
                && Objects.equals(kind, that.kind);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, namespace, kind);
    }
}
