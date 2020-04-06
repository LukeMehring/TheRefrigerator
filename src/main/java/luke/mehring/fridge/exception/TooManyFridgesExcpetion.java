package luke.mehring.fridge.exception;

public class TooManyFridgesExcpetion extends Exception {

    public TooManyFridgesExcpetion (String msg) {
        super(msg);
    }

    public TooManyFridgesExcpetion (String msg, Throwable t) {
        super(msg, t);
    }
}
