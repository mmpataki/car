package car.engine.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Pair<T1, T2> {

    T1 first;
    T2 second;

    public Pair(T1 t1, T2 t2) {
        first = t1;
        second = t2;
    }

    public static Pair<Object, Object> of(Object t1, Object t2) {
        return Pair.builder().first(t1).second(t2).build();
    }

}
