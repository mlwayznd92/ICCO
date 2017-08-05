package vn.monkey.icco.model;

import java.util.List;

/**
 * Created by Mlwayz on 6/26/2017.
 */

public class TypeCoffeeResponse extends BaseResponse {

    public Data data;

    public class Data {
        public List<Item> items;
    }

    public class Item {
        public Integer id;
        public String name;
    }
}
