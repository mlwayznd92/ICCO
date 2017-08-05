package vn.monkey.icco.model;

/**
 * Created by Mlwayz on 7/1/2017.
 */

public class GAPDetailResponse extends BaseResponse {

    public Data data;

    public class Data {
        public Long id;
        public String gap;
        public Integer status;
        public Long created_at;
        public Long updated_at;
        public String title;
    }
}
