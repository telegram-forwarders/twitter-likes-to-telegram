package twitter.telegram.forwarder;

import org.apache.http.client.methods.HttpGet;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStream;

public class HttpRequest {

    public String getUrl(String uri) throws Exception {
        HttpGet req = new HttpGet(uri);
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(req)) {
            InputStream inputStream = response.getEntity().getContent();
            return IOUtils.toString(inputStream, "utf-8");
        }
    }
}
