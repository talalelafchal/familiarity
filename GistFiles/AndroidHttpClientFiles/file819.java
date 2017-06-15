package org.wso2.iot.integration.SimpleIOTTest;

import junit.framework.Assert;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.net.util.Base64;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.iot.integration.common.Constants;
import org.wso2.iot.integration.common.OAuthUtil;
import org.wso2.iot.integration.common.RestClient;
import org.wso2.iot.integration.common.TestBase;

public class SimpleIOTTest extends TestBase {

    private RestClient client;
    private final String APPLICATION_JSON = "application/json";
    private final String CONFIG_MGT_ENDPOINT = "/api/device-mgt/android/v1.0/configuration/";
    private final String LICENSE_ENDPOINT = "license";
    private final String SC_OK = "200";

    @Factory(dataProvider = "userModeProvider")
    public SimpleIOTTest(TestUserMode testUserMode) {
        this.userMode = testUserMode;
    }

    @BeforeClass(alwaysRun = true)
    public void initTest() throws Exception {
        super.init(userMode);
        String tenantDomain = automationContext.getContextTenant().getDomain();
        backendHTTPSURL = automationContext.getContextUrls().getWebAppURLHttps().replace("9443", String.valueOf(Constants
                .HTTPS_GATEWAY_PORT)).replace("/t/" + tenantDomain , "");
        User currentUser = getAutomationContext().getContextTenant().getContextUser();
        byte[] bytesEncoded = Base64
                .encodeBase64((currentUser.getUserName() + ":" + currentUser.getPassword()).getBytes());
        String encoded = new String(bytesEncoded);
        accessToken = OAuthUtil.getOAuthTokenPair(encoded, backendHTTPSURL, backendHTTPSURL, currentUser.getUserName(),
                currentUser.getPassword());
        accessTokenString = "Bearer " + accessToken;

        this.client = new RestClient(backendHTTPSURL, APPLICATION_JSON, accessTokenString);
    }

    @Test(description = "Test get android license.")
    public void testGetLicense() throws Exception {
        HttpResponse response = client.get(CONFIG_MGT_ENDPOINT + LICENSE_ENDPOINT);
        Assert.assertEquals(SC_OK, response.getResponseCode());

    }

}
