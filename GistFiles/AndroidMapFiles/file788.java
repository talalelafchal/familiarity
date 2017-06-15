import com.helpshift.HSCallable;

k.put ("hello", "world");
k.put ("foo", "bar");

hs.setMetadataCallback (new HSCallable (){
        public HashMap call() {
            return k;
        }
    });