package vn.monkey.icco.model;

/**
 * Created by hoang on 6/23/2016.
 */
public class UserResponse extends BaseResponse {

    public Data data;

    public class Data {
        public Long id;
        public Integer authen_type;
        public String msisdn;
        public String username;
        public Integer status;
        public String email;
        public String full_name;
        public Long last_login_at;
        public Long last_login_session;
        public Long birthday;
        public Integer sex;
        public String avatar;
        public Long created_at;
        public Long updated_at;
        public String address;
    }
}
