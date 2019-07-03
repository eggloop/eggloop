package io.github.eggloop.stl.syntax;

import io.github.eggloop.expression.relational.DomainFunction;
import io.github.eggloop.stl.visitor.FormulaVisitor;

import java.util.function.Supplier;

public class Historically implements Formula {

    private Interval interval;
    private Formula argument;

    public Historically(Interval interval, Formula argument) {
        this.interval = interval;
        this.argument = argument;
    }

    @Override
    public <T> DomainFunction<T> accept(FormulaVisitor<T> visitor) {
        return  visitor.visit(this);
    }

    public Interval getInterval() {
        return interval;
    }

    public Formula getArgument() {
        return argument;
    }
}
