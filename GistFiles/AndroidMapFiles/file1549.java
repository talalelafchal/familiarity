package com.example.abhishek.assignmentwiredelta.Model;

import android.util.Log;

/**
 * Created by Ramya on 09-04-2016.
 */
public class CompanyDetails {

    private String companyid;
    private String companyname;
    private String companyowner;
    private String companystartdate;
    private String companydescription;
    private String companydepartments;




    public CompanyDetails(String companyId, String companyName, String companyOwner, String companyStartDate, String companyDescription, String companyDepartments) {
        this.companyid = companyId;
        this.companyname = companyName;
        Log.i("model", companyname);
        this.companyowner = companyOwner;
        this.companystartdate=companyStartDate;
        this.companydescription=companyDescription;
        this.companydepartments=companyDepartments;
    }


    public String getCompanyid() {
        return this.companyid;
    }

    public String getCompanyname() {
        return this.companyname;
    }

    public String getCompanyowner() {
        return this.companyowner;
    }

    public String getCompanystartdate() {
        return this.companystartdate;
    }

    public String getCompanydepartments() {
        return this.companydepartments;
    }

    public String getCompanydescription() {
        return this.companydescription;
    }






}



