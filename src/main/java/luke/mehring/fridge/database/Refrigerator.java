package luke.mehring.fridge.database;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Refrigerator {
    private String name;
    private Map<String, Items> items = new HashMap<>();

    public Refrigerator () {}

    public Refrigerator (BasicDBObject dbObject) {
        setName(dbObject.getString("name"));
        setItems(((BasicDBList)dbObject.get("items")).stream().map(item -> new Items((BasicDBObject)item))
                .collect(Collectors.toMap(item -> item.getKey(), item -> item)));

    }

    public boolean equals(Object o) {
        if (!(o instanceof Refrigerator)) return false;
        Refrigerator that = (Refrigerator)o;
        if (!that.getName().equals(this.getName())) return false;
        if (!that.getItems().equals(this.getItems())) return false;
        return true;
    }

    public String toString() {
        return "{name="+getName()+",items="+getItems().values().toString()+"}";
    }

    public int hashCode() {
        return name.hashCode();
    }

    public BasicDBObject getDBDocument() {
        BasicDBObject fridgeDoc = new BasicDBObject();
        fridgeDoc.put("name", getName());
        fridgeDoc.put("items", getItems().values().stream().map(Items::getDBDocument).collect(Collectors.toList()));
        return fridgeDoc;
    }

    public void update(Refrigerator that) {
        this.setName(that.getName());

        that.getItems().values().stream().forEach(this::addItem);
    }

    public void addItem(Items item) {
        Items oldItem = getItems().get(item.getKey());
        if (oldItem == null) {
            getItems().put(item.getKey(), item);
        } else {
            int newCount = oldItem.getCount() + item.getCount();
            if (newCount != 0) {
                oldItem.setCount(newCount);
            } else {
                //If its zero, we can just remove
                getItems().remove(item.getKey());
            }
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Items> getItems() {
        return items;
    }

    public void setItems(Map<String, Items> items) {
        this.items = items;
    }
}
