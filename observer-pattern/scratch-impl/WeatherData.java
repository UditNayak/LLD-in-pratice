import java.util.ArrayList;
import java.util.List;

public class WeatherData implements ISubject {
    private final List<IObserver> observers;
    private float temperature;
    private float humidity;
    private float pressure;

    public WeatherData() {
        this.observers = new ArrayList<>();
    }
    @Override
    public void registerObserver(IObserver o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(IObserver o) {
        int idx = -1;
        for (int i=0; i<observers.size(); i++) {
            if (observers.get(i) == o) {
                idx = i;
                break;
            }
        }
        observers.remove(idx);
    }

    @Override
    public void notifyObservers() {
        for (IObserver observer: observers) {
            observer.update(temperature, humidity, pressure);
        }
    }

    public void measurementsChanged() {
        notifyObservers();
    }

    public void setMeasurements(float newTemperature, float newHumidity, float newPressure) {
        this.temperature = newTemperature;
        this.humidity = newHumidity;
        this.pressure = newPressure;
        measurementsChanged();
    }
    
}
