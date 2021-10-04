package org.efac.common;

import java.math.BigInteger;

public class Utility {
    public static BigInteger factorial(int n) {
        if (n == 0) {
            return new BigInteger("1");
        }
        
        return innerFactorial(n);
    }

    private static BigInteger innerFactorial(Integer m) {
        return m == 1 ? new BigInteger("1") : innerFactorial(m - 1).multiply(new BigInteger(m.toString()));
    }
}
