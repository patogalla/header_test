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

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filter the request, so the logic of which IP should be set for ProxyMesh, is implemented here.
 */
public class ProxyMeshResponseFilter implements ResponseFilter {

    static Logger LOGGER = LoggerFactory.getLogger(ProxyMeshResponseFilter.class);

    @Override
    public <T> FilterContext<T> filter(FilterContext<T> ctx) throws FilterException {
        LOGGER.info("EXEC Response Filter");

        HttpHeaders responseHeaders = ctx.getResponseHeaders();
        String newProxyIp = responseHeaders.get(ProxyMeshClient.X_PROXY_MESH_IP);
        if (newProxyIp != null) {
            ProxyMeshClient.proxyIp = newProxyIp;
            MDC.put("proxyIp", ProxyMeshClient.proxyIp);
            LOGGER.info("new {} : {}", ProxyMeshClient.X_PROXY_MESH_IP, ProxyMeshClient.proxyIp);
        }

        if (ctx.getResponseStatus() != null && ctx.getResponseStatus().getStatusCode() == 200) {
            //Response SUCCESS
            LOGGER.info("Status : {}", ctx.getResponseStatus().getStatusCode());
        } else if (ctx.getResponseStatus() != null && ctx.getResponseStatus().getStatusCode() != 200 && ctx.getResponseStatus().getStatusCode() != 407) {
            //Response ERROR
            ProxyMeshClient.proxyIp = null;
            for (Map.Entry<String, String> entry : responseHeaders.entries()) {
                LOGGER.info("Header [{} , {}] :", entry.getKey(), entry.getValue());
            }
            String blockedIp = responseHeaders.get(ProxyMeshClient.X_PROXY_MESH_IP) == null ? MDC.get("proxyIp") : responseHeaders.get(ProxyMeshClient.X_PROXY_MESH_IP);
            if (blockedIp != null) {
                ProxyMeshClient.proxyIpBlocked.add(blockedIp);
                MDC.remove("proxyIp");
                LOGGER.info("Blocking IP {} : {}", ProxyMeshClient.X_PROXY_MESH_IP, blockedIp);
            }
            LOGGER.info("Error Status : {}", ctx.getResponseStatus().getStatusCode());
        } else if (ctx.getResponseStatus() != null && ctx.getResponseStatus().getStatusCode() == 407) {
            LOGGER.debug("Error Status : {}", ctx.getResponseStatus().getStatusCode());
        }
        return ctx;
    }
}
