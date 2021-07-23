package mobileapp.ctemplar.com.ctemplarapp.billing;

public class BillingConstants {
    public static final String PRIME_MONTHLY_SKU = "prime_monthly_subscription";
    public static final String KNIGHT_MONTHLY_SKU = "knight_monthly_subscription";
    public static final String MARSHAL_MONTHLY_SKU = "marshall_monthly_subscription";
    public static final String CHAMPION_MONTHLY_SKU = "champion_monthly_subscription";//

    public static final String PRIME_ANNUAL_SKU = "prime_annual_subscription";
    public static final String KNIGHT_ANNUAL_SKU = "knight_annual_subscription";
    public static final String MARSHAL_ANNUAL_SKU = "marshall_annual_subscription";
    public static final String CHAMPION_ANNUAL_SKU = "champion_annual_subscription";//

    public static final String[] ALL_SUBSCRIPTIONS = new String[]{
            PRIME_MONTHLY_SKU,
            KNIGHT_MONTHLY_SKU,
            MARSHAL_MONTHLY_SKU,
            CHAMPION_MONTHLY_SKU,
            PRIME_ANNUAL_SKU,
            KNIGHT_ANNUAL_SKU,
            MARSHAL_ANNUAL_SKU,
            CHAMPION_ANNUAL_SKU
    };

    public static final String[] MONTHLY_SUBSCRIPTIONS = new String[]{
            PRIME_MONTHLY_SKU,
            KNIGHT_MONTHLY_SKU,
            MARSHAL_MONTHLY_SKU,
            CHAMPION_MONTHLY_SKU
    };

    public static final String[] ANNUAL_SUBSCRIPTIONS = new String[]{
            PRIME_ANNUAL_SKU,
            KNIGHT_ANNUAL_SKU,
            MARSHAL_ANNUAL_SKU,
            CHAMPION_ANNUAL_SKU
    };

    public static final String PLAY_STORE_SUBSCRIPTION_URL
            = "https://play.google.com/store/account/subscriptions";
    public static final String PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL
            = "https://play.google.com/store/account/subscriptions?sku=%s&package=%s";
}
