package tbc.uncagedmist.newgames.Model;

public class Rating {

    private String imageId;
    private String rateValue;

    public Rating() {
    }

    public Rating(String imageId, String rateValue) {
        this.imageId = imageId;
        this.rateValue = rateValue;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }
}