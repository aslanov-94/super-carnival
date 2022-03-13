package az.ingress.k8s.client.service.kubernetes;

import az.ingress.k8s.client.dto.K8sResourceRelationDto;
import az.ingress.k8s.client.dto.KubernetesResourceDto;
import az.ingress.k8s.client.enums.ResourceKind;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1OwnerReference;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public interface K8sResourceMapper {

    default Map<String, KubernetesResourceDto> mapK8sObjectListToResourceMap(List<? extends KubernetesObject> items) {
        return items.stream()
                .map(this::mapKubernetesObjectToResourceDto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors
                        .toMap(KubernetesResourceDto::getName,
                                kubernetesResourceDto -> kubernetesResourceDto));
    }

    private Optional<KubernetesResourceDto> mapKubernetesObjectToResourceDto(KubernetesObject kubernetesObject) {
        if (Objects.isNull(kubernetesObject.getMetadata())) {
            return Optional.empty();
        }

        V1ObjectMeta metadata = kubernetesObject.getMetadata();
        return Optional.of(
                KubernetesResourceDto.builder()
                        .namespace(metadata.getNamespace())
                        .name(metadata.getName())
                        .labels(metadata.getLabels())
                        .kind(kubernetesObject.getKind())
                        .resourceOwners(mapOwnerReferenceToDto(metadata.getOwnerReferences()))
                        .build()
        );
    }

    private List<K8sResourceRelationDto> mapOwnerReferenceToDto(List<V1OwnerReference> ownerReferences) {
        if (Objects.isNull(ownerReferences)) {
            return List.of();
        }
        return ownerReferences.stream()
                .filter(Objects::nonNull)
                .map(reference -> K8sResourceRelationDto.builder()
                        .kind(ResourceKind.findResourceKind(reference.getKind()))
                        .name(reference.getName())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
