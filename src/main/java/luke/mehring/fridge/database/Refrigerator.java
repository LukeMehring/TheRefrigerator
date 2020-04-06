package luke.mehring.fridge.database;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import java.util.*;
import java.util.stream.Collectors;

public class Refrigerator {
    private String name;
    private Map<String, Items> items = new HashMap<>();

    public Refrigerator () {}

    public Refrigerator (BasicDBObject dbObject) {
        setName(dbObject.getString("name"));
        ((BasicDBList)dbObject.get("items")).stream().map(item -> new Items((BasicDBObject)item))
                .collect(Collectors.toMap(item -> item.getKey(), item -> item));

    }

    public BasicDBObject getDBDocument() {
        BasicDBObject fridgeDoc = new BasicDBObject();
        fridgeDoc.put("name", getName());
        fridgeDoc.put("items", getItems().values().stream().map(Items::getDBDocument).collect(Collectors.toList()));
        return fridgeDoc;
    }

    public void update(Refrigerator that) {
        this.setName(that.getName());

        Map<String, Items> newItemMap = that.getItems();

        items.keySet().forEach(itemKey -> {
            Items oldItem = items.get(itemKey);
            Items newItemUpdate = newItemMap.remove(itemKey);
            oldItem.setCount(oldItem.getCount() + newItemUpdate.getCount());
        });

        items.putAll(newItemMap);
    }

    public void addItem(Items item) {
        Items oldItem = getItems().get(item.getKey());
        if (oldItem == null) {
            getItems().put(item.getKey(), item);
        } else {
            oldItem.setCount(oldItem.getCount() + item.getCount());
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
