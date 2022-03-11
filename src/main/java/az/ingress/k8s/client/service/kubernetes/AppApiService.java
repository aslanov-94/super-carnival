package az.ingress.k8s.client.service.kubernetes;

import az.ingress.k8s.client.dto.KubernetesResourceDto;

import java.util.Map;

public interface AppApiService extends K8sResourceMapper {
    Map<String, KubernetesResourceDto> getDeployments(String namespace);

    Map<String, KubernetesResourceDto> getStatefulSet(String namespace);

    Map<String, KubernetesResourceDto> getReplicaSet(String namespace);
}
