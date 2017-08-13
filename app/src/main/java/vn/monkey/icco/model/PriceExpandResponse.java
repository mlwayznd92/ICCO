package vn.monkey.icco.model;

import java.util.List;

/**
 * Created by Mlwayz on 6/18/2017.
 */

public class PriceExpandResponse extends BaseResponse {

    public List<Item> data;

    public class Item {
        public String province_name;
        public List<PriceItem> price;
    }

    public class PriceItem {
        public Long id;
        public String province_name;
        public String organisation_name;
        public Integer price_average;
        public String unit;
        public Long created_at;
        public Long coffee_old_id;
        public TypeCoffee type_coffee;
    }

    public class TypeCoffee {
        public String name_coffee;
        public String company;
    }
}
