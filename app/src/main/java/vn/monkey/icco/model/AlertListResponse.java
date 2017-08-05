package vn.monkey.icco.model;

import java.util.List;

/**
 * Created by ho2ng on 6/8/2016.
 */
public class AlertListResponse {
    public int total;
    public int page;
    public int page_size;
    public List<Item> items;

    public class Item {
        public String image_url;
        public String label;
        public String description;
        public String click_value;
        public int click_type;
    }



}
