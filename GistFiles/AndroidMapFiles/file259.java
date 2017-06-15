public interface AddressCallback {
    void onLoad(android.location.Address address);
    void onError(Exception e);
}