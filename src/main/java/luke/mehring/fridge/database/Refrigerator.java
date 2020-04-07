package luke.mehring.fridge.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import java.beans.Transient;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Refrigerator {
    private String name;
    private Map<String, Items> items = new HashMap<>();

    public Refrigerator () {}

    public Refrigerator (BasicDBObject dbObject) {
        setName(dbObject.getString("name"));
        setItemsMap(((BasicDBList)dbObject.get("items")).stream().map(item -> new Items((BasicDBObject)item))
                .collect(Collectors.toMap(item -> item.getKey(), item -> item)));

    }

    public boolean equals(Object o) {
        if (!(o instanceof Refrigerator)) return false;
        Refrigerator that = (Refrigerator)o;
        if (!that.getName().equals(this.getName())) return false;
        if (!that.getItemsMap().equals(this.getItemsMap())) return false;
        return true;
    }

    public String toString() {
        return "{name="+getName()+",items="+ getItemsMap().values().toString()+"}";
    }

    public int hashCode() {
        return name.hashCode();
    }

    @JsonIgnore
    public BasicDBObject getDBDocument() {
        BasicDBObject fridgeDoc = new BasicDBObject();
        fridgeDoc.put("name", getName());
        fridgeDoc.put("items", getItemsMap().values().stream().map(Items::getDBDocument).collect(Collectors.toList()));
        return fridgeDoc;
    }

    public void update(Refrigerator that) {
        this.setName(that.getName());

        that.getItemsMap().values().stream().forEach(this::addItem);
    }

    public void addItem(Items item) {
        Items oldItem = getItemsMap().get(item.getKey());
        if (oldItem == null) {
            getItemsMap().put(item.getKey(), item);
        } else {
            int newCount = oldItem.getCount() + item.getCount();
            if (newCount > 0) {
                oldItem.setCount(newCount);
            } else {
                //If its zero or lower, we can just remove
                getItemsMap().remove(item.getKey());
            }
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Items> getItems() {
        return items.values();
    }

    public void setItems(Collection<Items> items) {
        this.setItemsMap(items.stream().collect(Collectors.toMap(item -> item.getKey(), item -> item)));
    }

    @JsonIgnore
    public Map<String, Items> getItemsMap() {
        return items;
    }

    @JsonIgnore
    public void setItemsMap(Map<String, Items> items) {
        this.items = items;
    }
}
