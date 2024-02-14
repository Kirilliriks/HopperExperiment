package me.kirillirik;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;

public final class HopperExperiment {

    public static final DecimalFormat FORMAT = new DecimalFormat("#0.00");
    public static final int LAUNCHES = 100;

    public static final double AREA_MAX_SIZE = 200;

    public static class Vector2 {
        public double x, y;

        public Vector2(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public HopperExperiment() {
        final List<Vector2> points = new ArrayList<>();

        final Random random = new Random();
        for (int i = 0; i < LAUNCHES; i++) {

            final double x = random.nextGaussian(0, 30);
            final double y = random.nextGaussian(0, 30);

            points.add(new Vector2(x, y));
            System.out.printf("Point[" + (i + 1) + "] x = %.2f : y = %.2f", x, y);
            System.out.println();
        }

        // Воронка чётко над мишенью, шарики падают там где и были сгенерированы
        launchRule(1, points, (hopperVector, point) -> 0.0);

        // Перемешаем воронку в обратную сторону от стороны куда упал шарик на расстояние между упавшим шариком и мишенью
        // Относительно последнего положения воронки
        launchRule(2, points, (hopperVector, point) -> -point);

        // Перемешаем воронку в обратную сторону от стороны куда упал шарик на расстояние между упавшим шариком и мишенью
        // Относительно центра мишени
        launchRule(3, points, (hopperVector, point) -> -(hopperVector + point));

        // Перемешаем воронку туда, где шарик был сброшен в прошлый раз [hopperVector + point]
        launchRule(4, points, Double::sum);
    }

    private List<Vector2> makeOffsetsByRule(Collection<Vector2> points, BiFunction<Double, Double, Double> hopperRule) {
        final List<Vector2> result = new ArrayList<>();

        double x = 0;
        double y = 0;

        for (final Vector2 point : points) {
            final double offsetX = x + point.x;
            final double offsetY = y + point.y;

            x = hopperRule.apply(x, point.x);
            y = hopperRule.apply(y, point.y);

            result.add(new Vector2(offsetX, offsetY));
        }

        return result;
    }

    private void launchRule(int ruleNumber, Collection<Vector2> points, BiFunction<Double, Double, Double> rule) {
        final List<Vector2> offsetPoints = makeOffsetsByRule(points, rule);

        final Plot.Data data = Plot.data();

        double maxDistance = 0;
        double avgDistance = 0;
        double minDistance = Double.MAX_VALUE;
        for (final Vector2 point : offsetPoints) {
            data.xy(point.x, point.y);

            final double localDistance = Math.sqrt(Math.pow(-point.x, 2) + Math.pow(-point.y, 2));
            if (maxDistance < localDistance) {
                maxDistance = localDistance;
            }

            if (minDistance > localDistance) {
                minDistance = localDistance;
            }

            avgDistance += localDistance;
        }

        avgDistance /= offsetPoints.size();


        final String str = "Макс " + FORMAT.format(maxDistance)
                + " Мин " + FORMAT.format(minDistance)
                + " Сред " + FORMAT.format(avgDistance);

        final Plot plot = Plot.plot(
                        Plot.plotOpts()
                                .title("Rule " + ruleNumber)
                                .legend(Plot.LegendFormat.BOTTOM)
                )
                .xAxis("x", Plot.axisOpts()
                        .range(-AREA_MAX_SIZE, AREA_MAX_SIZE)
                )
                .yAxis("y", Plot.axisOpts()
                        .range(-AREA_MAX_SIZE, AREA_MAX_SIZE)
                )
                .series(str, data,
                        Plot.seriesOpts()
                                .markerSize(15)
                                .line(Plot.Line.NONE)
                                .marker(Plot.Marker.CIRCLE)
                                .markerColor(Color.GREEN)
                                .color(Color.BLACK)
                );

        try {
            plot.save("rule_" + ruleNumber, "png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
