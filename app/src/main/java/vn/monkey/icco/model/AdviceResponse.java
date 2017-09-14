package vn.monkey.icco.model;

/**
 * Created by mlwayz on 9/8/17.
 */

public class AdviceResponse extends BaseResponse {

    public Data data;

    public class Data {
        public Long id;
        public String gap;
        public Long created_at;
        public Long updated_at;
        public String title;
        public Integer type;
        public Long temperature_max;
        public Long temperature_min;
        public String image;
        public Long precipitation_max;
        public Long precipitation_min;
        public Float windspeed_max;
        public Float windspeed_min;
    }
}
