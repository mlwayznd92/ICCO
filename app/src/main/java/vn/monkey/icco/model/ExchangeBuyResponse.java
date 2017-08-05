package vn.monkey.icco.model;

import java.util.List;

/**
 * Created by Mlwayz on 6/30/2017.
 */

public class ExchangeBuyResponse extends BaseResponse {

    public Data data;

    public class Data {
        public List<Item> items;
    }

    public class Item {
        public Long id;
        public Long subscriber_id;
        public Long created_at;
        public Long updated_at;
        public String price;
        public String coffee;
        public String total_quantity;
        public String subscriber_name;
        public String location;
        public String location_name;
    }
}
