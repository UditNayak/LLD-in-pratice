# State Pattern
**The State Pattern** allows an object to change its behavior when its internal state changes. The object will appear to change its class.

## State Pattern UML
![State Pattern UML](https://drive.google.com/uc?export=view&id=1Q37_j2HuoCDiO6FvVSb90EOxQq1NSkQh)

## Real Life Analogy

#### Example1: Vending Machine / Gumball Machine
- **States**: No Coin, Has Coin, Sold, Sold Out
- Behavior changes based on state:
  - If no coin, it won't dispense.
  - If has coin, it waits for the crank to be turned.
  - If sold, it dispenses the gumball.
  - If sold out, it rejects the coin.

#### Example2: Traffic Light System
- **States**: Red, Green, Yellow
- Behavior changes based on state:
  - Red means stop.
  - Green means go.
  - Yellow means slow down or prepare to stop.

#### Example3: ATM Machine
- **States**: No Card, Has Card, Authenticated, Out of Service
- Behavior changes based on state:
    - If no card, it won't accept any operations.
    - If has card, it waits for PIN entry.
    - If authenticated, it allows transactions.
    - If out of service, it rejects all operations.

#### Example4: Media Player
- **States**: Playing, Paused, Stopped
- Behavior changes based on state:
    - If playing, it plays the media.
    - If paused, it stops playback but retains the position.
    - If stopped, it resets the position to the beginning.

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
