package com.serotonin.m2m2.http.dwr;

import com.serotonin.db.pair.StringStringPair;
import com.serotonin.m2m2.Common;
import com.serotonin.m2m2.web.dwr.beans.TestingUtility;
import com.serotonin.web.http.HttpUtils4;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

public class HttpSenderTester extends Thread
  implements TestingUtility
{
  private static final Log LOG = LogFactory.getLog(HttpSenderTester.class);
  private final String url;
  private final boolean usePost;
  private final List<StringStringPair> staticHeaders;
  private final List<StringStringPair> staticParameters;
  private String result;

  public HttpSenderTester(String url, boolean usePost, List<StringStringPair> staticHeaders, List<StringStringPair> staticParameters)
  {
    this.url = url;
    this.usePost = usePost;
    this.staticHeaders = staticHeaders;
    this.staticParameters = staticParameters;
    start();
  }

  public void run()
  {
    List<NameValuePair> nvps = convertToNVPs(this.staticParameters);
    HttpRequestBase request;
    if (this.usePost) {
      HttpPost post = new HttpPost(this.url);
      try {
        post.setEntity(new UrlEncodedFormEntity(nvps));
      }
      catch (UnsupportedEncodingException e) {
        LOG.warn("", e);
        this.result = ("ERROR: " + e.getMessage());
        return;
      }
      request = post;
    }
    else {
      try {
        URIBuilder urib = new URIBuilder(this.url);
        for (NameValuePair nvp : nvps)
          urib.addParameter(nvp.getName(), nvp.getValue());
        request = new HttpGet(urib.build());
      }
      catch (URISyntaxException e) {
        LOG.warn("", e);
        this.result = ("ERROR: " + e.getMessage());
        return;
      }

    }

    request.addHeader("User-Agent", "Mango M2M2 HTTP Sender publisher");

    for (StringStringPair kvp : this.staticHeaders)
      request.addHeader(kvp.getKey(), kvp.getValue());
    try
    {
      HttpResponse response = Common.getHttpClient().execute(request);
      if (response.getStatusLine().getStatusCode() != 200)
        this.result = ("ERROR: Invalid response code: " + response.getStatusLine().getStatusCode());
      else
        this.result = HttpUtils4.readResponseBody(response, 1024);
    }
    catch (Exception e) {
      this.result = ("ERROR: " + e.getMessage());
    }
    finally {
      request.reset();
    }
  }

  public String getResult() {
    return this.result;
  }

  private List<NameValuePair> convertToNVPs(List<StringStringPair> staticParameters) {
    List nvps = new ArrayList(staticParameters.size());
    for (StringStringPair ssp : staticParameters)
      nvps.add(new BasicNameValuePair(ssp.getKey(), ssp.getValue()));
    return nvps;
  }

  public void cancel()
  {
  }
}