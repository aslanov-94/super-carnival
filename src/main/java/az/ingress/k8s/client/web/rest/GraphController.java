package az.ingress.k8s.client.web.rest;

import az.ingress.k8s.client.dto.KubernetesResourceDto;
import az.ingress.k8s.client.service.graph.GraphService;
import az.ingress.k8s.client.util.GraphJsonExportUtil;
import lombok.RequiredArgsConstructor;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/k8s-graph-resources")
public class GraphController {

    private final GraphService graphService;
    private final GraphJsonExportUtil jsonExportUtil;

    @GetMapping("/v1alpha1")
    public ResponseEntity<String> getClusterGraphResources(@RequestParam String namespace) {
        Graph<KubernetesResourceDto, DefaultEdge> graph = graphService.getClusterResourcesAsGraph(namespace);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(jsonExportUtil.exportK8sResourcesGraphToJson(graph));
    }

}
