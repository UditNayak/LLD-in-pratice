# Design a Multilevel Parking Lot

## Initial Requirements
- Parking lot has multiple levels (floors) and each level has multiple rows of parking spots.
- Parking Lot has multiple entry and exit points (all in the ground level).
- Customer collect a ticket from the entry gate. Ticket contains entry time and parking spot information.
- Customer to make payment at the exit gate.
- If the parking lot is full, then customer will be denied entry.
- Parking lot has parking spots of different types for different types of vehicles (e.g., motorcycle, car, truck).
- Some parking spots have power plugs for electric vehicles.

## Follow-up Questions
1. How the Billing will be done?
    - Billing is a Function of three things:
        - time (duration of parking)
        - Type of spot (smaller spots are cheaper, larger spots are more expensive)
        - Extra Resources (e.g., power plug)
2. Can a smaller vehicle park in a larger spot?
    - Yes, but the billing will be based on the size of the spot.
    - Larger vehicles cannot park in smaller spots.
3. Any strategy for parking spot allocation?
    - Allocate the Nearest available Spot to the Entry Gate.
4. What information does the Ticket contain?
    - Ticket contains:
        - Vehicle Information (Type, License Plate Number)
        - Entry Gate
        - Entry Time
        - Parking Spot Information (Floor Number, Row Number, Spot Number)
5. Suppose a customer opted for power plug, but never used it. Will he be charged?
    - Yes, if the customer opted for power plug, he will be charged for it.

## Note:
- In future the Parking Lot can be extended:
    - more floors
    - more slots
    - larger slots can be converted to multiple smaller slots
    - more entry/exit gates
- Future Scope: Dynamic Pricing / Surge Pricing based on demand (e.g., peak hours, special events)