package vn.monkey.icco.model;

/**
 * Created by hoang on 6/29/2016.
 */
public class ImageSlideResponse {
    public String image_url;
    public String label;
    public String description;
    public String click_value;
    public int click_type;

    public static int TYPE_NOTHING = 0;
    public static int TYPE_PRODUCT = 1;
    public static int TYPE_CATEGORY = 2;
    public static int TYPE_BRAND = 3;
    public static int TYPE_LINK = 4;


}
