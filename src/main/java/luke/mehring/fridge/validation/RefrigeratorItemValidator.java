package luke.mehring.fridge.validation;

import luke.mehring.fridge.database.ItemType;
import luke.mehring.fridge.database.Items;
import luke.mehring.fridge.database.Refrigerator;
import luke.mehring.fridge.exception.RefrigeratorValidationException;

public class RefrigeratorItemValidator {

    private static final int MAX_SODA = 12;

    public static void validate(Refrigerator fridge) throws RefrigeratorValidationException {
        checkSodaLevels(fridge);
    }

    private static void checkSodaLevels(Refrigerator fridge) throws RefrigeratorValidationException {
        //Get sums of soda Levels
        int sodaLevel = fridge.getItems().values().stream().filter(item -> item.getType() == ItemType.SODA).mapToInt(Items::getCount).sum();
        if (sodaLevel > MAX_SODA) {
            throw new RefrigeratorValidationException(ItemType.SODA.getValue() + " is over the limit of " + MAX_SODA);
        }
    }


}
