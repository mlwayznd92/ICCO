package vn.monkey.icco.model;

import java.util.List;

/**
 * Created by Mlwayz on 7/1/2017.
 */

public class TermResponse extends BaseResponse {

    public Data data;

    public class Data {
        public List<Item> items;
    }

    public class Item {
        public Long id;
        public String term;
        public Long created_at;
        public Long updated_at;
    }
}
