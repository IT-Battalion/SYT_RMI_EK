package client;

import compute.Task;

import java.io.Serializable;

public class Fibonacci implements Task<Long>, Serializable
{
    private final int n;

    public Fibonacci(int n) {
        this.n = n;
    }

    @Override
    public Long execute() {
        return computeFibonacci(this.n);
    }

    public static Long computeFibonacci(int n) {
        long n2 = 0;
        long n1 = 1;
        long tmp;
        for (int i = n; i >= 2; i--) {
            tmp = n2;
            n2 = n1;
            n1 = n1 + tmp;
        }
        return n2 + n1;
    }
}
