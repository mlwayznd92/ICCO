package vn.monkey.icco.model;

import java.util.List;

/**
 * Created by Mlwayz on 5/29/2017.
 */

public class NewsResponse extends BaseResponse {


    public Data data;

    public class Data {
        public List<Item> items;

        public class Item {
            public Long id;
            public Long category_id;
            public String title;
            public String short_description;
            public String description;
            public String content;
            public Long created_at;
            public Long updated_at;
            public Integer status;
            public String image;
        }
    }
}
