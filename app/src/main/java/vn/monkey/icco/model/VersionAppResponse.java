package vn.monkey.icco.model;

/**
 * Created by mlwayz on 9/7/17.
 */

public class VersionAppResponse extends BaseResponse {

    public Data data;

    public class Data {
        public Item items;
    }

    public class Item {
        public Integer id;
        public Integer type;
        public String version;
        public String link;
    }
}
