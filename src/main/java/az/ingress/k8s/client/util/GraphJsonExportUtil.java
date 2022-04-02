package az.ingress.k8s.client.util;

import az.ingress.k8s.client.dto.KubernetesResourceDto;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.IntegerIdProvider;
import org.jgrapht.nio.json.JSONExporter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class GraphJsonExportUtil {

    private static final JSONExporter<KubernetesResourceDto, DefaultEdge> EXPORTER = new JSONExporter<>();

    public GraphJsonExportUtil() {
        Function<KubernetesResourceDto, Map<String, Attribute>> vertexAttributeProvider = v -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("name", DefaultAttribute.createAttribute(v.getName()));
            map.put("namespace", DefaultAttribute.createAttribute(v.getNamespace()));
            map.put("kind", DefaultAttribute.createAttribute(v.getKind()));
            map.put("age", DefaultAttribute.createAttribute(v.getAge()));
            map.put("status", DefaultAttribute.createAttribute(v.getStatus()));
            return map;
        };

//        exporter.setEdgeIdProvider(new IntegerIdProvider<>(1));
        EXPORTER.setVertexAttributeProvider(vertexAttributeProvider);
    }

    public String exportK8sResourcesGraphToJson(Graph<KubernetesResourceDto, DefaultEdge> graph) {
        EXPORTER.setVertexIdProvider(new IntegerIdProvider<>(1));
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            EXPORTER.exportGraph(graph, outputStream);
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
