package car.engine.processor;

import car.engine.util.Pair;

public abstract class Engine {

    EngineOptions opts;

    Engine(EngineOptions opts) {
        this.opts = opts;
    }

    public abstract Pair<String, String> detect() throws Exception;

    public abstract void process() throws Exception;

}
