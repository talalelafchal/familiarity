/**
 * Created with IntelliJ IDEA.
 * User: lunanueva
 * Date: 13/01/13
 * Time: 23:09
 * To change this template use File | Settings | File Templates.
 */
public class HttpClientDataLoader {


    private void getResponse() {
      /*
        //Adapted from the Basic Auth example in the Apache Commonds HTTPClient examples with mfantcook's addRequestHeader thrown in the mix
        client = new Packages.org.apache.commons.httpclient.HttpClient;
        usrpsw = new Packages.org.apache.commons.httpclient.UsernamePasswordCredentials("myReallyLongSuperSecureApiTokenThingy", "myReallyLongSuperSecureApiTokenThingy");
        authsc = new Packages.org.apache.commons.httpclient.auth.AuthScope("mysubdomain.mysaasapp.com", 80, "My Realm - Use CURL to discover");
// create a new GET method using basic authentication from above.
        method = new Packages.org.apache.commons.httpclient.methods.GetMethod("http://mysubdomain.mysaasapp.com/myresource.xml");

// pass our credentials to HttpClient
        client.getState().setCredentials(authsc,usrpsw);

// Tell the GET method to automatically handle authentication.
        method.setDoAuthentication( true );

// Add an "If-None-Match" header to pass an ETag for change checking
// This can MASSIVELY reduce wire transfer if the host supports it
// Note the embedded double quotes in the etag string, this is essential.
        method.addRequestHeader("If-None-Match",'"4e6993f454dc19b5246cceb38376b546"');

// Add an "If-Modified-Since" header to pass an ETag for change checking
// This can also reduce wire transfer but carries the added burden of time zones, etc.
        method.addRequestHeader("If-Modified-Since","Thu, 26 Aug 2010 08:23:02 GMT");

// Add "Accept" and "Content-Type" headers, may be necessary for some APIs.
        method.addRequestHeader("Accept","application/xml");
        method.addRequestHeader("Content-Type","application/xml");

        var status = client.executeMethod( method );
        var message = method.getStatusText();
        var response = method.getResponseBodyAsString();
        var request = method.getRequestHeaders().toSource()
//var query = method.getQueryString();
//var path = method.getPath();

        method.releaseConnection();

        */
    }

}




/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
/*
package org.apache.http.examples.client;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * This example demonstrates the use of the {@link ResponseHandler} to simplify
 * the process of processing the HTTP response and releasing associated resources.
 */
/*
public class ClientWithResponseHandler {

    public final static void main(String[] args) throws Exception {

        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet("http://www.google.com/");

            System.out.println("executing request " + httpget.getURI());

            // Create a response handler
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            System.out.println("----------------------------------------");

        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }

}

client.getParams().setIntParameter("http.connection.timeout", 1);

  HttpUriRequest request = new HttpGet("http://192.168.20.43");
  HttpResponse response = client.execute(request);

  // set the connection timeout value to 30 seconds (30000 milliseconds)
    final HttpParams httpParams = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
    client = new DefaultHttpClient(httpParams);

*/


/*

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
String logging = "org.apache.commons.logging";
// Configure Logging
System.setProperty(logging + ".Log", logging + ".impl.SimpleLog");
System.setProperty(logging + ".logging.simplelog.showdatetime", "true");
System.setProperty(logging + ".simplelog.log.httpclient.wire", "debug");
System.setProperty(logging + ".simplelog.log.org.apache.commons.httpclient",
                   "debug");

HttpClient client = new HttpClient( );
String url = "http://www.discursive.com/jccook/";
HttpMethod method = new GetMethod( url );
client.executeMethod( method );
String response = method.getResponseBodyAsString( );
System.out.println( response );
method.releaseConnection( );
method.recycle( );

*/