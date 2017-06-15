package com.example.client;

import android.graphics.Color;
import android.os.Bundle;
import base.BaseActivity;
import my.TextView;
import po.PersonPO;
import service.HelloWorld;
import service.meta.TestMeta;
import stub.impl.HelloWorldImpl;
import utils.Handler;
import utils.sqllite.MySQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyActivity extends BaseActivity {
    public final static int SAY_HELLO_EVENT = 1;


    @Override
    protected void handle(int what, Object ojb) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView t1 = (TextView) findViewById(R.id.t1);
        t1.setBackgroundColor(Color.BLUE);
        TextView t2 = (TextView) findViewById(R.id.t2);
        t2.setBackgroundColor(Color.GREEN);


        try {
            PersonPO personPO=new PersonPO();
            personPO.setAge(20);
            personPO.setScore(20.5f);
            personPO.setName("smh");
            PersonPO criteriaPO=new PersonPO();
            criteriaPO.setName("test");
            MySQLiteOpenHelper sqLiteOpenHelper=new MySQLiteOpenHelper(this);
            List<Object> resList=sqLiteOpenHelper.query(criteriaPO);
            System.out.println(resList);
            sqLiteOpenHelper.update(personPO,criteriaPO);
            resList=sqLiteOpenHelper.query(personPO);
            System.out.println(resList);
            sqLiteOpenHelper.delete(personPO);
            resList=sqLiteOpenHelper.query(personPO);
            System.out.println(resList);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        sqLiteOpenHelper.execSQL(sql);
//        call(SAY_HELLO_EVENT, true, new Handler() {
//            @Override
//            public Object call() {
//                HelloWorld helloWorld = new HelloWorldImpl();
//                TestMeta testMeta = new TestMeta();
//                testMeta.setI(10);
//                testMeta.setMsg("haha");
//                List<String> msgs = new ArrayList<String>();
//                msgs.add("smh");
//                testMeta.setI(5);
//                testMeta.setNow(new Date());
//                testMeta.setTf(false);
//                List<TestMeta> res = helloWorld.sayHello(msgs, testMeta);
//                return res;
//            }
//        });
    }
}

