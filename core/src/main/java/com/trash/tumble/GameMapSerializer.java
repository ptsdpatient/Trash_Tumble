package com.trash.tumble;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class GameMapSerializer implements Json.Serializer<LevelEdit.GameMap> {

    @Override
    public void write(Json json, LevelEdit.GameMap object, Class knownType) {
        json.writeObjectStart();
        json.writeValue("id", object.id);
        json.writeValue("type", object.type);
        json.writeValue("x", object.x);
        json.writeValue("y", object.y);
        json.writeValue("rotation", object.rotation);
        json.writeValue("scale", object.scale);
        json.writeObjectEnd();
    }

    @Override
    public LevelEdit.GameMap read(Json json, JsonValue jsonData, Class type) {
        // Implement this method if you need deserialization
        int id = jsonData.getInt("id");
        int objectType = jsonData.getInt("type");
        float x = jsonData.getFloat("x");
        float y = jsonData.getFloat("y");
        float rotation = jsonData.getFloat("rotation");
        float scale = jsonData.getFloat("scale");
        return new LevelEdit.GameMap( id, objectType, x, y, rotation, scale);
    }
}
