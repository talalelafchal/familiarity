@AutoValue
public abstract class Address implements AddressModel, Parcelable {

    public static final Factory<Address> FACTORY = new Factory<>(AutoValue_Address::new);

    public static final Func1<Cursor, Address> MAPPER = FACTORY.selectAllMapper()::map;

    public static Builder builder() {
        return new AutoValue_Address.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder id(long id);
        public abstract Builder name(String name);
        public abstract Builder line1(String line1);
        public abstract Builder line2(String line2);
        public abstract Builder landmark(String landmark);
        public abstract Builder city(String city);
        public abstract Builder country(String country);
        public abstract Builder pincode(long pincode);
        public abstract Address build();
    }
    
}