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
public class ProxyMeshRequestFilter implements RequestFilter {

    static Logger LOGGER = LoggerFactory.getLogger(ProxyMeshRequestFilter.class);

    @Override
    public <T> FilterContext<T> filter(FilterContext<T> ctx) throws FilterException {
        LOGGER.info("EXEC Request Filter");
        //Request
        if (ProxyMeshClient.proxyIp != null) {
            MDC.put("proxyIp", ProxyMeshClient.proxyIp);
            LOGGER.info("Adding {} : {}", ProxyMeshClient.X_PROXY_MESH_PREFER_IP, ProxyMeshClient.proxyIp);
            ctx.getRequest().getHeaders().add(ProxyMeshClient.X_PROXY_MESH_PREFER_IP, ProxyMeshClient.proxyIp);
        }

        if (!ProxyMeshClient.proxyIpBlocked.isEmpty()) {
            String blockedString = ProxyMeshClient.proxyIpBlocked.stream().collect(Collectors.joining(","));
            LOGGER.info("Adding {} : {}", ProxyMeshClient.X_PROXY_MESH_NOT_IP, blockedString);
            ctx.getRequest().getHeaders().add(ProxyMeshClient.X_PROXY_MESH_NOT_IP, blockedString);
        }
        return ctx;
    }
}
