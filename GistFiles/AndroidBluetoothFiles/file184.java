package andrej.jelic.attendance;

/**
 * Created by Korisnik on 19.7.2015..
 */
public class Tables {

    int _id;
    String _tableName;

    public Tables(){

    }

    public Tables(String _tableName){
        this._tableName = _tableName;
    }

    public String getTableName() {
        return this._tableName;
    }

    public void setTableName(String _tableName) {
        this._tableName = _tableName;
    }


}
