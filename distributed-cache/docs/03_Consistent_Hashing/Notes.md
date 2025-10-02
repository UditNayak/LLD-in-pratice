# Consistent Hashing - Notes

## What is a Good Hash Function?
A good hash function should have the following properties:
- Even distribution (avoiding hot spots)
- Low collision probability
- Fast computation (especially important at scale)

## Common Hash Functions
| Algorithm               | Output Size         | Type              | Comments                                                                                                                                        |
| ----------------------- | ------------------- | ----------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| **MD5**                 | 128 bits (16 bytes) | Cryptographic     | Very commonly used in consistent hashing. Fast, good distribution. Deprecated for security, but still fine for hashing (non-security) purposes. |
| **SHA-1**               | 160 bits (20 bytes) | Cryptographic     | Better collision resistance than MD5. Slightly slower. Also deprecated for security.                                                            |
| **SHA-256**             | 256 bits (32 bytes) | Cryptographic     | Very secure, but overkill and slower for consistent hashing use cases.                                                                          |
| **MurmurHash**          | 32 or 128 bits      | Non-cryptographic | Extremely fast, great distribution, often used in large-scale systems (like Cassandra, Elasticsearch).                                          |
| **CityHash / FarmHash** | 64 to 128 bits      | Non-cryptographic | Google’s fast hash family. High performance and good distribution. Suitable for high-throughput applications.                                   |

### Which one should you choose? (General Guidelines)
- **Use MD5** if you're starting out, learning, or want easy compatibility.
- Use **MurmurHash3 or CityHash** for real-world systems that need fast and balanced hashing.
- Avoid cryptographic hashes unless you need security — they are slower and unnecessary for consistent hashing.


## What data type should we use (`int`, `long`, `BigInteger`) to store hash values for consistent hashing?

To decide the appropriate data type for storing hash values in consistent hashing, we must consider two things:

### 1. Range of Common Hash Functions

| Hash Algorithm | Output Bits | Range (Unsigned)                   |
| -------------- | ----------- | ---------------------------------- |
| MD5            | 128 bits    | `0` to `2^128 - 1`                 |
| SHA-1          | 160 bits    | `0` to `2^160 - 1`                 |
| SHA-256        | 256 bits    | `0` to `2^256 - 1`                 |
| MurmurHash3    | 32 or 128   | `0` to `2^32 - 1` (or `2^128 - 1`) |
| CityHash       | 64 or 128   | `0` to `2^64 - 1` (or `2^128 - 1`) |

### 2. Range of Java Data Types

| Java Type    | Bits | Range                                                                |
| ------------ | ---- | -------------------------------------------------------------------- |
| `int`        | 32   | `-2^31` to `2^31 - 1` (`-2,147,483,648` to `2,147,483,647`)          |
| `long`       | 64   | `-2^63` to `2^63 - 1` (`-9.2 quintillion` to `9.2 quintillion`)      |
| `BigInteger` | ∞    | Arbitrary precision – can store the full output of any hash function |

> Note: Java integers are signed, but we often mask with 0xFF or use unsigned comparisons to interpret the bits as positive numbers when needed.

### So, which one should we use?

| Use Case                                    | Recommended Type | Reason                                             |
| ------------------------------------------- | ---------------- | -------------------------------------------------- |
| Simple / Small-scale ring (learning, demos) | `int`            | Easy to handle, enough for small number of nodes   |
| Medium to large rings (real systems)        | `long`           | Provides 64-bit space – large enough for most apps |
| Very high precision (full MD5/SHA-1 output) | `BigInteger`     | Can hold 128/160/256-bit hashes without truncation |


### Practical Tip:

Even if you're using a hash function like **MD5 (128 bits)**, in most consistent hashing implementations, you **don’t need to use the full hash**. You can safely extract **4 or 8 bytes (32 or 64 bits)** and convert them into an `int` or `long`.

Example:
```java
// Convert first 4 bytes to a 32-bit int
int hash32 = ((digest[3] & 0xFF) << 24) |
             ((digest[2] & 0xFF) << 16) |
             ((digest[1] & 0xFF) << 8) |
             (digest[0] & 0xFF);

// Convert first 8 bytes to a 64-bit long
long hash64 = 0;
for (int i = 0; i < 8; i++) {
    hash64 |= ((long)(digest[i] & 0xFF)) << (8 * i);
}
```

This helps you **balance performance and simplicity**, especially since most ring sizes don’t require more than 64-bit precision.

# Implementing Hash Functions in Java

## What Library Do We Use?
Java provides built-in support for cryptographic hash functions through the `java.security.MessageDigest` class.
- `MessageDigest` is a part of the standard Java Security API.
- It supports popular hash algorithms like **MD5, SHA-1, SHA-256**, and more.
- You get the hash as a **byte array** (called a digest), which you can then convert to an integer type as needed.

```java
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    private MessageDigest md5;
    private MessageDigest sha1;
    private MessageDigest sha256;
    // For non-cryptographic hashes like MurmurHash or CityHash, you would need external libraries

    public HashUtil() throws NoSuchAlgorithmException {
        md5 = MessageDigest.getInstance("MD5");
        sha1 = MessageDigest.getInstance("SHA-1");
        sha256 = MessageDigest.getInstance("SHA-256");
    }
}
```

## What is `getBytes()` in Java?
- `getBytes()` is a method of the `String` class.
- It converts the string into a **byte array** (`byte[]`) representing the string in a specific character encoding (by default, the platform’s charset, usually UTF-8).
- So `"hello".getBytes()` returns a byte array representing the ASCII/UTF-8 bytes of the characters `h`, `e`, `l`, `l`, `o`.

For `"hello"`, the bytes are:
| Character | ASCII Code (Decimal) | Byte (Hex) |
| --------- | -------------------- | ---------- |
| h         | 104                  | 0x68       |
| e         | 101                  | 0x65       |
| l         | 108                  | 0x6C       |
| l         | 108                  | 0x6C       |
| o         | 111                  | 0x6F       |

```java
        String key = "hello";
        byte[] bytes = key.getBytes();

        // Print the array reference (not useful)
        System.out.println(bytes); // prints something like [B@7ad041f3

        // Print actual byte values
        System.out.println(Arrays.toString(bytes)); // prints [104, 101, 108, 108, 111]

        // Or print as characters
        for (byte b : bytes) {
            System.out.print((char) b);
        }
        System.out.println();
```


## Understanding `byte[] digest` in Hashing
When you generate a hash using `MessageDigest`, the output you get is a **byte array**, commonly called the **digest**.

### What is a Byte Array (`byte[]`)?
- A byte array is a sequence of bytes (`byte` is an 8-bit signed integer in Java).
- Each element in the array stores a value between -128 and 127.
- In the context of hashing, the byte array represents the raw binary output of the hash function.

### Why a byte array for hashes?
- Hash functions produce fixed-length binary outputs (e.g., MD5 produces 128 bits = 16 bytes).
- These outputs are not characters or strings but raw binary data.
- The byte array is a convenient way to store and process this binary data in Java.

#### Example:
```java
        String key = "hello";

        final MessageDigest md = MessageDigest.getInstance("MD5");
        md.reset();

        byte[] keyBytes = key.getBytes();
        md.update(keyBytes);

        byte[] digest = md.digest();
        System.out.println("Digest length: " + digest.length); // Prints 16

        // Print bytes as signed integers
        for (byte b : digest) {
            System.out.print(b + " ");
        }
        System.out.println();
```

**Note**: Bytes are signed in Java (range -128 to 127), so some values might look negative when printed. But they really represent unsigned values between 0 and 255 in binary.

## Understanding `0xFF` and Why We Use It (Bitmasking)

### What is `0xFF`?
- `0xFF` is a **hexadecimal literal** in Java representing the decimal number 255.
- In binary, `0xFF` = `11111111` (8 bits all set to 1).

### Why do we use `& 0xFF` on bytes?
Since Java bytes are signed (`-128` to `127`), when converting a `byte` to a larger type like `int` or `long`, **sign extension** happens automatically. This means:
- If the byte’s highest bit is 1 (meaning negative), Java will fill the higher bits of the new int/long with 1s to preserve the sign.
- This is NOT what we want when dealing with raw binary data like hashes.

Using `& 0xFF` masks the byte so that:
- Only the last 8 bits are kept.
- It effectively converts the signed byte to an unsigned integer between 0 and 255.
- Prevents sign extension when promoting to larger types.

#### Example Without `& 0xFF`
```java
byte b = (byte) 0xAB; // 0xAB = 171 decimal, but byte will be -85 in Java (signed)

int withoutMask = b; 
System.out.println(withoutMask); // Prints -85 (sign extended)
```

#### Example With `& 0xFF`
```java
byte b = (byte) 0xAB;

int withMask = b & 0xFF;
System.out.println(withMask); // Prints 171 (correct unsigned value)
```

### Why is this important in hashing?
When you combine bytes into an int or long hash value:
- You want each byte’s value as an unsigned number (0 to 255).
- If you don’t mask, the sign extension can cause incorrect results.
- Masking ensures the hash value is built correctly from the original binary digest.


## Generating a 32-bit Integer Hash from MD5
```java
public int hashToInt(String key) {
    md.reset(); // Reset MessageDigest for new hash
    md.update(key.getBytes()); // Feed the input bytes to MD5
    byte[] digest = md.digest(); // Calculate MD5 digest (16 bytes)

    // Extract first 4 bytes of digest and combine into an int
    int hash = ((digest[3] & 0xFF) << 24) |  // highest byte
                ((digest[2] & 0xFF) << 16) |
                ((digest[1] & 0xFF) << 8)  |
                (digest[0] & 0xFF);          // lowest byte
    
    return hash;
}
```

#### Explanation:
- `md.reset()` clears any previous state in the `MessageDigest` instance to prepare for a new hash calculation.
- `md.update(key.getBytes())` converts the string key into bytes and updates the digest with it.
- `byte[] digest = md.digest()` completes the hash computation and returns the 16-byte/128-bit MD5 hash.
- Combining 4 bytes:
    - `digest[0]` to `digest[3]` are bytes from the MD5 hash.
    - Each byte is masked with 0xFF to ensure it's treated as unsigned (0-255).
    - These bytes are shifted and combined into a 32-bit int.

## Generating a 64-bit Long Hash from MD5
```java
public long hashToLong(String key) {
    md.reset();
    md.update(key.getBytes());
    byte[] digest = md.digest();

    long hash = 0;
    // Combine first 8 bytes of the digest into a 64-bit long
    for (int i = 0; i < 8; i++) {
        hash |= ((long)(digest[i] & 0xFF)) << (8 * i);
    }
    return hash;
}
```

#### Explanation:
- Same process as before: reset, update with key bytes, compute MD5 digest.
- Loop through first 8 bytes of the digest.
- For each byte, mask with `0xFF` and cast to long to prevent sign extension.
- Shift the byte left by `8 * i` bits to place it correctly in the 64-bit long.
- Use bitwise OR `|=` to accumulate the bytes into a single long value.

## Generating a Full MD5 Hash as a BigInteger
Since MD5 produces a 128-bit (16-byte) hash, sometimes you want to store or work with the entire hash value without losing precision by truncating to int or long. Java’s BigInteger class can hold arbitrarily large integers, making it a perfect fit.

```java
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    private MessageDigest md;

    public HashUtil() throws NoSuchAlgorithmException {
        md = MessageDigest.getInstance("MD5");
    }

    /**
     * Returns the full MD5 hash as a positive BigInteger.
     * 
     * @param key the string to hash
     * @return BigInteger representing the full 128-bit MD5 hash
     */
    public BigInteger hashToBigInteger(String key) {
        md.reset();
        md.update(key.getBytes());
        byte[] digest = md.digest();

        // Convert to positive BigInteger (use 1 as signum)
        return new BigInteger(1, digest);
    }

    public static void main(String[] args) throws Exception {
        HashUtil util = new HashUtil();
        String key = "hello";

        BigInteger bigHash = util.hashToBigInteger(key);
        System.out.println("MD5 hash as BigInteger: " + bigHash.toString());
        System.out.println("MD5 hash as hex: " + bigHash.toString(16)); // hex representation
    }
}
```

### Explanation:
- `new BigInteger(1, digest)`: The `1` here means the number is positive (signum = 1). This is important because by default, `BigInteger` treats the input bytes as a signed number and may interpret the hash as negative if the highest bit is set.
- `digest` is the full 16-byte MD5 hash.
- The resulting `BigInteger` holds the entire 128-bit hash value exactly.

#### Output Example
For the string "hello", you might get something like:
```
MD5 hash as BigInteger: 90706087009812418299129074514289793218
MD5 hash as hex: 5d41402abc4b2a76b9719d911017c592
```