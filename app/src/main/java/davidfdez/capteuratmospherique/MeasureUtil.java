package davidfdez.capteuratmospherique;

public class MeasureUtil {

    static final int optimalCO2 = 400;
    static final int acceptableCO2 = 600;
    static final int maxCO2 = 850;

    static final int minLuminosity = 300;
    static final int comfortableLuminosity = 500;

    static double calculateMinTemperature(double humidity) {
        return -0.05 * (humidity / 100) + 21.5;
    }

    static double calculateOptimalTemperature(double humidity) {
        return -0.05 * (humidity / 100) + 27.5;
    }

    static double calculateMaxTemperature(double humidity) {
        return -0.075 * (humidity / 100) + 25.25;
    }

    static double calculateMinHumidity(double temperature) {
        double h = -20 * temperature + 430;
        return h < 0.3 ? 30 : h * 100;
    }

    static double calculateMaxHumidity(double temperature) {
        double h = -10 * temperature + 330;
        return h < 0.7 ? 70 : h * 100;
    }

    static double calculateMinColorTemperature(double lux) {
        return 1313.06 * Math.exp(-248.36 / lux) + 2338.54;
    }

    static double calculateMaxColorTemperature(double lux) {
        return 5.71 * lux + 2448.42;
    }

}