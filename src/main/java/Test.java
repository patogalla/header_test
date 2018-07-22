import com.google.common.collect.Sets;
import org.asynchttpclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Future;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class Test
{

    static Logger LOGGER = LoggerFactory.getLogger(Test.class);
    private AsyncHttpClientConfig clientConfig = null;

    public static void main(String args[]) throws Exception
    {
        Test test = new Test();
        test.testing();
    }

    public void testing() throws Exception
    {

        initConfig();
        Set<Future<Integer>> responses = Sets.newConcurrentHashSet();
        for (String term : getTerms())
        {
            responses.add(makeGetRequest(term));
            //Thread.sleep(1000);
        }

        for (Future<Integer> response: responses) {
            response.get();
        }
        LOGGER.info("Finish.");
    }

    public void initConfig()
    {

        // TODO : make provision to set custom headers for https requests before sending
        clientConfig = new DefaultAsyncHttpClientConfig.Builder()
                .setProxyServer(ProxyMeshClient.getProxyServer())
                .addRequestFilter(new ProxyMeshRequestFilter())
                .addResponseFilter(new ProxyMeshResponseFilter())
                .build();

    }

    public Set<String> getTerms()
    {
        Set<String> set = new HashSet<String>();
        set.add("pato");
        set.add("cabra2");
        set.add("cabra3");
        set.add("cabra4");
        set.add("cabra5");
        set.add("cabra12");
        set.add("cabra13");
        set.add("cabra14");
        set.add("cabra15");
        set.add("cabra22");
        set.add("cabra23");
        set.add("cabra24");
        set.add("cabra25");
        set.add("cabra32");
        set.add("cabra33");
        set.add("cabra34");
        set.add("cabra35");
        set.add("dog");
        return set;
    }

    public Future<Integer> makeGetRequest(String term)
    {
        LOGGER.info("Making Request : {}", term);
        List<Param> paramList = new ArrayList<>(1);
        paramList.add(new Param("q", term));

        String url = "https://www.google.com/search";

        AsyncHttpClient asyncHttpClient = asyncHttpClient(clientConfig);
        return asyncHttpClient.prepareGet(url)
                .setQueryParams(paramList).execute(new GetRequestAsyncCompletionHandler(term));

    }
}
// INFO: Regarding what headers we would be inserting and reading, here is the link : https://docs.proxymesh.com/article/42-java-proxy-configuration-examples