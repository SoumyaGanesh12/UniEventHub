package unieventhub.util;

public class StudentCardValidator {
    public static boolean isValid(long number) {
        int length = getLength(number);
        if (length < 13 || length > 16) return false;

        if (!prefixMatched(number, 4) &&
            !prefixMatched(number, 5) &&
            !prefixMatched(number, 37) &&
            !prefixMatched(number, 6)) return false;

        int sum = sumOfDoubleEvenPlace(number) + sumOfOddPlace(number);
        return (sum % 10 == 0);
    }

    public static int sumOfDoubleEvenPlace(long number) {
        int sum = 0;
        String numStr = Long.toString(number);
        for (int i = numStr.length() - 2; i >= 0; i -= 2) {
            int digit = Character.getNumericValue(numStr.charAt(i));
            sum += getDigit(digit * 2);
        }
        return sum;
    }

    public static int getDigit(int number) {
        return number < 10 ? number : number / 10 + number % 10;
    }

    public static int sumOfOddPlace(long number) {
        int sum = 0;
        String numStr = Long.toString(number);
        for (int i = numStr.length() - 1; i >= 0; i -= 2) {
            sum += Character.getNumericValue(numStr.charAt(i));
        }
        return sum;
    }

    public static boolean prefixMatched(long number, int d) {
        int prefixSize = getLength(d);
        long prefix = getPrefix(number, prefixSize);
        return prefix == d;
    }

    public static int getLength(long d) {
        return Long.toString(d).length();
    }

    public static long getPrefix(long number, int k) {
        String numStr = Long.toString(number);
        if (numStr.length() < k) return number;
        return Long.parseLong(numStr.substring(0, k));
    }
    
}