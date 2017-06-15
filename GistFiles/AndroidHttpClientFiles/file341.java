package com.wts;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.URI;

public class getCommerce {

    /**
     * 保存记录
     * */
    private static void save(JSONArray jsStrs, XSSFSheet SheetBefore, XSSFSheet SheetAfter, int count, int i) {
        if (jsStrs.size() > 0) {
            for (int k = 0; k < jsStrs.size(); k++) {
                XSSFRow nextRows = SheetAfter.createRow(SheetAfter.getLastRowNum()+1);
                for (int j = 0; j < count; j++) {
                    nextRows.createCell(j).setCellValue(SheetBefore.getRow(i).getCell(j).toString());
                }
                JSONObject jsStr = jsStrs.getJSONObject(k);
                nextRows.createCell(count).setCellValue(jsStr.getString("grxm"));
                nextRows.createCell(count + 1).setCellValue(jsStr.getString("dwmc"));
                nextRows.createCell(count + 2).setCellValue(jsStr.getString("zch"));
                nextRows.createCell(count + 3).setCellValue(jsStr.getString("clrq"));
                nextRows.createCell(count + 4).setCellValue(jsStr.getString("zxrq"));
                nextRows.createCell(count + 5).setCellValue(jsStr.getString("dxrq"));
                nextRows.createCell(count + 6).setCellValue(jsStr.getString("rysf"));
                nextRows.createCell(count + 7).setCellValue(jsStr.getString("lxdh"));
                nextRows.createCell(count + 8).setCellValue(jsStr.getString("djjg"));
            }
        }
    }
    /**
     * 发起请求
     * */
    private static void send(CloseableHttpClient login_httpclient,XSSFSheet sheetBefore, XSSFSheet sheetAfter, int count, int i,String personNumber) throws Exception{
        URI gsUrl = new URIBuilder()
                .setScheme("http")
                .setHost("10.153.50.108:7001")
                .setPath("/lemis3/lemis3Person.do")
                .setParameter("method", "queryGsjPersonInfo")
                .setParameter("_xmlString", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><p><s gmsfhm=\"" + personNumber + "\" zch=\"\" xshs=\"200\" ESTDATE=\"\" clzzrq=\"\" REVDATE=\"\" CANDATE=\"\" zxzzrq=\"\" dxzzrq=\"\" REGORG=\"\" ENTCAT=\"\" /></p>")
                .setParameter("_jbjgqxfw", "undefined")
                .setParameter("_sbjbjg", "undefined")
                .setParameter("_dwqxfw", "undefined")
                .build();
        HttpPost gs_post = new HttpPost(gsUrl);
        // 创建默认的httpClient实例.
        CloseableHttpResponse gs_response = login_httpclient.execute(gs_post);
        HttpEntity gs_entity = gs_response.getEntity();
        String res = EntityUtils.toString(gs_entity, "UTF-8");

        if (util.getCount(res, "init('true','true','[") == 1) {
            JSONArray jsStrs = JSONArray.fromObject(res.substring(res.lastIndexOf("init('true','true','[") + 20, res.lastIndexOf("]');</script>") + 1));
            save(jsStrs,sheetBefore,sheetAfter,count,i);
        } else if((util.getCount(res, "init('true','true','[") == 2)) {
            JSONArray jsStrs1 = JSONArray.fromObject(res.substring(res.lastIndexOf("init('true','true','[") + 20, res.lastIndexOf("]');</script>") + 1));
            save(jsStrs1,sheetBefore,sheetAfter,count,i);
            JSONArray jsStrs2 = JSONArray.fromObject(res.substring(res.indexOf("init('true','true','[") + 20, res.indexOf("]');</script>") + 1));
            save(jsStrs2,sheetBefore,sheetAfter,count,i);
        }else{

        }
        gs_response.close();
    }
    public static void main(String[] args) throws Exception {
        System.out.println("               欢迎使用济南市工商信息批量速查程序         ");
        System.out.println(" ");
        System.out.println("------------------------用前须知------------------------");
        System.out.println(" ");
        System.out.println("1：本程序必须在办公内网环境下运行！");
        System.out.println("2：待查的Excel文件必须放置在C盘根目录下！");
        System.out.println("3：Excel文件后缀为xlsx，不支持xls后缀的低版本Excel文件！");
        System.out.println("4：Excel文件第一行为标题，如果没有可以不填写！");
        System.out.println("5：Excel文件第一列内容必须为身份证号码！");
        System.out.println("6：Excel文件中的信息必须完整，即不能有空白的单元格！");
        System.out.println("7：单次查询数据量不要超过1000人，否则抓取速度会非常慢！");
        System.out.println(" ");
        System.out.println(" ");
        System.out.println("------------------------结果说明------------------------");
        System.out.println(" ");
        System.out.println("1：查询结果包含该人员全部的工商记录，包括在营和注销的！");
        System.out.println("2：核查时会自动将18位身份证号码转为15位进行了二次查询！");
        System.out.println("3：核查结果跟劳动993系统中的工商查询结果保持一致！");
        System.out.println(" ");
        System.out.println("-------------------------------------------------------");
        System.out.println(" ");
        String result;
        do {
            // 输出提示文字
            System.out.print("请输入待查的Excel文件名：");
            InputStreamReader is_reader = new InputStreamReader(System.in);
            result = new BufferedReader(is_reader).readLine();
        } while (result.equals("")); // 当用户输入无效的时候，反复提示要求用户输入
        File file = new File("C:\\" + result + ".xlsx");
        if (!file.exists()) {
            System.out.print("C:\\" + result + ".xlsx文件不存在！");
            System.out.print("按回车关闭程序...");
            while (true) {
                if (System.in.read() == '\n')
                    System.exit(0);
            }
        } else {
            URI loginUri = new URIBuilder()
                    .setScheme("http")
                    .setHost("10.153.50.108:7001")
                    .setPath("/lemis3/logon.do")
                    .setParameter("method", "doLogon")
                    .setParameter("userid", "hyzt")
                    .setParameter("passwd", util.getMD5("7957908"))
                    .setParameter("userLogSign", "0")
                    .setParameter("passWordLogSign", "0")
                    .setParameter("screenHeight", "768")
                    .setParameter("screenWidth", "1024")
                    .setParameter("mode", "")
                    .build();
            HttpPost login_post = new HttpPost(loginUri);
            // 创建默认的httpClient实例.
            CloseableHttpClient login_httpclient = HttpClients.createDefault();
            CloseableHttpResponse login_response = login_httpclient.execute(login_post);
            HttpEntity login_entity = login_response.getEntity();
            if (login_entity != null) {
                System.out.println("  ");
                System.out.println("是否成功接入工商数据库: " + EntityUtils.toString(login_entity, "UTF-8"));
            }
            System.out.println("3秒后开始抓取数据....");
            Thread.sleep(1000);
            System.out.println("2秒后开始抓取数据....");
            Thread.sleep(1000);
            System.out.println("1秒后开始抓取数据....");
            Thread.sleep(1000);
            System.out.println("开始抓取....");
            XSSFWorkbook workbookAfter = new XSSFWorkbook();
            XSSFSheet sheetAfter = workbookAfter.createSheet("sheet1");
            XSSFWorkbook workbookBefore = new XSSFWorkbook(new FileInputStream("c:\\" + result + ".xlsx"));
            XSSFSheet sheetBefore = workbookBefore.getSheetAt(0);
            XSSFRow rowAfter = sheetAfter.createRow(0);
            int count = sheetBefore.getRow(0).getPhysicalNumberOfCells();
            int total = sheetBefore.getLastRowNum();
            for (int j = 0; j < count; j++) {
                rowAfter.createCell(j).setCellValue(sheetBefore.getRow(0).getCell(j).toString());
            }
            rowAfter.createCell(count).setCellValue("个人姓名");
            rowAfter.createCell(count + 1).setCellValue("单位名称");
            rowAfter.createCell(count + 2).setCellValue("注册号");
            rowAfter.createCell(count + 3).setCellValue("成立日期");
            rowAfter.createCell(count + 4).setCellValue("注销日期");
            rowAfter.createCell(count + 5).setCellValue("吊销日期");
            rowAfter.createCell(count + 6).setCellValue("人员身份");
            rowAfter.createCell(count + 7).setCellValue("联系电话");
            rowAfter.createCell(count + 8).setCellValue("登记机关");

            for (int i = 1; i < total + 1; i++) {
                String personNumber = sheetBefore.getRow(i).getCell(0).getStringCellValue();
                System.out.println("正在抓取第"+i+"行人员，身份证号码为："+personNumber);
                if (personNumber.length()>=17){
                    send(login_httpclient,sheetBefore,sheetAfter,count,i,personNumber);
                    String personNumber15 =personNumber.substring(0,6)+personNumber.substring(8,17);
                    send(login_httpclient,sheetBefore,sheetAfter,count,i,personNumber15);
                } else{
                    send(login_httpclient,sheetBefore,sheetAfter,count,i,personNumber);
                }
            }
            login_response.close();
            login_httpclient.close();
            FileOutputStream os = new FileOutputStream("c:\\" + result + "_工商数据抓取后.xlsx");
            workbookAfter.write(os);
            os.close();
            System.out.println("  ");
            System.out.println("  ");
            System.out.println("工商数据抓取完成！");
            System.out.println("请查看文件--> c:\\" + result + "_工商数据抓取后.xlsx");
            System.out.println("  ");
            System.out.println("按回车键退出程序...");
            while (true) {
                if (System.in.read() == '\n')
                    System.exit(0);
            }
        }
    }
}
