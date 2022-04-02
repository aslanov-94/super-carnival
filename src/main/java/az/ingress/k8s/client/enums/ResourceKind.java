package az.ingress.k8s.client.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;


@Getter
@RequiredArgsConstructor
public enum ResourceKind {

    DEPLOYMENT("Deployment"),
    REPLICA_SET("ReplicaSet"),
    STATEFUL_SET("StatefulSet"),
    DAEMON_SET("DaemonSet"),
    POD("Pod"),
    NOT_IMPLEMENTED("NAN");

    private static final List<ResourceKind> RESOURCE_KINDS = List.of(ResourceKind.values());
    private final String kind;

    public static ResourceKind findResourceKind(String kubernetesKind) {
        return RESOURCE_KINDS
                .stream()
                .filter(resourceKind -> resourceKind.kind.equalsIgnoreCase(kubernetesKind))
                .findFirst()
                .orElseGet(() -> NOT_IMPLEMENTED);
    }

}
