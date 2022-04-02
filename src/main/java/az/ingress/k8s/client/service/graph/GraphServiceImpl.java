package az.ingress.k8s.client.service.graph;

import az.ingress.k8s.client.dto.KubernetesResourceDto;
import az.ingress.k8s.client.enums.ResourceKind;
import az.ingress.k8s.client.service.kubernetes.AppApiService;
import az.ingress.k8s.client.service.kubernetes.CoreApiService;
import lombok.RequiredArgsConstructor;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GraphServiceImpl implements GraphService {

    private final CoreApiService coreApiService;
    private final AppApiService appApiService;

    @Override
    public Graph<KubernetesResourceDto, DefaultEdge> getClusterResourcesAsGraph(String namespace) {
        Graph<KubernetesResourceDto, DefaultEdge> graph = initializeGraph();

        EnumMap<ResourceKind, Map<String, KubernetesResourceDto>> clusterResources = getClusterResources(namespace);

        addClusterResourcesToGraph(graph, clusterResources);

        addRelationsBetweenResources(graph, clusterResources);
        return graph;
    }

    private Graph<KubernetesResourceDto, DefaultEdge> initializeGraph() {
        return GraphTypeBuilder
                .<KubernetesResourceDto, DefaultEdge>
                        directed()
                .allowingMultipleEdges(true)
                .allowingSelfLoops(true)
                .edgeClass(DefaultEdge.class)
                .weighted(false)
                .buildGraph();
    }

    private EnumMap<ResourceKind, Map<String, KubernetesResourceDto>> getClusterResources(String namespace) {
        EnumMap<ResourceKind, Map<String, KubernetesResourceDto>> clusterResources = new EnumMap<>(ResourceKind.class);


        clusterResources.put(ResourceKind.POD, coreApiService.getPods(namespace));
        clusterResources.put(ResourceKind.DEPLOYMENT, appApiService.getDeployments(namespace));
        clusterResources.put(ResourceKind.STATEFUL_SET, appApiService.getStatefulSet(namespace));
        clusterResources.put(ResourceKind.REPLICA_SET, appApiService.getReplicaSet(namespace));
        clusterResources.put(ResourceKind.DAEMON_SET, appApiService.getDaemonSet(namespace));
        return clusterResources;
    }

    private void addClusterResourcesToGraph(Graph<KubernetesResourceDto, DefaultEdge> graph,
                                            EnumMap<ResourceKind, Map<String, KubernetesResourceDto>> clusterResources) {
        clusterResources.values()
                .forEach(stringKubernetesResourceDtoMap -> stringKubernetesResourceDtoMap.values()
                        .forEach(graph::addVertex));
    }

    private void addRelationsBetweenResources(Graph<KubernetesResourceDto, DefaultEdge> graph,
                                              EnumMap<ResourceKind, Map<String, KubernetesResourceDto>> clusterResources) {
        clusterResources.values()
                .forEach(stringKubernetesResourceDtoMap -> stringKubernetesResourceDtoMap.values()
                        .forEach(kubernetesResourceDto -> kubernetesResourceDto.getResourceOwners()
                                .forEach(k8sResourceRelationDto -> {
                                    Map<String, KubernetesResourceDto> resources =
                                            clusterResources.get(k8sResourceRelationDto.getKind());
                                    if (Objects.isNull(resources)) {
                                        //todo add warning not found mapping
                                    } else {
                                        KubernetesResourceDto ownerResource =
                                                resources.get(k8sResourceRelationDto.getName());
                                        if (Objects.isNull(ownerResource)) {
                                            //todo add warning not found owner resource
                                        } else {
                                            graph.addEdge(ownerResource, kubernetesResourceDto);
                                        }
                                    }
                                })));
    }

}
