public class ExpressionCounter {
    public void fun() {
        // 1 instanceof expression
        if (x instanceof String) {
            System.out.println(x);
        }

        // 3 binary expressions
        System.out.println(x + y + 1 + 2);

        // 2 unary expressions
        System.out.println(-(-x));
    }
}
