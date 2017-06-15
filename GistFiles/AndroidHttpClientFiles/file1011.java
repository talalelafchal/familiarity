package co.id.veritrans.payment.android;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.junit.Test;

import java.io.*;

/**
 * @author Eko Khannedy
 */
public class DefaultVeritransPaymentClientTest {

    @Test
    public void testToken() throws Exception {

        String cardNumber = "4111111111111111";
        String cardExpMonth = "10";
        String cardExpYear = "2017";
        String cardCvv = "123";
        String clientKey = "42f28ba6-8742-4957-1234-dcaafc6d041f";

        StringBuilder builder = new StringBuilder();
        builder.append("https://payments.veritrans.co.id/vtdirect/v1/tokens?");
        builder.append("card_number=").append(cardNumber).append("&");
        builder.append("card_exp_month=").append(cardExpMonth).append("&");
        builder.append("card_exp_year=").append(cardExpYear).append("&");
        builder.append("card_cvv=").append(cardCvv).append("&");
        builder.append("client_key=").append(clientKey);

        HttpGet get = new HttpGet(builder.toString());

        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(get);

        String bodyContent = readInputStream(response.getEntity().getContent());

        JSONObject json = new JSONObject(bodyContent);

        String code = json.getString("code");
        String status = json.getString("status");
        String message = json.getString("message");
        String token = null;

        if (code.equals("VD01")) {
            token = json.getString("token_id");
        }
    }

    public static String readInputStream(InputStream inputStream) throws IOException {

        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try {
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder total = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                total.append(line);
            }

            return total.toString();
        } catch (IOException t) {
            throw t;
        } finally {
            close(inputStreamReader);
            close(bufferedReader);
            close(inputStream);
        }
    }

    public static void close(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
