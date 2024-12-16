package com.jms.calidaddeaire;

public class HistoryItem {
    public String timestamp;
    public String temperature;
    public String humidity;
    public String co2;
    public String status;

    public HistoryItem(String timestamp, String temperature, String humidity, String co2, String status) {
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.co2 = co2;
        this.status = status;
    }
}
