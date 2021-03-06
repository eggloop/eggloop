package io.github.eggloop.expression.relational;

import io.github.eggloop.expression.arithmetic.ArithmeticExpression;

public interface Domain<T> {
    DomainFunction<T> lowerEqualTo(ArithmeticExpression left, ArithmeticExpression right);

    DomainFunction<T> lowerThan(ArithmeticExpression left, ArithmeticExpression right);

    DomainFunction<T> greaterEqualTo(ArithmeticExpression left, ArithmeticExpression right);

    DomainFunction<T> greaterThan(ArithmeticExpression left, ArithmeticExpression right);

    DomainFunction<T> equalTo(ArithmeticExpression left, ArithmeticExpression right);

    T conjunction(T left, T right);

    T disjunction(T left, T right);

    T negation(T argument);
}
