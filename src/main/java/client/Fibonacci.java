package client;

import compute.Task;

import java.io.Serializable;
import java.math.BigInteger;

public class Fibonacci implements Task<BigInteger>, Serializable {
    private final int n;

    public Fibonacci(int n) {
        this.n = n;
    }

    @Override
    public BigInteger execute() {
        return computeFibonacci(this.n);
    }

    public static BigInteger computeFibonacci(int n) {
        BigInteger prepre = new BigInteger("0");
        BigInteger pre = new BigInteger("1");
        BigInteger out = new BigInteger("1");

        if (n >= 2) {
            for (int i = 3; i <= n; i++) {
                prepre = pre;
                pre = out;
                out = prepre.add(pre);
            }
            return out;
        } else if (n >= 0) {
            return BigInteger.valueOf(n);
        } else {
            return prepre;
        }
    }
}
