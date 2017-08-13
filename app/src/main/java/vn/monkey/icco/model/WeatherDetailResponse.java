package vn.monkey.icco.model;

import java.util.List;

/**
 * Created by Mlwayz on 7/1/2017.
 */

public class WeatherDetailResponse extends BaseResponse {

    public Data data;

    public class Data {
        public Item items;
        public List<Item> events;
        public List<Item> weather_week_ago;
    }

    public class Item {
        public Long id;
        public String station_code;
        public Integer precipitation;
        public Integer tmax;
        public Integer tmin;
        public String wnddir;
        public Long wndspd;
        public Long station_id;
        public Long timestamp;
        public Long created_at;
        public Long updated_at;
        public String image;
        public String station_name;
        public String wndspd_km_h;
        public String content;
        public String t_average;
        public String province_name;
        public String precipitation_unit;
    }
}
