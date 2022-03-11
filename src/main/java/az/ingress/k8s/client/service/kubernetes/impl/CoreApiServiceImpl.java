package az.ingress.k8s.client.service.kubernetes.impl;

import az.ingress.k8s.client.dto.KubernetesResourceDto;
import az.ingress.k8s.client.service.kubernetes.ConnectionComponent;
import az.ingress.k8s.client.service.kubernetes.CoreApiService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class CoreApiServiceImpl extends ConnectionComponent implements CoreApiService {

    @Override
    public Map<String, KubernetesResourceDto> getPods(String namespace) {
        CoreV1Api coreV1Api = new CoreV1Api(client);
        namespace = Objects.nonNull(namespace) ? namespace : DEFAULT_NS;

        try {
            V1PodList v1PodList = coreV1Api.listNamespacedPod(namespace,
                    null, null, null, null,
                    null, null, null, null,
                    10, false);
            List<V1Pod> pods = v1PodList.getItems();

            return mapK8sObjectListToResourceMap(pods);
        } catch (ApiException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
