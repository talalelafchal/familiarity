/*
 * Copyright(c) 2012 Android Test and Evaluation Club.
 *
 */

package org.android_tec.atmarkit.usemockexample.models;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.test.AndroidTestCase;

/**
 * FxRateLoaderのテストを行なう.
 * <p>
 * - requestFeed()のレスポンス200、200以外、通信不可の例外<br>
 * - parseFeed()の正常系、異常系？<br>
 * -
 * 
 * @author Koji Hasegawa
 * @since 1.0
 */
public class FxRateLoaderTest extends AndroidTestCase {

    FxRateLoader mFxRateLoader;

    protected void setUp() throws Exception {
        super.setUp();
        mFxRateLoader = new FxRateLoader(getContext());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * ダミーのHTTPレスポンスボディとなる{@link InputStream}インスタンスを生成して返す.
     */
    private InputStream createDummyInputStream() {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
    }

    /**
     * HttpClient#execute()が正常にレスポンス(200)を返すとき、レスポンスボディが返ること.
     */
    public void testRequestFeed_success() throws Exception {
        // ダミーのHTTPレスポンスボディ
        InputStream expectedContent = createDummyInputStream();

        // ダミーのレスポンスボディを返すHttpEntityモック
        HttpEntity mockHttpEntity = mock(HttpEntity.class);
        when(mockHttpEntity.getContent()).thenReturn(expectedContent);

        // HTTPステータス200を返すStatusLineモック
        StatusLine mockStatusLine = mock(StatusLine.class);
        when(mockStatusLine.getStatusCode()).thenReturn(200);

        // 上で定義したモックオブジェクトを返すHttpResponseモック
        // ステータスを確認してからEntityを取得していること（順序）を確認するため、StrictMock.
        HttpResponse mockResponse = mock(HttpResponse.class);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockResponse.getEntity()).thenReturn(mockHttpEntity);

        // 上で定義したモックオブジェクトを返すHttpClientモック
        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockHttpClient.execute(Mockito.notNull(HttpUriRequest.class)))
                .thenReturn(mockResponse);

        // テスト対象のインスタンスフィールドにあるHttpClientをモックに差し替える
        mFxRateLoader.mHttpclient = mockHttpClient;

        // テスト実行
        InputStream actual = mFxRateLoader.requestFeed();
        assertEquals("Mockに仕込んだInputStreamが返される", expectedContent, actual);

        // HttpResponseのメソッドが正しい順序で呼ばれたかを検証
        InOrder inOrder = inOrder(mockResponse);
        inOrder.verify(mockResponse).getStatusLine();
        inOrder.verify(mockResponse).getEntity();
    }

    /**
     * HTTP_OK以外のステータスコードのとき、InvalidHttpStatusCodeExceptionがスローされること.
     */
    public void testRequestFeed_internalServerError() throws Exception {
        // HTTPステータス500を返すStatusLineモック
        StatusLine mockStatusLine = mock(StatusLine.class);
        when(mockStatusLine.getStatusCode()).thenReturn(500);

        // 上で定義したモックオブジェクトを返すHttpResponseモック
        HttpResponse mockResponse = mock(HttpResponse.class);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);

        // 上で定義したモックオブジェクトを返すHttpClientモック
        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockHttpClient.execute(Mockito.notNull(HttpUriRequest.class)))
                .thenReturn(mockResponse);

        // テスト対象のインスタンスフィールドにあるHttpClientをモックに差し替える
        mFxRateLoader.mHttpclient = mockHttpClient;

        // テスト実行
        try {
            mFxRateLoader.requestFeed();
            fail("InvalidHttpStatusCodeExceptionがthrowされていない");

        } catch (InvalidHttpStatusCodeException e) {
            // 例外がスローされるのが正しい振る舞い
            assertEquals("InvalidHttpStatusCodeExceptionにStatusCodeが渡されている", 500, e.getStatusCode());
        }
    }

    /**
     * 通信切断状態ではUnknownHostExceptionがスローされること.
     */
    public void testRequestFeed_notConnected() throws Exception {
        // 通信できない状態の例外インスタンス
        Exception expected = new UnknownHostException();

        // 通信できない状態の例外をスローするHttpClientモック
        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockHttpClient.execute(Mockito.notNull(HttpUriRequest.class))).thenThrow(expected);

        // テスト対象のインスタンスフィールドにあるHttpClientをモックに差し替える
        mFxRateLoader.mHttpclient = mockHttpClient;

        // テスト実行
        try {
            mFxRateLoader.requestFeed();
            fail("UnknownHostExceptionがthrowされていない");

        } catch (UnknownHostException e) {
            // 例外がスローされるのが正しい振る舞い
        }
    }

    /**
     * フィードのパース結果をFxRateに詰めて返すことができること.
     */
    public void testParseFeed() throws XmlPullParserException, IOException {
        final String SEED_EURUSD = "1.34032";
        final String SEED_EURJPY = "107.836";
        final BigDecimal expectedEurusd = new BigDecimal(SEED_EURUSD);
        final BigDecimal expectedEurjpy = new BigDecimal(SEED_EURJPY);

        // ダミーのパース結果を返すXmlPullParserモック
        // ※EasyMockのStrictMockに比べてメソッドコール順が読み取りにくいため、
        // このような複雑なmock定義はしないほうがいいでしょう
        XmlPullParser mockParser = mock(XmlPullParser.class);
        mockParser.setInput(Mockito.notNull(InputStream.class), Mockito.notNull(String.class));
        when(mockParser.getEventType()).thenReturn(XmlPullParser.START_TAG);
        when(mockParser.getName()).thenReturn("gesmes:Envelope", "gesmes:subject", "gesmes:Sender",
                "gesmes:name", "Cube");
        when(mockParser.next()).thenReturn(XmlPullParser.START_TAG, XmlPullParser.START_TAG,
                XmlPullParser.START_TAG, XmlPullParser.END_TAG, XmlPullParser.START_TAG,
                XmlPullParser.START_TAG, XmlPullParser.START_TAG, XmlPullParser.START_TAG,
                XmlPullParser.START_TAG, XmlPullParser.END_TAG, XmlPullParser.END_TAG,
                XmlPullParser.END_TAG, XmlPullParser.END_DOCUMENT);
        when(mockParser.getAttributeValue(null, "currency")).thenReturn(null, null, "GBP", "USD",
                "JPY");
        when(mockParser.getAttributeValue(null, "rate")).thenReturn("dummy", SEED_EURUSD,
                SEED_EURJPY);

        // テスト対象のインスタンスフィールドにあるHttpClientをモックに差し替える
        mFxRateLoader.mParser = mockParser;

        // テスト実行
        InputStream dummyResponseBody = createDummyInputStream();
        FxRate actual = mFxRateLoader.parseFeed(dummyResponseBody);
        assertEquals("EUR/USDは正しくパースされている", expectedEurusd, actual.eurusd);
        assertEquals("EUR/JPYは正しくパースされている", expectedEurjpy, actual.eurjpy);
    }

}
