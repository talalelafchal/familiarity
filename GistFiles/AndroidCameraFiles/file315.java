public class Usaha implements SmartFhone{

    @Override
    public String os() {
       return "android 6.1";
    }

    @Override
    public String wifi() {
        return "ya ";
    }
     
    @Override
    public String sinyal() {
       return "3G/4G";
    }

    @Override
    public int camera() {
        return 13 ;
    }

    @Override
    public String sms() {
       return "ya";
    
    }

    @Override
    public String internet() {
        return "ya";
    }

    @Override
    public String panggilan() {
        return "ya";
    }
}


class implementasi{
    
    public static void main(String[] args){
        
        Usaha smartFhone = new Usaha ();
        
        System.out.println("INI HP KELUARAN BARU 2017");
        System.out.println("os : "+smartFhone.os());
        System.out.println("apakah ada wifi : "+smartFhone.wifi());
        System.out.println("sinyal berkekuatann : "+smartFhone.sinyal());
        System.out.println("kamera : "+smartFhone.camera ()+"mp");
        System.out.println("apakah bisa sms : "+smartFhone.sms());
        System.out.println("apakah bisa internet : "+smartFhone.internet());
        System.out.println("apakah bisa panggilan : "+smartFhone.panggilan());
    }         
}

    
