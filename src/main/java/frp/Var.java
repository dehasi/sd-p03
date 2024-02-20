package frp;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

class Var {
    private final Function<Integer, Integer> transformation;
    private int value;

    private Var(int value) {
        this(value, null);
    }

    public Var(int value, Function<Integer, Integer> transformation) {
        this.value = value;
        this.transformation = transformation;
    }

    int value() {
        return this.value;
    }

    Var setValue(int value) {
        this.value = value;
        bounds.forEach(v -> v.update(this));
        return this;
    }

    private void update(Var var) {
        this.value = transformation.apply(var.value());
    }

    static Var of(int value) {
        return new Var(value);
    }

    public static Var bind(Var var, Function<Integer, Integer> transformation) {
        // var bound = new Var(transformation.apply(var.value), transformation);
        var bound = new Var(var.value, transformation);
        var.notify(bound);
        return bound;
    }

    private final List<Var> bounds = new ArrayList<>();

    private void notify(Var bound) {
        bounds.add(bound);
    }
}
