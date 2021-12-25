package az.ingress.k8s.client.service.impl;

import az.ingress.k8s.client.dto.DeploymentDto;
import az.ingress.k8s.client.dto.RollingUpdateDto;
import az.ingress.k8s.client.dto.StrategyDto;
import az.ingress.k8s.client.service.KubernetesService;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1DeploymentStrategy;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1RollingUpdateDeployment;
import io.kubernetes.client.util.Config;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class KubernetesServiceImpl implements KubernetesService {

    private final ApiClient client;

    public KubernetesServiceImpl() {
        try {
            this.client = Config.defaultClient();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DeploymentDto> getDeployments(String namespace) {
        AppsV1Api appsV1Api = new AppsV1Api(client);
        try {
            V1DeploymentList v1DeploymentList = appsV1Api.listNamespacedDeployment(namespace,
                    null, null, null, null,
                    null, null, null, null,
                    10, false);
            List<V1Deployment> items = v1DeploymentList.getItems();
            return items.stream()
                    .map(this::mapDeploymentToDto)
                    .collect(Collectors.toList());
        } catch (ApiException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private DeploymentDto mapDeploymentToDto(V1Deployment k8sDeployment) {
        DeploymentDto dto = new DeploymentDto();

        if (Objects.nonNull(k8sDeployment.getMetadata())) {
            V1ObjectMeta metadata = k8sDeployment.getMetadata();
            dto.setName(metadata.getName());
            dto.setNamespace(metadata.getNamespace());
            dto.setLabels(metadata.getLabels());
        }

        if (Objects.nonNull(k8sDeployment.getSpec())) {
            V1DeploymentSpec spec = k8sDeployment.getSpec();
            dto.setReplicas(spec.getReplicas());
            dto.setLabelSelector(spec.getSelector().getMatchLabels());

            if (Objects.nonNull(spec.getStrategy())) {
                StrategyDto strategyDto = mapStrategyToDto(spec.getStrategy());
                dto.setStrategy(strategyDto);
            }
        }

        if (Objects.nonNull(k8sDeployment.getStatus()))
            dto.setReadyReplicas(k8sDeployment.getStatus().getReadyReplicas());

        return dto;
    }

    private StrategyDto mapStrategyToDto(V1DeploymentStrategy k8sStrategy) {
        StrategyDto strategyDto = new StrategyDto();
        strategyDto.setType(k8sStrategy.getType());
        if (Objects.nonNull(k8sStrategy.getRollingUpdate())) {
            RollingUpdateDto rollingUpdateDto = mapRollingUpdateToDto(k8sStrategy.getRollingUpdate());
            strategyDto.setRollingUpdate(rollingUpdateDto);
        }
        return strategyDto;
    }

    private RollingUpdateDto mapRollingUpdateToDto(V1RollingUpdateDeployment k8sRollingUpdate) {
        return RollingUpdateDto.builder()
                .maxSurge(getValueFromIntOrString(k8sRollingUpdate.getMaxSurge()))
                .maxUnavailable(getValueFromIntOrString(k8sRollingUpdate.getMaxUnavailable()))
                .build();
    }

    private String getValueFromIntOrString(IntOrString value) {
        if (Objects.nonNull(value)) {
            return value.toString();
        } else return null;
    }


}
