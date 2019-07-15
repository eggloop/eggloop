package io.github.eggloop.spider;

import io.github.eggloop.expression.arithmetic.Assignment;
import io.github.eggloop.expression.arithmetic.Variable;
import io.github.eggloop.expression.relational.GreaterEqualTo;
import io.github.eggloop.stl.syntax.*;
import io.github.eggloop.stl.visitor.BooleanTemporalMonitoring;
import io.github.eggloop.stl.visitor.FormulaPrinter;
import io.github.eggloop.stl.visitor.LogicOperatorToken;
import io.github.eggloop.trajectories.Trajectory;
import io.github.eggloop.trajectories.TrajectoryFactory;
import io.github.eggloop.utils.FileUtils;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class AllFormulas {

    private AllFormulas() {
        //utility class
    }

    public static void getTrajectory(String inputLocationOfTrajectories, String outputLocation) throws IOException, ParseException {
        String jsonTrajectory = FileUtils.readFileToString(inputLocationOfTrajectories);
        Trajectory trajectory = TrajectoryFactory.fromJSON(jsonTrajectory);
        double[] times = trajectory.getTimes();
        double[] space = IntStream.range(-1, 3).mapToDouble(s -> 0.1 * s).toArray();
        List<double[]> intervals = new ArrayList<>();
        for (int i = 0; i < times.length; i++) {
            double initTime = times[i];
            for (int j = i; j < times.length; j++) {
                intervals.add(new double[]{initTime, times[j]});
            }
        }
        BooleanTemporalMonitoring booleanTemporalMonitoring = new BooleanTemporalMonitoring(trajectory);
        FormulaPrinter formulaPrinter = new FormulaPrinter(new LogicOperatorToken());

        Formula eventuallyFormula = new Finally(new Interval(new Variable("a"), new Variable("b")), new Atom(new GreaterEqualTo(new Variable("X"), new Variable("k"))));
        Formula globallyFormula = new Globally(new Interval(new Variable("a"), new Variable("b")), new Atom(new GreaterEqualTo(new Variable("X"), new Variable("k"))));


        Function<double[], Assignment> ass = value -> {
            Assignment assignment = new Assignment();
            assignment.put("k", value[0]);
            assignment.put("a", value[1]);
            assignment.put("b", value[2]);
            return assignment;
        };


        Predicate<double[]> globallyChecker = value -> globallyFormula.accept(booleanTemporalMonitoring).evaluate(ass.apply(value));
        Predicate<double[]> eventuallyChecker = value -> eventuallyFormula.accept(booleanTemporalMonitoring).evaluate(ass.apply(value));
        StringBuilder stringBuilder = new StringBuilder();
        getString(stringBuilder, space, intervals, formulaPrinter, globallyFormula, ass, globallyChecker);
        getString(stringBuilder, space, intervals, formulaPrinter, eventuallyFormula, ass, eventuallyChecker);
        Path path = Paths.get(outputLocation);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(stringBuilder.toString());
        }
    }

    private static void getString(StringBuilder stringBuilder, double[] space, List<double[]> intervals, FormulaPrinter formulaPrinter, Formula provag, Function<double[], Assignment> ass, Predicate<double[]> eval) {
        Function<double[], String> printer = value -> provag.accept(formulaPrinter).evaluate(ass.apply(value));
        for (double[] interval : intervals) {
            for (double spaceValue : space) {
                double[] params = {spaceValue, interval[0], interval[1]};
                boolean test = eval.test(params);
                if(test) {
                    stringBuilder.append(printer.apply(params)).append("\n");
                }
            }
        }
    }

}
