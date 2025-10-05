import java.util.Observable;

public class WeatherData extends Observable {
    private float temperature;
    private float humidity;
    private float pressure;

    public void measurementsChanged() {
        setChanged();       // changed = true
        notifyObservers();  // we are not sending any dataobject in notifyObservers method. Since we are using PULL model
    }

    public void setMeasurements(float newTemperature, float newHumidity, float newPressure) {
        this.temperature = newTemperature;
        this.humidity = newHumidity;
        this.pressure = newPressure;
        measurementsChanged();
    }

    // getters --> used in PULL model
    public float getTemperature() { return temperature; }
    public float getHumidity() { return humidity; }
    public float getPressure() { return pressure; }
}
