public class StatisticsDisplay implements IObserver, IDisplayElement {
    private float maxTemp = 0.0f;
    private float minTemp = 200;
    private float tempSum=0.0f;
    private int numReadings;
    private ISubject weatherData;

    public StatisticsDisplay(ISubject weatherData) {
        this.weatherData = weatherData;
        weatherData.registerObserver(this);
    }

    @Override
    public void update(float temperature, float humidity, float pressure) {
        tempSum += temperature;
        numReadings++;

        if(temperature > maxTemp) {
            maxTemp = temperature;
        }

        if(temperature < minTemp) {
            minTemp = temperature;
        }

        display();
    }

    @Override
    public void display() {
        System.out.println("Avg/Max/Min temperature = " + (tempSum / numReadings)
                + "/" + maxTemp + "/" + minTemp);
    }
}