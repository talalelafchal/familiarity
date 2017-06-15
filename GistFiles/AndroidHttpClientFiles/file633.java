public class Passenger {

    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAIL = "email";
    public static final String PHONE_NUMBER = "phone_number";

    public static final String REGISTRATION_STATUS = "registration_status";
    public static final String USER_TYPE = "user_type";
    public static final String REGISTRATION_TOKEN = "registration_token";


    public static final String ID = "_id";
    public static final String DEVICE_ID = "device_id";
    public static final String CARD_NUMBER = "card_number";
    public static final String TYPE = "type";

    public static final String CARD_USE_TYPE = "card_use_type";
    public static final String CARD_ID = "card_id";
    public static final String STATE = "state";
    public static final String CITY = "city";
    public static final String PASSENGER_DETAIL_ID = "passenger_detail_id";
    public static final String IMAGE = "image";

    public static final String IS_CASH_DEFAULT = "is_cash_default";
    public static final String AFFILIATE_ID = "affiliate_id";
    public static final String AFFILIATE = "affiliate";
    public static final String AFFILIATE_LOGO = "affiliate_logo";


    public static final String BT_PAYMENT_TOKEN = "bt_payment_token";
    public static final String RATTING = "ratting";
    public static final String NUMBER_OF_TRIPS = "number_of_trips";
    public static final String OS_TYPE = "os_type";
    public static final String SPECIAL_INSTRUCTIONS = "special_instructions";


    @SerializedName(FIRST_NAME)
    public String first_name;

    @SerializedName(LAST_NAME)
    public String last_name;
    @SerializedName(EMAIL)
    public String email;

    @SerializedName(PHONE_NUMBER)
    public String phone_number;

    @SerializedName(REGISTRATION_STATUS)
    public String registration_status;
    @SerializedName(USER_TYPE)
    public String user_type;
    @SerializedName(REGISTRATION_TOKEN)
    public String registration_token;

    @SerializedName(ID)
    public String _id;
    @SerializedName(DEVICE_ID)
    public String device_id;
    @SerializedName(CARD_NUMBER)
    public String card_number;
    @SerializedName(TYPE)
    public String type;

    @SerializedName(CARD_USE_TYPE)
    public String card_use_type;
    @SerializedName(CARD_ID)
    public String card_id;
    @SerializedName(STATE)
    public String state;
    @SerializedName(CITY)
    public String city;
    @SerializedName(PASSENGER_DETAIL_ID)
    public String passenger_detail_id;
    @SerializedName(IMAGE)
    public String image;

    @SerializedName(IS_CASH_DEFAULT)
    public String is_cash_default;
    @SerializedName(AFFILIATE_ID)
    public String affiliate_id;
    @SerializedName(AFFILIATE)
    public String affiliate;
    @SerializedName(AFFILIATE_LOGO)
    public String affiliate_logo;

    @SerializedName(BT_PAYMENT_TOKEN)
    public String bt_payment_token;
    @SerializedName(RATTING)
    public String ratting;
    @SerializedName(NUMBER_OF_TRIPS)
    public String number_of_trips;
    @SerializedName(OS_TYPE)
    public String os_type;
    @SerializedName(SPECIAL_INSTRUCTIONS)
    public String special_instructions;

}