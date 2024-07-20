package com.trash.tumble;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Methods {
    public static FileHandle files(String item){
        return Gdx.files.internal(item);
    }
    public static TextureRegion[] extractSprite(FileHandle file,int width,int height){
        return new TextureRegion(new Texture(file)).split(width,height)[0];
    }
    public static void print(String line){
        Gdx.app.log("",line);
    }

    public static void print(int line){
        Gdx.app.log("",line+"");
    }
    public static void print(float line){
        Gdx.app.log("",""+line);
    }
}
