package wildlogic.fishlog;

/**
 * Created by thatDude on 3/3/18.
 */
public class Record {
    private String name;
    private String latitude;
    private String longitude;
    private String lure;
    private String weather;
    private String species;
    private String time;
    private String temperature;
    private String user;
    private String path;
    private String hour;

    public Record(){
        name = "";
        latitude = "";
        longitude = "";
        lure = "";
        weather = "";
        species = "";
        time = "";
        temperature = "";
        user = "";
        path = "";

    }

    public Record(String name, String latitude, String longitude, String lure, String weather, String species, String time, String temperature, String user, String path){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lure = lure;
        this.weather = weather;
        this.species = species;
        this.time = time;
        this.temperature = temperature;
        this.user = user;
        this.path = path;
    }
    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLure() {
        return lure;
    }

    public void setLure(String lure) {
        this.lure = lure;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
