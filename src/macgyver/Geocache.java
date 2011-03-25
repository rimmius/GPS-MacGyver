package macgyver;


public class Geocache {

    /**
     * @param Sorush Arefipour
     */
    private String name;
    private double latitude;
    private double longitude;
    private String type;
    private String link;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }


}