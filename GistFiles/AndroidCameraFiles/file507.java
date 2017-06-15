package model;




public class ExpandedMenuModel
{
    private int id;
    private String name;
    private String num;
    private String pp;
    private String q;
    private boolean hd;
    private boolean fav;

    public ExpandedMenuModel(int id, String name, String num, String pp, String q, boolean hd, boolean fav) {
        this.id = id;
        this.name = name;
        this.num = num;
        this.pp = pp;
        this.q = q;
        this.hd = hd;
        this.fav = fav;
    }

    public ExpandedMenuModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPp() {
        return pp;
    }

    public void setPp(String pp) {
        this.pp = pp;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public boolean getHd() {
        return hd;
    }

    public void setHd(boolean hd) {
        this.hd = hd;
    }

    public boolean getFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

}
