	public static String requestGET(String url) { 
	
		String resultData = null;
		
		try {
			//재시도 핸들러 (선택)
			DefaultHttpClient httpclient = new DefaultHttpClient();

			HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {

			        public boolean retryRequest(
			                IOException exception, 
			                int executionCount,
			                HttpContext context) {
				        	
			            if (executionCount > 3) {
			                // Do not retry if over max retry count
			                return false;
			            }
			            if (exception instanceof InterruptedIOException) {
			                // Timeout
			                return false;
			            }
			            if (exception instanceof UnknownHostException) {
			                // Unknown host
			                return false;
			            }
			            if (exception instanceof ConnectException) {
			                // Connection refused
			                return false;
			            }
			            if (exception instanceof SSLException) {
			                // SSL handshake exception
			                return false;
			            }
			            HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
				    boolean idempotent = !(request instanceof HttpEntityEnclosingRequest); 
				    if (idempotent) {
				    	// Retry if the request is considered idempotent 
				        return true;
				    }
				    return false;
				}

			};
			httpclient.setHttpRequestRetryHandler(myRetryHandler);


			HttpGet httpGet = new HttpGet(url.toString());
			try {
				HttpResponse response = httpclient.execute(httpGet);
				        
				HttpEntity resEntityGet = response.getEntity();  
				if (resEntityGet != null) {  
				    	resultData = EntityUtils.toString(resEntityGet);
				}
				        
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
			   	e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultData;
	} 