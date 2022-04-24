package az.ingress.k8s.client.service.kubernetes.impl;

import az.ingress.k8s.client.dto.K8sResourceRelationDto;
import az.ingress.k8s.client.dto.KubernetesResourceDto;
import az.ingress.k8s.client.enums.ResourceKind;
import az.ingress.k8s.client.service.kubernetes.ConnectionComponent;
import az.ingress.k8s.client.service.kubernetes.PodService;
import az.ingress.k8s.client.util.ObjectsUtil;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1EnvFromSource;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1EnvVarSource;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PodServiceImpl extends ConnectionComponent implements PodService {

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

            return pods.stream()
                    .map(this::mapToKubernetesResource)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors
                            .toMap(KubernetesResourceDto::getName,
                                    kubernetesResourceDto -> kubernetesResourceDto));

        } catch (ApiException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Optional<KubernetesResourceDto> mapToKubernetesResource(V1Pod pod) {
        return mapKubernetesObjectToResourceDto(pod, ResourceKind.POD)
                .map(resourceDto -> {
                    resourceDto.setUsedResources(getPodUsedResources(pod));
                    return resourceDto;
                });
    }

    private List<K8sResourceRelationDto> getPodUsedResources(V1Pod pod) {
        Stream<K8sResourceRelationDto> envResources = Stream.empty();
        Stream<K8sResourceRelationDto> volumeResources = Stream.empty();
        if (ObjectsUtil.nonNull(() -> pod.getSpec().getVolumes()))
            volumeResources = getVolumeResources(pod);
        if (ObjectsUtil.nonNull(() -> pod.getSpec().getContainers()))
            envResources = getEnvironmentResources(pod);
        return Stream.concat(envResources, volumeResources)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Stream<K8sResourceRelationDto> getVolumeResources(V1Pod pod) {
        return pod.getSpec().getVolumes()
                .stream()
                .map(volume -> {
                    if (ObjectsUtil.nonNull(() -> volume.getConfigMap().getName()))
                        return new K8sResourceRelationDto(ResourceKind.CONFIG_MAP, volume.getConfigMap().getName());
                    if (ObjectsUtil.nonNull(() -> volume.getSecret().getSecretName()))
                        return new K8sResourceRelationDto(ResourceKind.SECRET, volume.getSecret().getSecretName());
                    if (ObjectsUtil.nonNull(() -> volume.getPersistentVolumeClaim().getClaimName()))
                        return new K8sResourceRelationDto(ResourceKind.PERSISTENT_VOLUME_CLAIM,
                                volume.getPersistentVolumeClaim().getClaimName());
                    return null;
                });
    }

    private Stream<K8sResourceRelationDto> getEnvironmentResources(V1Pod pod) {
        return pod.getSpec().getContainers()
                .stream()
                .flatMap(container -> {
                    Stream<K8sResourceRelationDto> envFrom = getEnvironmentResourceFromEnvFrom(container);
                    Stream<K8sResourceRelationDto> env = getEnvironmentResourceFromEnv(container);

                    return Stream.concat(env, envFrom);
                });
    }

    private Stream<K8sResourceRelationDto> getEnvironmentResourceFromEnvFrom(V1Container container) {
        Stream<K8sResourceRelationDto> envFrom;
        if (Objects.nonNull(container.getEnvFrom()))
            envFrom = container.getEnvFrom()
                    .stream()
                    .map(this::mapEnvFromToRelationResource);
        else
            envFrom = Stream.empty();
        return envFrom;
    }

    public K8sResourceRelationDto mapEnvFromToRelationResource(V1EnvFromSource envVarSource) {
        if (ObjectsUtil.nonNull(() -> envVarSource.getConfigMapRef().getName()))
            return new K8sResourceRelationDto(ResourceKind.CONFIG_MAP, envVarSource.getConfigMapRef().getName());
        if (ObjectsUtil.nonNull(() -> envVarSource.getSecretRef().getName()))
            return new K8sResourceRelationDto(ResourceKind.SECRET, envVarSource.getSecretRef().getName());
        return null;
    }

    private Stream<K8sResourceRelationDto> getEnvironmentResourceFromEnv(V1Container container) {
        Stream<K8sResourceRelationDto> env;
        if (Objects.nonNull(container.getEnv()))
            env = container.getEnv()
                    .stream()
                    .map(V1EnvVar::getValueFrom)
                    .map(this::mapEnvToRelationResource);
        else
            env = Stream.empty();
        return env;
    }

    public K8sResourceRelationDto mapEnvToRelationResource(V1EnvVarSource envVarSource) {
        if (ObjectsUtil.nonNull(() -> envVarSource.getConfigMapKeyRef().getName()))
            return new K8sResourceRelationDto(ResourceKind.CONFIG_MAP, envVarSource.getConfigMapKeyRef().getName());
        if (ObjectsUtil.nonNull(() -> envVarSource.getSecretKeyRef().getName()))
            return new K8sResourceRelationDto(ResourceKind.SECRET, envVarSource.getSecretKeyRef().getName());
        return null;
    }

}
