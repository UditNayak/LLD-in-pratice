# Luhn Algorithm Explained

The **Luhn Algorithm** (also known as the "modulus 10" or "mod 10" algorithm) is a simple checksum formula used to validate various identification numbers, most commonly credit card numbers.

## Purpose

The algorithm helps detect simple errors in numbers, such as mistyped digits, by validating whether the number satisfies a checksum condition.

## Step-by-step Explanation

Let's walk through the algorithm with an example card number:

Example Card Number: `4539 1488 0343 6467`

Remove the spaces: `4539148803436467`


### Step 1: Starting from the rightmost digit (check digit), move left.

You will process each digit, but **double every second digit** starting from the second-to-last digit (right to left).

Digits (right to left): `7, 6, 4, 3, 0, 8, 8, 4, 1, 9, 3, 5, 4`


Double every second digit (positions 2, 4, 6, etc.):

| Position | Digit | Double | After subtracting 9 if >9 |
|----------|-------|--------|---------------------------|
| 2        | 6     | 12     | 3 (12 - 9)                |
| 4        | 6     | 12     | 3                         |
| 6        | 4     | 8      | 8                         |
| 8        | 0     | 0      | 0                         |
| 10       | 8     | 16     | 7                         |
| 12       | 1     | 2      | 2                         |
| 14       | 3     | 6      | 6                         |
| 16       | 4     | 8      | 8                         |

---

### Step 2: Sum all digits (after doubling and adjusting) including the untouched digits

- Sum of doubled digits after adjustments:  
  3 + 3 + 8 + 0 + 7 + 2 + 6 + 8 = **37**

- Sum of untouched digits:  
  7 + 4 + 3 + 3 + 8 + 4 + 9 + 5 = **43**

- Total sum:  
  37 + 43 = **80**


### Step 3: Check if the total sum is divisible by 10

Since 80 % 10 = 0, the card number **passes the Luhn check** and is considered valid.


## Summary of the algorithm:

1. From right to left, double every second digit.
2. Subtract 9 from any result higher than 9.
3. Sum all the digits.
4. If the total is divisible by 10, the number is valid.



## Code Example (Java)

```java
public static boolean isValidCardNumber(String cardNumber) {
    String digitsOnly = cardNumber.replaceAll("\\D", "");
    int sum = 0;
    boolean alternate = false;

    for (int i = digitsOnly.length() - 1; i >= 0; i--) {
        int n = Integer.parseInt(digitsOnly.substring(i, i + 1));
        if (alternate) {
            n *= 2;
            if (n > 9) {
                n -= 9;
            }
        }
        sum += n;
        alternate = !alternate;
    }
    return sum % 10 == 0;
}
```

## Test Cases

| Card Number          | Validity  |
|----------------------|-----------|
| 4539 1488 0343 6467  | Valid     |
| 4539 1488 0343 6468  | Invalid   |
| 1234 5678 9012 3456  | Invalid   |

## References
- [Luhn Algorithm - Wikipedia](https://en.wikipedia.org/wiki/Luhn_algorithm)
- [How to Implement the Luhn Algorithm](https://www.geeksforgeeks.org/luhn-algorithm/)
- [How Credit Card Validation Works](https://www.freeformatter.com/credit-card-number-generator-validator.html)