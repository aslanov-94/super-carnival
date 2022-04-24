package az.ingress.k8s.client.service.kubernetes;

import az.ingress.k8s.client.dto.KubernetesResourceDto;

import java.util.Map;

public interface PodService extends K8sResourceMapper {

    /**
     * Separate method needed because of pod holds environment and volume information. That is why custom pod
     * mining logic created.
     *
     * @param namespace namespace need to get pods
     * @return Map&lt;PodName, KubernetesResourceDto&gt; -> DTO holds used resources info and owner references as well
     */
    Map<String, KubernetesResourceDto> getPods(String namespace);


}
