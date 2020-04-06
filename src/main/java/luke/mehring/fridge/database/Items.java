package luke.mehring.fridge.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.BasicDBObject;

public class Items {
    private String name;
    private ItemType type;
    private int count;

    public Items() {
    }

    public Items(BasicDBObject dbObj) {
        this.setName(dbObj.getString("name"));
        this.setType(ItemType.findByValue(dbObj.getString("type")));
        this.setCount(dbObj.getInt("count"));
    }

    public Items(String name, ItemType type, int count) {
        this.setCount(count);
        this.setName(name);
        this.setType(type);
    }

    public BasicDBObject getDBDocument() {
        BasicDBObject itemDoc = new BasicDBObject();
        itemDoc.put("name", getName());
        itemDoc.put("type", getType().getValue());
        itemDoc.put("count", getCount());
        return itemDoc;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Items)) return false;
        Items that = (Items)o;
        return that.getName().equals(this.getName()) &&
                that.getCount() == this.getCount() &&
                that.getType() == this.getType();
    }

    public String toString() {
        return "{name="+getName()+",type="+getType()+",count="+getCount()+"}";
    }

    public int hashCode() {
        return getKey().hashCode();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @JsonIgnore
    public String getKey() {
        return name + "|" + type;
    }


}
