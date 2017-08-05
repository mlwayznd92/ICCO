package vn.monkey.icco.model;


/**
 * Created by hoang on 6/23/2016.
 */
public class LoginResponse extends BaseResponse {

    public Data data;

    public class Data {
        public Long id;
        public String message;
        public String username;
        public String avatar;
        public String full_name;
        public String token;
        public Long expired_date;
        public Integer authen_type;
        public Integer channel;
    }
}
