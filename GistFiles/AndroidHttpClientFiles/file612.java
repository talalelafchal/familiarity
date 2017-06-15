public class MathOp {
    public MathOp() {
    }

    public int fac(int a) {

        int n = a;
        int result = 1;
        for (int i = 2; i <= n; i++)
            result *= i;
        return result;
    }
}