package car.engine.query;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
public abstract class Op {

    List<Op> operands = new ArrayList<>();

    Object value;

    public List<Op> getOperands() {
        return operands;
    }

    public Op with(Object... operands) {
        for (Object operand : operands) {
            if (operand instanceof Op)
                this.operands.add((Op) operand);
            else
                this.operands.add(new ValOp(operand));
        }
        return this;
    }

    static Map<String, Function<Void, Op>> operators = new HashMap<String, Function<Void, Op>>() {{
        put("and", v -> new AndOp());
        put("or", v -> new OrOp());
        put("not", v -> new NotOp());
        put("gt", v -> new GtOp());
        put("lt", v -> new LtOp());
        put("gte", v -> new GteOp());
        put("lte", v -> new LteOp());
        put("eq", v -> new EqOp());
        put("ne", v -> new NeOp());
        put("in", v -> new InOp());
        put("nin", v -> new NinOp());
        put("exists", v -> new ExistsOp());
        put("mod", v -> new ModOp());
        put("regex", v -> new RegexOp());
        put("all", v -> new AllOp());
        put("join", v -> new JoinOp());
    }};

    public static Op makeOp(Object q) {

        if (List.class.isAssignableFrom(q.getClass())) {
            List qArr = (List) q;
            Object first = qArr.get(0);
            if (!(first instanceof String && ((String) first).startsWith("$"))) {
                return new ValOp(qArr);
            }
            String operator = (String) first;
            Op op = operators.get(operator.substring(1)).apply(null);
            for (int i = 1; i < qArr.size(); i++) {
                op.operands.add(makeOp(qArr.get(i)));
            }
            return op;
        } else {
            return new ValOp(q);
        }
    }

    /* supported classes are listed here */
    public static class ValOp extends Op {
        ValOp(Object val) {
            this.value = val;
        }
    }

    public static class JoinOp extends Op {
    }

    public static class AndOp extends Op {
    }

    public static class OrOp extends Op {
    }

    public static class NotOp extends Op {
    }

    public static class GtOp extends Op {
    }

    public static class LtOp extends Op {
    }

    public static class GteOp extends Op {
    }

    public static class LteOp extends Op {
    }

    public static class EqOp extends Op {
    }

    public static class NeOp extends Op {
    }

    public static class InOp extends Op {
    }

    public static class NinOp extends Op {
    }

    public static class ExistsOp extends Op {
    }

    public static class ModOp extends Op {
    }

    public static class RegexOp extends Op {
    }

    public static class AllOp extends Op {
    }

}
