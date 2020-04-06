package luke.mehring.fridge.exception;

public class NoFridgesExcpetion extends Exception {

    public NoFridgesExcpetion (String msg) {
        super(msg);
    }

    public NoFridgesExcpetion (String msg, Throwable t) {
        super(msg, t);
    }
}
