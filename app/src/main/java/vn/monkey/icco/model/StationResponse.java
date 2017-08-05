package vn.monkey.icco.model;

import java.util.List;

/**
 * Created by Mlwayz on 6/19/2017.
 */

public class StationResponse extends BaseResponse {

    public Data data;

    public class Data {
        public List<Item> items;
    }

    public class Item {
        public long id;
        public String station_name;
        public String province_name;
        public String district_name;
        public String latitude;
        public String longtitude;
        public int status;
    }
}
