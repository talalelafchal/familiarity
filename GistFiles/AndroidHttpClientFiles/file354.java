package com.eviltester.webdriver;

import junit.framework.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import static java.lang.Thread.sleep;


public class MyFirsClass {

    @Test
    public  void startWebdriver(){
        WebDriver mozila= new FirefoxDriver();

        mozila.navigate().to("https://skift.dataswell.com/portal/index.html#login");

        WebElement email=mozila.findElement(By.name("email")) ;
        System.out.print(email.isEnabled());
        if(email.isEnabled()){
        email.click();
        email.sendKeys("yuri.plakosh@gmail.com");
        }
            WebElement psw=mozila.findElement(By.name("password")) ;
            System.out.print(psw.isEnabled() );
            if(psw.isEnabled()){
                psw.click();
                psw.sendKeys("159753");
        }
        WebElement loginbtn=mozila.findElement(By.id("loginbtn")) ;
        System.out.print(loginbtn.isEnabled() );
        if(loginbtn.isEnabled()) try {
            sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        loginbtn.click();
    }
}
