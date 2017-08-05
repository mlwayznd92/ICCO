package vn.monkey.icco.util;

/**
 * Created by hoang on 6/21/2016.
 */

public class AppConfig {

    public static final String HEADER_KEY = "Authorization";
    public static final String HEADER_VALUE = " Bearer %s";
    public static final String X_API_KEY = "p6f3astoe8ymvigp11e3ruejeci8rjcw";
    public static final Integer API_TIME_OUT = 30;

    public static final String HOST = "http://45.32.112.173:84";
    public static final String HOST_HTTPS = "https://45.32.112.173:84";

    public static final Integer PRODUCT_SEARCH_NEWEST = 1;
    public static final Integer PRODUCT_SEARCH_POPULAR = 2;
    public static final Integer PRODUCT_SEARCH_HOT_SALE = 3;


    public static final boolean ADD_HOST_TO_LINK = false;
    public static final boolean TEST = true;
    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";


    //Keys
    public static final String TWITTER_CONSUMER_KEY = "TWITTER_CONSUMER_KEY";
    public static final String TWITTER_CONSUMER_SECRET = "TWITTER_CONSUMER_SECRET";
    public static final String LINKEDIN_CONSUMER_KEY = "LINKEDIN_CONSUMER_KEY";
    public static final String LINKEDIN_CONSUMER_SECRET = "LINKEDIN_CONSUMER_SECRET";

    //redirect urls
    public static final String TWITTER_CALLBACK_URL = "http://google.com";


    public static final String BUY_LINK = "http://153.122.53.191/track?" +
                                          "source_content=Product detail page&" +
                                          "source_type=PRODUCT&" +
                                          "source_id=%s&" +
                                          "source_url=ANDROID APP&" +
                                          "dest_url=%s";

    public static final String email = "ecsystem.ytasia@gmail.com";

    // unit ms
    public static final Integer TIME_RELOAD_NEWS = 5;
    public static final Integer TIME_RELOAD_MAPS = 5 * 60 * 1000;
    public static final Integer TIME_RELOAD_GAPS = 5;
    public static final Integer TIME_RELOAD_QAS = 5;
}
