package vn.monkey.icco.model;

/**
 * Created by Mlwayz on 5/29/2017.
 */

public class NewsDetailResponse extends BaseResponse {


    public Data data;

    public class Data {
        public Long id;
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
