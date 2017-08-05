package vn.monkey.icco.model;

/**
 * Created by Mlwayz on 7/3/2017.
 */

public class QuestionAnswerDetailResponse extends BaseResponse {

    public Data data;

    public class Data {
        public Long id;
        public String question;
        public String answer;
        public Long created_at;
        public Long updated_at;
        public Integer status;
        public String image;
    }
}
