package org.glygen.glycan.verification.util;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GlyGenApiUtil
{
    private BasicCookieStore m_cookieStore = null;
    private CloseableHttpClient m_httpclient = null;

    public GlyGenApiUtil() throws IOException
    {
        this.connect();
    }

    private void connect() throws IOException
    {
        try
        {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial((chain, authType) -> true).build();

            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
                    new String[] { "SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2" }, null,
                    NoopHostnameVerifier.INSTANCE);
            // configure timeouts
            int timeout = 10;
            RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * 1000)
                    .setConnectionRequestTimeout(timeout * 1000).setSocketTimeout(timeout * 1000).build();
            // create cookie store and HTTP client
            this.m_cookieStore = new BasicCookieStore();
            this.m_httpclient = HttpClients.custom().setDefaultCookieStore(this.m_cookieStore)
                    .setSSLSocketFactory(sslConnectionSocketFactory).setDefaultRequestConfig(config).build();
        }
        catch (Exception e)
        {
            throw new IOException("Unable to build web client: " + e.getMessage(), e);
        }
    }

    public String getGlycoCT(String a_id) throws ClientProtocolException, IOException, ParseException
    {
        HttpGet httpget = new HttpGet("https://api.glygen.org//glycan/detail/" + a_id);
        CloseableHttpResponse t_response = this.m_httpclient.execute(httpget);
        HttpEntity t_entity = t_response.getEntity();
        if (t_response.getStatusLine().getStatusCode() >= 400)
        {
            Integer t_code = t_response.getStatusLine().getStatusCode();
            EntityUtils.consume(t_entity);
            t_response.close();
            this.m_httpclient.close();
            this.connect();
            throw new IOException("API request returns HTTP code: " + Integer.toString(t_code));
        }
        StringWriter t_writer = new StringWriter();
        IOUtils.copy(t_entity.getContent(), t_writer, StandardCharsets.UTF_8);
        String t_string = t_writer.toString();
        EntityUtils.consume(t_entity);
        t_response.close();
        return this.extractGlycoCT(t_string);
    }

    private String extractGlycoCT(String a_json) throws ParseException, IOException
    {
        JSONParser t_parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) t_parser.parse(a_json);
        String t_glycoCT = this.getString(jsonObject, "glycoct");
        if (t_glycoCT == null)
        {
            throw new IOException("Missing GlycoCT in GlyGen Response.");
        }
        t_glycoCT = t_glycoCT.replaceAll(" ", "\n");
        return t_glycoCT;
    }

    private String getString(JSONObject a_jsonAudiobook, String a_key) throws IOException
    {
        Object t_stringObject = a_jsonAudiobook.get(a_key);
        if (t_stringObject == null)
        {
            return null;
        }
        if (t_stringObject instanceof String)
        {
            return (String) t_stringObject;
        }
        else
        {
            throw new IOException("JSON format error: Value for " + a_key + " is not a string.");
        }
    }
}
