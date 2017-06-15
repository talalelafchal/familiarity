package cn.android.water.contactsmerge;

/**
 * Created by water.yue on 2015/1/13.
 */
public class ContactObj {
    private String id;
    private String name;
    private String phone;
    public ContactObj(String _id){
        this.id = _id;
    }
    // public void setId(String value)
    // {
    //     this.id = value;
    // }
    public String getId()
    {
        return this.id;
    }
    public void setName(String value)
    {
        this.name = value;
    }
    public String getName()
    {
        return this.name;
    }
    public void setPhone(String value)
    {
        this.phone = value;
    }
    public String getPhone()
    {
        return this.phone;
    }

}

