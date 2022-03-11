package az.ingress.k8s.client.service.kubernetes;

import az.ingress.k8s.client.dto.KubernetesResourceDto;

import java.util.Map;

public interface CoreApiService extends K8sResourceMapper {
    Map<String, KubernetesResourceDto> getPods(String namespace);
}
