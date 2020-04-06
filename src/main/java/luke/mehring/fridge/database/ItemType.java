package luke.mehring.fridge.database;

public enum ItemType {

    PIZZA("PIZZA"),
    SODA("SODA"),
    CHEESE("CHEESE");



    private String value;

    ItemType(String value) {
        this.setValue(value);
    }

    public static ItemType findByValue(String value){
        for(ItemType v : values()){
            if( v.getValue().equals(value)){
                return v;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
