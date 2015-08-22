package org.komine.watson.nlc.sample.rest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class RestClient {

    private static Log log = LogFactory.getLog(RestClient.class);

    public static String makeRestRequest(String method, String url, String username, String password,
            final Map<String, String> urlParams, final HttpEntity httpEntity)
                    throws URISyntaxException, ClientProtocolException, IOException {
        HttpRequestBase httpRequest = buildHttpMethodForAPI(method, url, username, password, urlParams, httpEntity);

        log.info(String.format("Making a REST request. method=%s, uri=%s", httpRequest.getMethod(),
                httpRequest.getURI().toASCIIString()));

        if (log.isDebugEnabled()) {
            debugHttpRequest(httpRequest);
        }

        // create client with credential
        URI uri = new URI(url);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),
                new UsernamePasswordCredentials(username, password));
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

        // execute
        CloseableHttpResponse response = httpclient.execute(httpRequest);
        StatusLine statusLine = response.getStatusLine();
        log.info(String.format("Response received. StatusCode=%s, ReasonPhrase=%s", statusLine.getStatusCode(),
                statusLine.getReasonPhrase()));

        String result = null;

        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                long len = entity.getContentLength();
                if (len != -1 && len < 2048) {
                    result = EntityUtils.toString(entity, Consts.UTF_8);
                } else {
                    BufferedHttpEntity bufEntity = new BufferedHttpEntity(entity);
                    InputStream input = bufEntity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input, Consts.UTF_8.name()));
                    String line = null;
                    StringBuffer buf = new StringBuffer();
                    while ((line = reader.readLine()) != null) {
                        buf.append(line);
                    }
                    result = buf.toString();
                }
            }
        } finally {
            response.close();
        }
        log.info(String.format("Response: %s", result));
        return result;
    }

    private static HttpRequestBase buildHttpMethodForAPI(final String method, final String url, final String username,
            final String password, final Map<String, String> params, final HttpEntity httpEntity)
                    throws MalformedURLException, URISyntaxException, UnsupportedEncodingException {

        HttpRequestBase httprequest;
        if (HttpPost.METHOD_NAME.equals(method)) {
            HttpPost httppost;
            if (null != httpEntity) {
                httppost = new HttpPost(url);
                httppost.setEntity(httpEntity);
            } else if (null != params) {
                ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
                Iterator<String> it = params.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    postParams.add(new BasicNameValuePair(key, params.get(key)));
                }
                httppost = new HttpPost(url);
                httppost.setEntity(new UrlEncodedFormEntity(postParams, Consts.UTF_8));
            } else {
                httppost = new HttpPost(url);
            }
            httprequest = httppost;
        } else if (HttpGet.METHOD_NAME.equals(method)) {
            httprequest = new HttpGet(url);
        } else if (HttpDelete.METHOD_NAME.equals(method)) {
            httprequest = new HttpDelete(url);
        } else {
            throw new IllegalArgumentException("Unexpected method is specified: " + method + ".");
        }

        return httprequest;
    }

    private static void debugHttpRequest(HttpRequestBase httpRequest) {
        log.debug("HTTP request info:");
        if (httpRequest instanceof HttpGet) {
            log.debug("Method=GET");
            log.debug("URL=" + httpRequest.getURI());
        } else if (httpRequest instanceof HttpPost) {
            log.debug("Method=POST");

            HttpPost httpPost = (HttpPost) httpRequest;
            HttpEntity entity = httpPost.getEntity();
            if (null != entity) {
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream((int) entity.getContentLength());
                    entity.writeTo(out);
                    String entityContent = new String(out.toByteArray());
                    log.debug(String.format("Entity:%n%s", entityContent));
                } catch (IOException e) {
                    log.fatal(e);
                }
            } else {
                log.debug("Entity is empty.");
            }
        }
    }
}
