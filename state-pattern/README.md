# State Pattern
**The State Pattern** allows an object to change its behavior when its internal state changes. The object will appear to change its class.


## Pre-Requisites

### What is a GumBall Machine?
A gumball machine is a small vending machine that sells colorful candies (called gumballs).
- You put in a coin.
- Turn a handle (called a crank).
- A gumball (candy) rolls out for you.

### Key US Terms
- **Quarter** → A U.S. coin worth 25 cents (like inserting ₹5 or ₹10 in India).
- **Crank** → The handle/knob you turn after inserting a coin.
- **Dispense** → The action of releasing a gumball (the candy comes out).

### States in a Gumball Machine
The machine behaves differently depending on its current state:
1. **No Quarter (No Coin Inserted)**:
    - Waiting for you to insert a coin.
2. **Has Quarter (Coin Inserted)**:
    - You already paid, now it’s waiting for you to turn the crank.
3. **Gumball Sold (Dispensing)**:
    - You turned the crank, the machine is releasing a gumball.
4. **Out of Gumball (Empty)**:
    - Machine is sold out, can’t accept coins or give candy.

### Why this Example?
The gumball machine is a perfect real-world example because:
- It has **different states**.
- The same action (e.g., turning the crank) leads to **different behaviors** depending on the state.
- This directly maps to the State Pattern:
> “Allow an object to alter its behavior when its internal state changes.”
