package davidfdez.capteuratmospherique;

public class MeasureUtil {

    public static final int optimalCO2 = 400;
    public static final int acceptableCO2 = 600;
    public static final int maxCO2 = 850;

    public static final int minLuminosity = 300;
    public static final int comfortableLuminosity = 500;

    public static double calculateMinTemperature(double humidity) {
        return -0.05 * (humidity / 100) + 21.5;
    }

    public static double calculateOptimalTemperature(double humidity) {
        return -0.05 * (humidity / 100) + 27.5;
    }

    public static double calculateMaxTemperature(double humidity) {
        return -0.075 * (humidity / 100) + 25.25;
    }

    public static double calculateMinHumidity(double temperature) {
        double h = -20 * temperature + 430;
        return h < 0.3 ? 30 : h * 100;
    }

    public static double calculateMaxHumidity(double temperature) {
        double h = -10 * temperature + 330;
        return h < 0.7 ? 70 : h * 100;
    }

    public static double calculateMinColorTemperature(double lux) {
        return 1313.06 * Math.exp(-248.36 / lux) + 2338.54;
    }

    public static double calculateMaxColorTemperature(double lux) {
        return 5.71 * lux + 2448.42;
    }

    public static double calculatePerformance(double CO2, double temperature, double humidity, double luminosity, double color) {
        double performance = 100 - ecHumidity(temperature, humidity) - ecCO2(CO2) - ecTemperature(temperature, humidity) - ecColor(color, luminosity);
        if (performance > 100)
            return 100;
        else if (performance < 0)
            return 0;
        else
            return performance;
    }

    private static double ecColor(double color, double luminosity) {
        final double weighting = 2 / 50000;
        double colorMin = calculateMinColorTemperature(luminosity);
        double colorMax = calculateMaxColorTemperature(luminosity);

        if (color > colorMax)
            return (color - colorMax) * (color - colorMax) * weighting;
        else if (color < colorMin)
            return (color - colorMin) * (color - colorMin) * weighting;
        else
            return 0;
    }

    private static double ecTemperature(double temperature, double humidity) {
        final double weighting = 1 / 2;
        double tMin = calculateMinTemperature(humidity);
        double tMax = calculateMaxTemperature(humidity);

        if (temperature < tMin)
            return (temperature - tMin) * (temperature - tMin) * weighting;
        else if (temperature > tMax)
            return (temperature - tMax) * (temperature - tMax) * weighting;
        else
            return 0;
    }

    private static double ecCO2(double CO2) {
        final double weighting = 1 / 300000;

        if (CO2 == -1)
            return 0;
        else
            return (CO2 - maxCO2) * (CO2 - maxCO2) * weighting;
    }

    private static double ecHumidity(double temperature, double humidity) {
        final double weighting = 1 / 100;
        double hMin = calculateMaxHumidity(temperature);
        double hMax = calculateMinHumidity(temperature);

        if (humidity < hMin)
            return (humidity - hMax) * (humidity - hMax) * weighting;
        else if (humidity > hMax)
            return (humidity - hMax) * (humidity - hMax) * weighting;
        else
            return 0;
    }

}