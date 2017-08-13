package vn.monkey.icco.model;

import java.util.List;

/**
 * Created by Mlwayz on 6/18/2017.
 */

public class PriceExpand {


    private String provinceName;
    private List<Price> prices;

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }


    public List<Price> getPrices() {
        return prices;
    }

    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }
}
