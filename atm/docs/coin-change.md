# Coin Change - LeetCode 322

## Problem Statement
You are given an integer array `coins` representing coins of different denominations and an integer `amount` representing a total amount of money. 

Return the fewest number of coins that you need to make up that amount. If that amount of money cannot be made up by any combination of the coins, return `-1`. 

You may assume that you have an infinite number of each kind of coin.

### Example 1:
```
Input: coins = [1,2,5], amount = 11
Output: 3
Explanation: 11 = 5 + 5 + 1
```

### Example 2:
```
Input: coins = [2], amount = 3
Output: -1
```

### Example 3:
```
Input: coins = [1], amount = 0
Output: 0
```

### Constraints:
- `1 <= coins.length <= 12`
- `1 <= coins[i] <= 2^31 - 1`
- `0 <= amount <= 10^4`


## Greedy Approach
The first approach that comes to mind is a greedy one. We can sort the coins in descending order and keep picking the largest denomination until we reach the target amount. However, this approach does not always yield the optimal solution.

For example, consider the coins `[1, 3, 4]` and the amount `6`. The greedy approach would pick `4` first, then `1`, and then another `1`, resulting in a total of `3` coins. However, the optimal solution is to use two `3` coins.

## DP on Subsequences / Take or Not Take
- Trying out all cmbinations to form Target : `Knapsack`
- Take the combination with minimum coins

```java
class Solution {
    public int coinChange(int[] coins, int amount) {
        int[][] dp = new int[coins.length][amount + 1];
        for (int[] row : dp) {
            Arrays.fill(row, -1);
        }

        int result = coinChangeHelper(0, amount, coins, dp);
        return result == Integer.MAX_VALUE ? -1 : result;
    }

    private int coinChangeHelper(int index, int target, int[] coins, int[][] dp) {
        if (target == 0) return 0; // No coins needed when target amount is reached
        if (index == coins.length) return Integer.MAX_VALUE; // Return MAX_VALUE to signal no valid solution from this path

        if (dp[index][target] != -1) return dp[index][target];

        int not_take = coinChangeHelper(index + 1, target, coins, dp);

        int take = Integer.MAX_VALUE;
        if (coins[index] <= target) {
            int subResult = coinChangeHelper(index, target - coins[index], coins, dp);
            if (subResult != Integer.MAX_VALUE) {
                take = 1 + subResult;
            }
        }

        return dp[index][target] = Math.min(take, not_take);
    }
}
```