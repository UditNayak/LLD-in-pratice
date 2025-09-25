# Naive Implementation of Gumball Machine

![Gumball Machine Workflow](https://drive.google.com/uc?export=view&id=1pgkI5bF_FegHdv89YNIhortvKp6LbafP)

### File Structure
```
state-pattern
  ├── src
  │   ├── GumballMachine.java              
  │   ├── NaiveTestDrive.java               // main class
  │   └── README.md                         // output of the program + explanation of the problem
  └── README.md
```

### Output

```
Mighty Gumball, Inc.
Java-enabled Standing Gumball Model #2004
Inventory: 5 gumballs
Machine is waiting for quarter

You inserted a quarter
You turned...
A gumball comes rolling out the slot

Mighty Gumball, Inc.
Java-enabled Standing Gumball Model #2004
Inventory: 4 gumballs
Machine is waiting for quarter

You inserted a quarter
Quarter returned
You turned but there's no quarter

Mighty Gumball, Inc.
Java-enabled Standing Gumball Model #2004
Inventory: 4 gumballs
Machine is waiting for quarter

You inserted a quarter
You turned...
A gumball comes rolling out the slot
You inserted a quarter
You turned...
A gumball comes rolling out the slot
You haven't inserted a quarter

Mighty Gumball, Inc.
Java-enabled Standing Gumball Model #2004
Inventory: 2 gumballs
Machine is waiting for quarter

You inserted a quarter
You can't insert another quarter
You turned...
A gumball comes rolling out the slot
You inserted a quarter
You turned...
A gumball comes rolling out the slot
Oops, out of gumballs!
You can't insert a quarter, the machine is sold out
You turned, but there are no gumballs

Mighty Gumball, Inc.
Java-enabled Standing Gumball Model #2004
Inventory: 0 gumballs
Machine is sold out
```

### Problem: Not following OCP
Suppose we want to add a new feature.

**New Feature:** 
- 10% of the time, when the user turns the crank, they get two gumballs for their quarter.
- It's means, for every 10 times a user turns the crank, on average, 1 time they will get 2 gumballs instead of 1.
- So, there is a 10% probability(or 1 in 10 chance) that the user gets a bonus gumball when they turn the crank.


### Problem with current code:
```java
final static int SOLD_OUT = 0;
final static int NO_QUARTER = 1;
final static int HAS_QUARTER = 2;
final static int SOLD = 3;
// first we need to add a new state: WINNER

// but then, you'd have to add a new condition in every single method to handle the WINNER state
// that's a lot of code to modify -- violating the OCP

public void turnCrank() {
    // This method will get especially messy
    // because you'd have to add code to check to see whether you've got a WINNER and then switch to either the WINNER state or the SOLD state
}
```

![Gumball With Winner](https://drive.google.com/uc?export=view&id=1Gez-Lslmqjyf0sCd-ilQB8MUlIHtcLZY)