public class hp implements soal {
    @Override
    public int camera() {
        return 13;
    }

    @Override
    public String os() {
        return "android 6.1";
    }

    @Override
    public String support() {
        return "ya";
    }

    @Override
    public String sinyal() {
        return "4G";
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
class Implementasi{
        
    public static void main(String[] args){
        hp hape= new hp();
        
        System.out.println("camera : "+hape.camera());
        System.out.println("os : "+hape.os());
        System.out.println("support : "+hape.support());
        System.out.println("sinyal: "+hape.sinyal());
        System.out.println("sms: "+hape.sms());
        System.out.println("panggilan: "+hape.panggilan());
    }
    }