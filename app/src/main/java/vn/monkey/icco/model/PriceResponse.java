package vn.monkey.icco.model;

import java.util.List;

/**
 * Created by Mlwayz on 6/18/2017.
 */

public class PriceResponse extends BaseResponse {

    public Data data;

    public class Data {
        public List<Item> items;
    }

    public class Item {
        public Long id;
        public String province_name;
        public String organisation_name;
        public Integer price_average;
        public String unit;
        public Long created_at;
        public Long coffee_old_id;
    }
}
