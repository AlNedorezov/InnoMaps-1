package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alnedorezov on 7/4/16.
 */
@DatabaseTable(tableName = "Edge_types")
public class EdgeType {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField(unique = true)
    private String name;

    public EdgeType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // For deserialization with Jackson
    public EdgeType() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof EdgeType)) {
            return false;
        } else {
            EdgeType var2 = (EdgeType) o;
            return getId() == var2.getId() &&
                    getName().equals(var2.getName());
        }
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}