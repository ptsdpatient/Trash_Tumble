package com.trash.tumble;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Methods {
    static int index=0;
    public static FileHandle files(String item){
        return Gdx.files.internal(item);
    }
    public static TextureRegion[] extractSprite(FileHandle file,int width,int height){
        return new TextureRegion(new Texture(file)).split(width,height)[0];
    }
    public static void print(String line){
        index++;
        Gdx.app.log(index+"",line);
    }

    public static void print(int line){
        index++;
        Gdx.app.log(index+"",line+"");
    }
    public static void print(float line){
        index++;
        Gdx.app.log(index+"",line+"");
    }
}
