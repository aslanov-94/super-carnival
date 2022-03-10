package az.ingress.k8s.client.service.kubernetes;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.Config;

import java.io.IOException;

public abstract class ConnectionComponent {

    protected final ApiClient client;
    protected final String DEFAULT_NS = "default";


    protected ConnectionComponent() {
        try {
            this.client = Config.defaultClient();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
