package vn.monkey.icco.model;

import android.support.annotation.NonNull;

/**
 * Created by Mlwayz on 6/18/2017.
 */

public class Price implements Comparable {

    private Long id;
    private String provinceName;
    private String organisationName;
    private Integer priceAverage;
    private String unit;
    private Long createdDate;
    private Long coffeeOldId;
    private String type;
    private String company;

    public Long getCoffeeOldId() {
        return coffeeOldId;
    }

    public void setCoffeeOldId(Long coffeeOldId) {
        this.coffeeOldId = coffeeOldId;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public Integer getPriceAverage() {
        return priceAverage;
    }

    public void setPriceAverage(Integer priceAverage) {
        this.priceAverage = priceAverage;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public int compareTo(@NonNull Object obj) {
        try {
            Long createDateObj = ((Price) obj).getCreatedDate();
            return this.getCreatedDate().compareTo(createDateObj);
        } catch (Exception ex) {
            return 0;
        }
    }
}
