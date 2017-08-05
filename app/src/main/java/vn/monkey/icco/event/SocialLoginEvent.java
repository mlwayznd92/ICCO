package vn.monkey.icco.event;

/**
 * Created by hoang on 7/7/2016.
 */
public class SocialLoginEvent {
    public String vendor;
    public String accessToken;
    public String secretToken;
    public String email;


    public SocialLoginEvent(String vendor, String accessToken) {
        this.vendor = vendor;
        this.accessToken = accessToken;
        this.secretToken = "";
        this.email = "";
    }

    public SocialLoginEvent(String vendor, String accessToken, String secretToken, String email) {
        this.vendor = vendor;
        this.accessToken = accessToken;
        this.secretToken = secretToken;
        this.email = email;
    }
}
