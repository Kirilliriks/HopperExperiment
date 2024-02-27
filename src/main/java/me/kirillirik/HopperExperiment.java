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
        final List<Vector2> first = launchRule(1, points, (hopperVector, point) -> 0.0);

        // Перемешаем воронку в обратную сторону от стороны куда упал шарик на расстояние между упавшим шариком и мишенью
        // Относительно последнего положения воронки
        final List<Vector2> second =  launchRule(2, points, (hopperVector, point) -> -point);

        // Перемешаем воронку в обратную сторону от стороны куда упал шарик на расстояние между упавшим шариком и мишенью
        // Относительно центра мишени
        final List<Vector2> third = launchRule(3, points, (hopperVector, point) -> -(hopperVector + point));

        // Перемешаем воронку туда, где шарик был сброшен в прошлый раз [hopperVector + point]
        final List<Vector2> four = launchRule(4, points, Double::sum);

        generatePlot("all_in", first, second, third, four);
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

    private List<Vector2> launchRule(int ruleNumber, Collection<Vector2> points, BiFunction<Double, Double, Double> rule) {
        final List<Vector2> offsetPoints = makeOffsetsByRule(points, rule);

        generatePlot("rule_" + ruleNumber, offsetPoints);

        return offsetPoints;
    }

    @SafeVarargs
    private void generatePlot(String title, Collection<Vector2>... pointsArray) {
        double maxDistance = 0;
        double avgDistance = 0;
        double minDistance = Double.MAX_VALUE;

        final List<Plot.Data> plotsData = new ArrayList<>();

        int pointsSize = 0;
        for (final Collection<Vector2> points : pointsArray) {
            final Plot.Data data = Plot.data();
            plotsData.add(data);

            for (final Vector2 point : points) {
                data.xy(point.x, point.y);

                final double localDistance = Math.sqrt(Math.pow(-point.x, 2) + Math.pow(-point.y, 2));
                if (maxDistance < localDistance) {
                    maxDistance = localDistance;
                }

                if (minDistance > localDistance) {
                    minDistance = localDistance;
                }

                avgDistance += localDistance;

                pointsSize++;
            }
        }
        avgDistance /= pointsSize;


        final String str = "Макс " + FORMAT.format(maxDistance)
                + " Мин " + FORMAT.format(minDistance)
                + " Сред " + FORMAT.format(avgDistance);

        final Plot plot = Plot.plot(
                        Plot.plotOpts()
                                .title(title)
                                .legend(Plot.LegendFormat.BOTTOM)
                )
                .xAxis("x", Plot.axisOpts()
                        .range(-AREA_MAX_SIZE, AREA_MAX_SIZE)
                )
                .yAxis("y", Plot.axisOpts()
                        .range(-AREA_MAX_SIZE, AREA_MAX_SIZE)
                );

        final Queue<Color> colors = new ArrayDeque<>();
        colors.add(Color.GREEN);
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);

        int i = 1;
        for (final Plot.Data data : plotsData) {
            plot.series(
                    str + " (" + i++ + ")",
                    data,
                    Plot.seriesOpts()
                            .markerSize(10)
                            .line(Plot.Line.NONE)
                            .marker(Plot.Marker.CIRCLE)
                            .markerColor(colors.poll())
                            .color(Color.BLACK)
            );
        }

        try {
            plot.save(title, "png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
