package me.kirillirik;

public final class Main {

//        System.out.printf("Mean x, y:    %.4f, %.4f%n", mean(offsetPoints, true), mean(offsetPoints, false));
//        System.out.printf("Std dev x, y: %.4f, %.4f%n", standardDeviation(offsetPoints, true), standardDeviation(offsetPoints, false));

//    private double mean(Collection<HopperExperiment.Vector2> points, boolean x) {
//        double sum = 0;
//        for (final HopperExperiment.Vector2 point : points) {
//            sum += x ? point.x : point.y;
//        }
//
//        return sum / points.size();
//    }
//
//    private double standardDeviation(Collection<HopperExperiment.Vector2> points, boolean x) {
//        final double m = mean(points, x);
//        double sum = 0;
//        for (final HopperExperiment.Vector2 point : points) {
//            sum += Math.pow((x ? point.x : point.y) - m, 2);
//        }
//
//        return Math.sqrt(sum / points.size());
//    }

//    public static final double MAXIMUM_SPREAD = 100;
//
//    public static double nextDouble(double min, double max) {
//        final var random = new Random();
//        return (max - min) * random.nextDouble() + min;
//    }

//            while (true) {
//                final double x = nextDouble(-MAXIMUM_SPREAD, MAXIMUM_SPREAD);
//                final double y = nextDouble(-MAXIMUM_SPREAD, MAXIMUM_SPREAD);
//
//                final double distance = Math.sqrt(Math.pow(0 - x, 2) + Math.pow(0 - y, 2));
//
//                if (nextDouble(0.0D, 1.0D) < Math.pow(distance, (1 + distance / MAXIMUM_SPREAD)) / MAXIMUM_SPREAD) {
//                    continue;
//                }
//
//                points.add(new Vector2(x, y));
//                break;
//            }

    public static void main(String[] args) {
        new HopperExperiment();
    }
}