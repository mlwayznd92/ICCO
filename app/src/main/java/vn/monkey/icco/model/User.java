package vn.monkey.icco.model;

/**
 * Created by hoang on 6/26/2016.
 */

public class User {

    private Long id;
    private Integer authenType;
    private String msisdn;
    private String username;
    private String password;
    private String token;
    private Integer status;
    private String email;
    private String fullName;
    private Long lastLogin;
    private Long lastLoginSession;
    private Long birthday;
    private Integer sex;
    private String avatarUrl;
    private Long skypeId;
    private Long googleId;
    private Long facebookId;
    private Long createdDate;
    private Long updatedDate;
    private Integer clientType;
    private Integer usingPromotion;
    private String address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAuthenType() {
        return authenType;
    }

    public void setAuthenType(Integer authenType) {
        this.authenType = authenType;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Long getLastLoginSession() {
        return lastLoginSession;
    }

    public void setLastLoginSession(Long lastLoginSession) {
        this.lastLoginSession = lastLoginSession;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Long getSkypeId() {
        return skypeId;
    }

    public void setSkypeId(Long skypeId) {
        this.skypeId = skypeId;
    }

    public Long getGoogleId() {
        return googleId;
    }

    public void setGoogleId(Long googleId) {
        this.googleId = googleId;
    }

    public Long getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(Long facebookId) {
        this.facebookId = facebookId;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Long updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    public Integer getUsingPromotion() {
        return usingPromotion;
    }

    public void setUsingPromotion(Integer usingPromotion) {
        this.usingPromotion = usingPromotion;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
