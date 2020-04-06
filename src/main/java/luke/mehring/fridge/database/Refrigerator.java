package luke.mehring.fridge.database;

import java.util.ArrayList;
import java.util.List;

public class Refrigerator {
    private String name;
    private List<Items> items = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }


}
