package az.ingress.k8s.client.service.graph;

import az.ingress.k8s.client.dto.KubernetesResourceDto;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public interface GraphService {

    Graph<KubernetesResourceDto, DefaultEdge> getClusterResourcesAsGraph(String namespace);

}
