import com.google.common.collect.Sets;
import io.netty.handler.codec.http.HttpHeaders;
import org.asynchttpclient.Realm;
import org.asynchttpclient.filter.FilterContext;
import org.asynchttpclient.filter.FilterException;
import org.asynchttpclient.filter.RequestFilter;
import org.asynchttpclient.filter.ResponseFilter;
import org.asynchttpclient.proxy.ProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filter the request, so the logic of which IP should be set for ProxyMesh, is implemented here.
 */
public class ProxyMeshClient {

    public static final String X_PROXY_MESH_NOT_IP = "X-ProxyMesh-Not-IP";
    static Logger LOGGER = LoggerFactory.getLogger(ProxyMeshClient.class);
    public static final String X_PROXY_MESH_PREFER_IP = "X-ProxyMesh-Prefer-IP";
    public static final String X_PROXY_MESH_IP = "X-ProxyMesh-IP";

    public static String proxyIp = null;
    public static Set<String> proxyIpBlocked = Sets.newConcurrentHashSet();

    public static final ProxyServer.Builder getProxyServer() {
        Realm.Builder realm = new Realm.Builder("patogalla",
                "p4tr1c10");
        realm.setScheme(Realm.AuthScheme.BASIC);
        ProxyServer.Builder proxyServer = new ProxyServer.Builder(
                "us-wa.proxymesh.com", 31280);
        proxyServer.setRealm(realm);
        return proxyServer;
    }
}
