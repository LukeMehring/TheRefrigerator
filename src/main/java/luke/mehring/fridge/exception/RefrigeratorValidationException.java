package luke.mehring.fridge.exception;

import luke.mehring.fridge.database.Refrigerator;

public class RefrigeratorValidationException extends Exception {

    public RefrigeratorValidationException (String msg) {
        super(msg);
    }

    public RefrigeratorValidationException (String msg, Throwable t) {
        super(msg, t);
    }
}
