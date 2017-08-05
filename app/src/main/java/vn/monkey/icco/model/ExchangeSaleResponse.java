package vn.monkey.icco.model;

import java.util.List;

/**
 * Created by Mlwayz on 6/30/2017.
 */

public class ExchangeSaleResponse extends BaseResponse {

    public Data data;

    public class Data {
        public List<Item> items;
    }

    public class Item {
        public Long id;
        public Long subscriber_id;
        public String location;
        public String location_name;
        public Long created_at;
        public Long updated_at;
        public Integer price;
        public String sold;
        public String coffee;
        public String total_quantity;
        public String subscriber_name;
    }
}
