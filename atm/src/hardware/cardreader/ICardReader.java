package hardware.cardreader;

import card.Card;

public interface ICardReader {
    Card readCard();      // read card from Device
    void ejectCard();
}
