package com.ctrlsmart.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/3/28.
 */
public class EditListInfo implements Serializable {
        String billNumber;
        String company;
        String explain;
        String issue;
        String num;
        String status;
        String time;

        public String getBillNumber()
        {
            return this.billNumber;
        }

        public String getCompany()
        {
            return this.company;
        }

        public String getExplain()
        {
            return this.explain;
        }

        public String getIssue()
        {
            return this.issue;
        }

        public String getNum()
        {
            return this.num;
        }

        public String getStatus()
        {
            return this.status;
        }

        public String getTime()
        {
            return this.time;
        }

        public void setBillNumber(String paramString)
        {
            this.billNumber = paramString;
        }

        public void setCompany(String paramString)
        {
            this.company = paramString;
        }

        public void setExplain(String paramString)
        {
            this.explain = paramString;
        }

        public void setIssue(String paramString)
        {
            this.issue = paramString;
        }

        public void setNum(String paramString)
        {
            this.num = paramString;
        }

        public void setStatus(String paramString)
        {
            this.status = paramString;
        }

        public void setTime(String paramString)
        {
            this.time = paramString;
        }

}
