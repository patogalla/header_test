import io.netty.handler.codec.http.HttpHeaders;
import org.asynchttpclient.Realm;
import org.asynchttpclient.filter.FilterContext;
import org.asynchttpclient.filter.FilterException;
import org.asynchttpclient.filter.RequestFilter;
import org.asynchttpclient.filter.ResponseFilter;
import org.asynchttpclient.proxy.ProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter the request, so the logic of which IP should be set for ProxyMesh, is implemented here.
 */
public class ProxyMeshClient implements RequestFilter, ResponseFilter {

    static Logger LOGGER = LoggerFactory.getLogger(ProxyMeshClient.class);
    public static final String X_PROXY_MESH_PREFER_IP = "X-ProxyMesh-Prefer-IP";
    public static final String X_PROXY_MESH_IP = "X-ProxyMesh-IP";
    public String proxyIp = null;

    public static final ProxyServer.Builder getProxyServer() {
        Realm.Builder realm = new Realm.Builder("patogalla",
                "p4tr1c10");
        realm.setScheme(Realm.AuthScheme.BASIC);
        ProxyServer.Builder proxyServer = new ProxyServer.Builder(
                "us-wa.proxymesh.com", 31280);
        proxyServer.setRealm(realm);
        return proxyServer;
    }

    @Override
    public <T> FilterContext<T> filter(FilterContext<T> ctx) throws FilterException {
        if (proxyIp != null) {
            LOGGER.info("Adding {} : {}", X_PROXY_MESH_PREFER_IP, proxyIp);
            ctx.getRequest().getHeaders().add(X_PROXY_MESH_PREFER_IP, proxyIp);
        }
        HttpHeaders responseHeaders = ctx.getResponseHeaders();
        if (responseHeaders != null && responseHeaders.get(X_PROXY_MESH_IP) != null) {
            proxyIp = responseHeaders.get(X_PROXY_MESH_IP);
            LOGGER.info("Reading {} : {}", X_PROXY_MESH_IP, proxyIp);
        }
        return ctx;
    }
}
