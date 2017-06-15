public class LockerApp extends MultiLanguageBaseApp {
    public final void b() {
        if (mobi.espier.locker.a.b.a(getApplicationContext())) {
            c();
            mobi.espier.locker.a.b.a(true);
            startService(new Intent(getApplicationContext(), mobi / espier / locker / LockerService));
            mobi.espier.locker.d.a(getApplicationContext()).a();
            mobi.espier.locker.a.d.c(this);
        } else {
            d();
        }
        mobi.espier.locker.a.b.E(getApplicationContext());
    }
}