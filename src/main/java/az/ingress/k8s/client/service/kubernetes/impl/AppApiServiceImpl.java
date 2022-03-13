package az.ingress.k8s.client.service.kubernetes.impl;

import az.ingress.k8s.client.dto.KubernetesResourceDto;
import az.ingress.k8s.client.service.kubernetes.AppApiService;
import az.ingress.k8s.client.service.kubernetes.ConnectionComponent;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1DaemonSet;
import io.kubernetes.client.openapi.models.V1DaemonSetList;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1ReplicaSet;
import io.kubernetes.client.openapi.models.V1ReplicaSetList;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.openapi.models.V1StatefulSetList;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class AppApiServiceImpl extends ConnectionComponent implements AppApiService {

    @Override
    public Map<String, KubernetesResourceDto> getDeployments(String namespace) {
        AppsV1Api appsV1Api = new AppsV1Api(client);
        namespace = Objects.nonNull(namespace) ? namespace : DEFAULT_NS;

        try {
            V1DeploymentList v1DeploymentList = appsV1Api.listNamespacedDeployment(namespace,
                    null, null, null, null,
                    null, null, null, null,
                    10, false);
            List<V1Deployment> items = v1DeploymentList.getItems();

            return mapK8sObjectListToResourceMap(items);
        } catch (ApiException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, KubernetesResourceDto> getStatefulSet(String namespace) {
        AppsV1Api appsV1Api = new AppsV1Api(client);
        namespace = Objects.nonNull(namespace) ? namespace : DEFAULT_NS;

        try {
            V1StatefulSetList v1StatefulSetList = appsV1Api.listNamespacedStatefulSet(namespace,
                    null, null, null, null,
                    null, null, null, null,
                    10, false);
            List<V1StatefulSet> items = v1StatefulSetList.getItems();

            return mapK8sObjectListToResourceMap(items);
        } catch (ApiException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, KubernetesResourceDto> getReplicaSet(String namespace) {
        AppsV1Api appsV1Api = new AppsV1Api(client);
        namespace = Objects.nonNull(namespace) ? namespace : DEFAULT_NS;

        try {
            V1ReplicaSetList v1ReplicaSetList = appsV1Api.listNamespacedReplicaSet(namespace,
                    null, null, null, null,
                    null, null, null, null,
                    10, false);
            List<V1ReplicaSet> items = v1ReplicaSetList.getItems();

            return mapK8sObjectListToResourceMap(items);
        } catch (ApiException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, KubernetesResourceDto> getDaemonSet(String namespace) {
        AppsV1Api appsV1Api = new AppsV1Api(client);
        namespace = Objects.nonNull(namespace) ? namespace : DEFAULT_NS;

        try {
            V1DaemonSetList v1DaemonSetList = appsV1Api.listNamespacedDaemonSet(namespace,
                    null, null, null, null,
                    null, null, null, null,
                    10, false);
            List<V1DaemonSet> items = v1DaemonSetList.getItems();

            return mapK8sObjectListToResourceMap(items);
        } catch (ApiException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
