package vn.monkey.icco.network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.monkey.icco.model.AlertListResponse;
import vn.monkey.icco.model.BaseResponse;
import vn.monkey.icco.model.BuyResponse;
import vn.monkey.icco.model.BuyTransResponse;
import vn.monkey.icco.model.CategoryListResponse;
import vn.monkey.icco.model.CategoryResponse;
import vn.monkey.icco.model.ChangeInfoResponse;
import vn.monkey.icco.model.CheckDeviceTokenResponse;
import vn.monkey.icco.model.ExchangeBuyResponse;
import vn.monkey.icco.model.ExchangeSaleResponse;
import vn.monkey.icco.model.GAPDetailResponse;
import vn.monkey.icco.model.GAPResponse;
import vn.monkey.icco.model.ImageListResponse;
import vn.monkey.icco.model.IsLogonResponse;
import vn.monkey.icco.model.ListTopSearch;
import vn.monkey.icco.model.LoginResponse;
import vn.monkey.icco.model.NewsDetailResponse;
import vn.monkey.icco.model.NewsResponse;
import vn.monkey.icco.model.PriceDetailResponse;
import vn.monkey.icco.model.PriceExpandResponse;
import vn.monkey.icco.model.PriceResponse;
import vn.monkey.icco.model.QuestionAnswerDetailResponse;
import vn.monkey.icco.model.QuestionAnswerResponse;
import vn.monkey.icco.model.QuestionResponse;
import vn.monkey.icco.model.RegisterResponse;
import vn.monkey.icco.model.SaleResponse;
import vn.monkey.icco.model.SaleTransResponse;
import vn.monkey.icco.model.SoldResponse;
import vn.monkey.icco.model.StationResponse;
import vn.monkey.icco.model.TermResponse;
import vn.monkey.icco.model.TotalQuantityResponse;
import vn.monkey.icco.model.TypeCoffeeResponse;
import vn.monkey.icco.model.UserResponse;
import vn.monkey.icco.model.WeatherDetailResponse;
import vn.monkey.icco.util.AppConfig;


/**
 * Created by manhi on 3/1/2016.
 */
public interface ApiService {
    @FormUrlEncoded
    @POST("/subscriber/login")
    Call<LoginResponse> login(@Field("username") String username,
                              @Field("password") String password);

    @GET("/subscriber/get-info")
    Call<UserResponse> getUserInfo(@Header(AppConfig.HEADER_KEY) String authorization,
                                   @Query("authen_type") int authen_type);

    @GET("/news/get-list-news")
    Call<NewsResponse> getNews(@Header(AppConfig.HEADER_KEY) String authorization,
                               @Query("id") long id, @Query("page") int page);

    @GET("/news/search")
    Call<NewsResponse> searchNews(@Header(AppConfig.HEADER_KEY) String authorization,
                                  @Query("keyword") String keyword);

    @GET("/station/get-list-station")
    Call<StationResponse> getStations(@Header(AppConfig.HEADER_KEY) String authorization);

    @GET("/station/search")
    Call<StationResponse> searchStations(@Header(AppConfig.HEADER_KEY) String authorization,
                                         @Query("keyword") String keyword);

    @FormUrlEncoded
    @POST("/app/check-device-token")
    Call<CheckDeviceTokenResponse> checkDeviceToken(
            @Header(AppConfig.HEADER_KEY) String authorization,
            @Field("device_token") String firebaseToken, @Field("channel") int channel,
            @Field("mac") String macMD5);

    @GET("/app/get-price")
    Call<PriceExpandResponse> getPricesExpand(@Header(AppConfig.HEADER_KEY) String authorization,
                                              @Query("date") String date);

    @GET("/app/get-price")
    Call<PriceResponse> getPrices(@Header(AppConfig.HEADER_KEY) String authorization,
                                  @Query("date") String date);

    @GET("/app/get-price-detail")
    Call<PriceDetailResponse> getPriceDetail(@Header(AppConfig.HEADER_KEY) String authorization,
                                             @Query("coffee_old_id") Long coffeeOldId,
                                             @Query("date") String date);

    @FormUrlEncoded
    @POST("/subscriber/register")
    Call<RegisterResponse> register(@Field("username") String username,
                                    @Field("password") String password,
                                    @Field("channel") int channel);

    @FormUrlEncoded
    @POST("/subscriber/change-info")
    Call<ChangeInfoResponse> changeInfo(@Header(AppConfig.HEADER_KEY) String authorization,
                                        @Field("fullname") String fullname,
                                        @Field("address") String address, @Field("sex") int sex,
                                        @Field("image") String image);

    @GET("/app/total-quantity")
    Call<TotalQuantityResponse> getTotalQuantity(
            @Header(AppConfig.HEADER_KEY) String authorization);

    @GET("/app/type-coffee")
    Call<TypeCoffeeResponse> getTypeCoffee(@Header(AppConfig.HEADER_KEY) String authorization);

    @GET("/app/sold")
    Call<SoldResponse> getSold(@Header(AppConfig.HEADER_KEY) String authorization);

    @FormUrlEncoded
    @POST("/subscriber/exchange-coffee")
    Call<SaleResponse> exchange(@Header(AppConfig.HEADER_KEY) String authorization,
                                @Field("total_quality_id") int totalQualityId,
                                @Field("sold_id") int soldId, @Field("type_coffee") int typeCoffee,
                                @Field("location") String location,
                                @Field("location_name") String locationName,
                                @Field("price") int price);

    @GET("/subscriber/transaction-sold")
    Call<SaleTransResponse> getExchangeTrans(@Header(AppConfig.HEADER_KEY) String authorization);

    @GET("/news/detail-news")
    Call<NewsDetailResponse> getDetailNews(@Header(AppConfig.HEADER_KEY) String authorization,
                                           @Query("id") Long id);

    @GET("/gap/detail-gap")
    Call<GAPDetailResponse> getDetailGAP(@Header(AppConfig.HEADER_KEY) String authorization,
                                         @Query("id") Long id);

    @GET("/question-answer/detail-question")
    Call<QuestionAnswerDetailResponse> getDetailQA(
            @Header(AppConfig.HEADER_KEY) String authorization, @Query("id") Long id);

    @GET("/gap/get-list-gap")
    Call<GAPResponse> getGAPs(@Header(AppConfig.HEADER_KEY) String authorization,
                              @Query("page") Integer page);

    @GET("/question-answer/get-list-question-answer")
    Call<QuestionAnswerResponse> getQAs(@Header(AppConfig.HEADER_KEY) String authorization,
                                        @Query("page") Integer page);

    @GET("/question-answer/search")
    Call<QuestionAnswerResponse> searchQA(@Header(AppConfig.HEADER_KEY) String authorization,
                                          @Query("keyword") String keyword);

    @GET("/gap/search")
    Call<GAPResponse> searchGAP(@Header(AppConfig.HEADER_KEY) String authorization,
                                @Query("keyword") String keyword);

    @GET("/subscriber/transaction-buy")
    Call<BuyTransResponse> getBuyTrans(@Header(AppConfig.HEADER_KEY) String authorization);

    @FormUrlEncoded
    @POST("/subscriber/exchange-buy")
    Call<BuyResponse> exchangeBuy(@Header(AppConfig.HEADER_KEY) String authorization,
                                  @Field("total_quantity") int totalQualityId,
                                  @Field("type_coffee") int typeCoffee,
                                  @Field("location") String location,
                                  @Field("location_name") String locationName,
                                  @Field("price") int price);

    @GET("/subscriber/get-list-exchange-buy")
    Call<ExchangeBuyResponse> getExchangeBuy(@Header(AppConfig.HEADER_KEY) String authorization);

    @GET("/subscriber/get-list-exchange-sold")
    Call<ExchangeSaleResponse> getExchangeSale(@Header(AppConfig.HEADER_KEY) String authorization);

    @FormUrlEncoded
    @POST("/question-answer/question-and-answer")
    Call<QuestionResponse> ask(@Header(AppConfig.HEADER_KEY) String authorization,
                               @Field("question") String question,
                               @Field("image") String imageBase64);

    @GET("/app/get-category")
    Call<CategoryResponse> getCategory(@Header(AppConfig.HEADER_KEY) String authorization);

    @GET("/app/term")
    Call<TermResponse> getTerm();

    @GET("weather/get-weather-detail?station_id=105")
    Call<WeatherDetailResponse> getWeatherDetail(@Header(AppConfig.HEADER_KEY) String authorization,
                                                 @Query("station_id") long stationId);

    @FormUrlEncoded
    @POST("/subscriber/reset-password")
    Call<BaseResponse> resetPassword(@Field("username") String username,
                                     @Field("new_password") String newPassword);

    @FormUrlEncoded
    @POST("/subscriber/change-password")
    Call<BaseResponse> changePassword(@Header(AppConfig.HEADER_KEY) String authorization,
                                      @Field("new_password") String newPassword,
                                      @Field("old_password") String oldPassword);

    //----------------------------------------------------------------------------------------------


    @GET("/rest/customer/active")
    Call<BaseResponse> activeAccount(@Query("email") String email, @Query("token") String token);

    @FormUrlEncoded
    @POST("/rest/customer/register")
    Call<RegisterResponse> registerCustomer(@Field("email") String email,
                                            @Field("password") String password,
                                            @Field("fullname") String fullname);

    @GET("/rest/customer/is_logon")
    Call<IsLogonResponse> checkIsLogon();

    @GET("/rest/customer/is_active")
    Call<BaseResponse> checkIsActive(@Header(AppConfig.HEADER_KEY) String authorization);

    @FormUrlEncoded
    @POST("/rest/customer/forgotpassword/")
    Call<BaseResponse> resetPassword(@Field("email") String email);

    @FormUrlEncoded
    @POST("/rest/customer/logout")
    Call<BaseResponse> logOut(@Header(AppConfig.HEADER_KEY) String authorization);


    // can check lai response
    @GET("/rest/image/get/{pos}/{id}")
    Call<ImageListResponse> getImageSlideList(@Path("pos") int pos, @Path("id") int id);

    @FormUrlEncoded
    @POST("/rest/brand/follow")
    Call<BaseResponse> followBrand(@Header(AppConfig.HEADER_KEY) String authorization,
                                   @Field("brand_id") String brand_id);

    @FormUrlEncoded
    @POST("/rest/brand/unfollow")
    Call<BaseResponse> unFollowBrand(@Header(AppConfig.HEADER_KEY) String authorization,
                                     @Field("brand_id") String brand_id);

    @FormUrlEncoded
    @POST("/rest/product/like")
    Call<BaseResponse> likeProduct(@Header(AppConfig.HEADER_KEY) String authorization,
                                   @Field("product_id") String brand_id);

    @FormUrlEncoded
    @POST("/rest/product/unlike")
    Call<BaseResponse> unLikeProduct(@Header(AppConfig.HEADER_KEY) String authorization,
                                     @Field("product_id") String brand_id);

    @Multipart
    @POST("/rest/customer/profile")
    Call<BaseResponse> updateAvatar(@Header("Authorization") String authorization,
                                    @Part("password_cr") RequestBody password_cr,
                                    @Part MultipartBody.Part filePart);


    @Multipart
    @POST("/rest/customer/profile")
    Call<BaseResponse> updateGender(@Header("Authorization") String authorization,
                                    @Part("gender") RequestBody gender);

    //    @Multipart
    //    @POST("/rest/customer/profile")
    //    Call<BaseResponse> updateAvatar(@Header("Authorization") String authorization,
    //                                    @Part("password_cr") RequestBody password_cr,
    //                                    @PartMap() Map<String,RequestBody> mapFileAndName);


    @Multipart
    @POST("/rest/customer/profile")
    Call<BaseResponse> updateFullName(@Header("Authorization") String authorization,
                                      //                                      @Part("password_cr") RequestBody password_cr,
                                      @Part("fullname") RequestBody fullname);

    @Multipart
    @POST("/rest/customer/profile")
    Call<BaseResponse> updatePassword(@Header("Authorization") String authorization,
                                      @Part("password_cr") RequestBody password_cr,
                                      @Part("password_new") RequestBody password_new,
                                      @Part("password_cf") RequestBody password_cf);

    @FormUrlEncoded
    @POST("/rest/customer/social_login")
    Call<LoginResponse> loginSocial(@Field("vendor") String vendor,
                                    @Field("access_token") String access_token,
                                    @Field("access_token_secret") String access_token_secret,
                                    @Field("email") String email);

    @GET("/rest/product/hot_keywords")
    Call<ListTopSearch> getTopSearch(@Query("page") int page, @Query("page_size") int page_size);

    @GET("/rest/notify/list")
    Call<AlertListResponse> getAlertList(@Header(AppConfig.HEADER_KEY) String authorization,
                                         @Query("page") int page,
                                         @Query("page_size") int page_size);

}


