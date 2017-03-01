package sensorPackage;

/*
 * 
 */


/**
 *
 * @author Yatin Rehani
 * Date 10-28-2016
 * This class is a POJO to save Sensor objects
 */
public class Sensor {
    
    private String sensorId;
    private String timeStamp;
    private String type;
    private String temperature;

    public Sensor(String sensorId, String timeStamp, String type, String temperature) {
        this.sensorId = sensorId;
        this.timeStamp = timeStamp;
        this.type = type;
        this.temperature = temperature;
    }
    
    

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getType() {
        return type;
    }

    public String getTemperature() {
        return temperature;
    }
    
    
    
}
