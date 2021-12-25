package az.ingress.k8s.client.service;

import az.ingress.k8s.client.dto.DeploymentDto;

import java.util.List;

public interface KubernetesService {

    List<DeploymentDto> getDeployments(String namespace);

}
