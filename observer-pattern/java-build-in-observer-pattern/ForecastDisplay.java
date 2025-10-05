import java.util.Observable;
import java.util.Observer;

public class ForecastDisplay implements Observer, IDisplayElement {
    private float currentPressure = 29.92f;  
    private float lastPressure;
    private final Observable observable;

    public ForecastDisplay(Observable observable) {
        this.observable = observable;
        observable.addObserver(this);
    }

    @Override
    public void update(Observable obs, Object arg) {
        if (arg instanceof WeatherData) {
            WeatherData weatherData = (WeatherData) arg;
            this.lastPressure = this.currentPressure;
            this.currentPressure = weatherData.getPressure();
            display();
        }
    }

    @Override
    public void display() {
        System.out.print("Forecast: ");
        if (currentPressure > lastPressure) {
            System.out.println("Improving weather on the way!");
        } else if (currentPressure == lastPressure) {
            System.out.println("More of the same");
        } else if (currentPressure < lastPressure) {
            System.out.println("Watch out for cooler, rainy weather");
        }
    }
}
