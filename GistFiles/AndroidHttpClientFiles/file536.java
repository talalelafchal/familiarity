/*
 * Copyright(c) 2012-2013 Android Test and Evaluation Club.
 *
 */

package org.android_tec.atmarkit.usemockexample.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.easymock.EasyMock;
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
        HttpEntity mockHttpEntity = EasyMock.createMock(HttpEntity.class);
        EasyMock.expect(mockHttpEntity.getContent()).andReturn(expectedContent);
        EasyMock.replay(mockHttpEntity);

        // HTTPステータス200を返すStatusLineモック
        StatusLine mockStatusLine = EasyMock.createMock(StatusLine.class);
        EasyMock.expect(mockStatusLine.getStatusCode()).andReturn(200);
        EasyMock.replay(mockStatusLine);

        // 上で定義したモックオブジェクトを返すHttpResponseモック
        // ステータスを確認してからEntityを取得していること（順序）を確認するため、StrictMock.
        HttpResponse mockResponse = EasyMock.createStrictMock(HttpResponse.class);
        EasyMock.expect(mockResponse.getStatusLine()).andReturn(mockStatusLine);
        EasyMock.expect(mockResponse.getEntity()).andReturn(mockHttpEntity);
        EasyMock.replay(mockResponse);

        // 上で定義したモックオブジェクトを返すHttpClientモック
        HttpClient mockHttpClient = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(mockHttpClient.execute((HttpUriRequest)EasyMock.notNull())).andReturn(
                mockResponse);
        EasyMock.replay(mockHttpClient);

        // テスト対象のインスタンスフィールドにあるHttpClientをモックに差し替える
        mFxRateLoader.mHttpclient = mockHttpClient;

        // テスト実行
        InputStream actual = mFxRateLoader.requestFeed();
        assertEquals("Mockに仕込んだInputStreamが返される", expectedContent, actual);

        // HttpResponseのメソッドが正しい順序で呼ばれたかを検証
        EasyMock.verify(mockResponse);

        // その他のモックの検証（メソッドの戻り値で判断できているので冗長な確認）
        EasyMock.verify(mockHttpEntity);
        EasyMock.verify(mockStatusLine);
        EasyMock.verify(mockHttpClient);
    }

    /**
     * HTTP_OK以外のステータスコードのとき、InvalidHttpStatusCodeExceptionがスローされること.
     */
    public void testRequestFeed_internalServerError() throws Exception {
        // HTTPステータス500を返すStatusLineモック
        StatusLine mockStatusLine = EasyMock.createMock(StatusLine.class);
        EasyMock.expect(mockStatusLine.getStatusCode()).andReturn(500);
        EasyMock.replay(mockStatusLine);

        // 上で定義したモックオブジェクトを返すHttpResponseモック
        HttpResponse mockResponse = EasyMock.createMock(HttpResponse.class);
        EasyMock.expect(mockResponse.getStatusLine()).andReturn(mockStatusLine);
        EasyMock.replay(mockResponse);

        // 上で定義したモックオブジェクトを返すHttpClientモック
        HttpClient mockHttpClient = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(mockHttpClient.execute((HttpUriRequest)EasyMock.notNull())).andReturn(
                mockResponse);
        EasyMock.replay(mockHttpClient);

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

        // モックの検証（メソッドの戻り値で判断できているので冗長な確認）
        EasyMock.verify(mockStatusLine);
        EasyMock.verify(mockResponse);
        EasyMock.verify(mockHttpClient);
    }

    /**
     * 通信切断状態ではUnknownHostExceptionがスローされること.
     */
    public void testRequestFeed_notConnected() throws Exception {
        // 通信できない状態の例外インスタンス
        Exception expected = new UnknownHostException();

        // 通信できない状態の例外をスローするHttpClientモック
        HttpClient mockHttpClient = EasyMock.createMock(HttpClient.class);
        EasyMock.expect(mockHttpClient.execute((HttpUriRequest)EasyMock.notNull())).andThrow(
                expected);
        EasyMock.replay(mockHttpClient);

        // テスト対象のインスタンスフィールドにあるHttpClientをモックに差し替える
        mFxRateLoader.mHttpclient = mockHttpClient;

        // テスト実行
        try {
            mFxRateLoader.requestFeed();
            fail("UnknownHostExceptionがthrowされていない");

        } catch (UnknownHostException e) {
            // 例外がスローされるのが正しい振る舞い
        }

        // モックの検証（メソッドの戻り値で判断できているので冗長な確認）
        EasyMock.verify(mockHttpClient);
    }

    /**
     * フィードのパース結果をFxRateに詰めて返すことができること（Mock使用版）.
     */
    public void testParseFeed_useMock() throws XmlPullParserException, IOException {
        final String SEED_EURUSD = "1.34032";
        final String SEED_EURJPY = "107.836";
        final BigDecimal expectedEurusd = new BigDecimal(SEED_EURUSD);
        final BigDecimal expectedEurjpy = new BigDecimal(SEED_EURJPY);

        // ダミーのパース結果を返すXmlPullParserモック
        XmlPullParser mockParser = EasyMock.createStrictMock(XmlPullParser.class);
        mockParser.setInput((InputStream)EasyMock.notNull(), (String)EasyMock.notNull());
        EasyMock.expect(mockParser.getEventType()).andReturn(XmlPullParser.START_TAG);
        EasyMock.expect(mockParser.getName()).andReturn("gesmes:Envelope");// 無視される
        EasyMock.expect(mockParser.next()).andReturn(XmlPullParser.START_TAG);
        EasyMock.expect(mockParser.getName()).andReturn("gesmes:subject");// 無視される
        EasyMock.expect(mockParser.next()).andReturn(XmlPullParser.START_TAG);
        EasyMock.expect(mockParser.getName()).andReturn("gesmes:Sender");// 無視される
        EasyMock.expect(mockParser.next()).andReturn(XmlPullParser.START_TAG);
        EasyMock.expect(mockParser.getName()).andReturn("gesmes:name");// 無視される
        EasyMock.expect(mockParser.next()).andReturn(XmlPullParser.END_TAG);
        EasyMock.expect(mockParser.next()).andReturn(XmlPullParser.START_TAG);
        EasyMock.expect(mockParser.getName()).andReturn("Cube");// これはコンテナ
        EasyMock.expect(mockParser.getAttributeValue(null, "currency")).andReturn(null);
        EasyMock.expect(mockParser.next()).andReturn(XmlPullParser.START_TAG);
        EasyMock.expect(mockParser.getName()).andReturn("Cube");// これはtime属性付きコンテナ
        EasyMock.expect(mockParser.getAttributeValue(null, "currency")).andReturn(null);
        EasyMock.expect(mockParser.next()).andReturn(XmlPullParser.START_TAG);
        EasyMock.expect(mockParser.getName()).andReturn("Cube");// 対象外の通貨ペア
        EasyMock.expect(mockParser.getAttributeValue(null, "currency")).andReturn("GBP");
        EasyMock.expect(mockParser.getAttributeValue(null, "rate")).andReturn("dummy");
        EasyMock.expect(mockParser.next()).andReturn(XmlPullParser.START_TAG);
        EasyMock.expect(mockParser.getName()).andReturn("Cube");// USD
        EasyMock.expect(mockParser.getAttributeValue(null, "currency")).andReturn("USD");
        EasyMock.expect(mockParser.getAttributeValue(null, "rate")).andReturn(SEED_EURUSD);
        EasyMock.expect(mockParser.next()).andReturn(XmlPullParser.START_TAG);
        EasyMock.expect(mockParser.getName()).andReturn("Cube");// JPY
        EasyMock.expect(mockParser.getAttributeValue(null, "currency")).andReturn("JPY");
        EasyMock.expect(mockParser.getAttributeValue(null, "rate")).andReturn(SEED_EURJPY);
        EasyMock.expect(mockParser.next()).andReturn(XmlPullParser.END_TAG); // </Cube+time>
        EasyMock.expect(mockParser.next()).andReturn(XmlPullParser.END_TAG); // </Cube>
        EasyMock.expect(mockParser.next()).andReturn(XmlPullParser.END_TAG); // </gesmes:Envelope>
        EasyMock.expect(mockParser.next()).andReturn(XmlPullParser.END_DOCUMENT);
        EasyMock.replay(mockParser);

        // テスト対象のインスタンスフィールドにあるHttpClientをモックに差し替える
        mFxRateLoader.mParser = mockParser;

        // テスト実行
        InputStream dummyResponseBody = createDummyInputStream();
        FxRate actual = mFxRateLoader.parseFeed(dummyResponseBody);
        assertEquals("EUR/USDは正しくパースされている", expectedEurusd, actual.eurusd);
        assertEquals("EUR/JPYは正しくパースされている", expectedEurjpy, actual.eurjpy);

        // モックの検証（メソッドの戻り値で判断できているので冗長な確認）
        EasyMock.verify(mockParser);
    }

    /**
     * フィードのパース結果をFxRateに詰めて返すことができること（ファイル使用版）.
     * 
     * @throws URISyntaxException
     */
    public void testParseFeed_useFile() throws XmlPullParserException, IOException,
            URISyntaxException {
        final URI FEED_PATH = new URI("file:///android_asset/testfeed.xml");
        final BigDecimal expectedEurusd = new BigDecimal("1.3312");
        final BigDecimal expectedEurjpy = new BigDecimal("107.95");

        // テスト実行
        File file = new File(FEED_PATH);
        FileInputStream dummyResponseBody = new FileInputStream(file);
        FxRate actual = mFxRateLoader.parseFeed(dummyResponseBody);
        assertEquals("EUR/USDは正しくパースされている", expectedEurusd, actual.eurusd);
        assertEquals("EUR/JPYは正しくパースされている", expectedEurjpy, actual.eurjpy);
    }
}