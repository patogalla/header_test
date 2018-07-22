import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetRequestAsyncCompletionHandler extends AsyncCompletionHandler<Integer>
{
    static Logger LOGGER = LoggerFactory.getLogger(GetRequestAsyncCompletionHandler.class);
    private String term;

    public GetRequestAsyncCompletionHandler(String term)
    {
        this.term = term;
    }

    @Override
    public Integer onCompleted(Response response)
    {
        // TODO: make provision to read custom headers here.
        System.out.println("Request for term = " + term + " received.");
        System.out.println("Status Code : " + response.getStatusCode());
        System.out.println("X-ProxyMesh-IP : " + response.getHeader("X-ProxyMesh-IP"));
        doSomeProcessing(response.getResponseBody());
        return 200;
    }

    @Override
    public void onThrowable(Throwable t)
    {
        t.printStackTrace();
    }

    private void doSomeProcessing(String s)
    {
        LOGGER.info("Going to process text here.");
        // We are doing some processing here.
    }

}
