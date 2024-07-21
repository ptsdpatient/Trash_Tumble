package com.trash.tumble;

import static com.trash.tumble.Methods.print;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameScreen implements Screen {
    private TrashTumble game;
    Json json;
    private SpriteBatch batch;
    private static OrthographicCamera camera;
    private Viewport viewport;
    Array<GameMap> gameMap = new Array<>();

    public void setBG(int currentBG) {
        
    }

    public static class GameMap{
        int id,type;
        float scale,rotation,x,y;
        public GameMap(int id,int type,float x,float y,float rotation,float scale){
            this.id=id;
            this.type=type;
            this.x=x;
            this.y=y;
            this.rotation=rotation;
            this.scale=scale;

        }
    }

    public GameScreen(TrashTumble game){
        json = new Json();
        json.setSerializer(LevelEdit.GameMap.class, new GameMapSerializer());
        this.game=game;
        this.batch=game.batch;
        camera=new OrthographicCamera();
        camera.setToOrtho(false,1280/2f,720/2f);
        viewport=new ExtendViewport(1280/2f,720/2f,camera);
        viewport.apply();

    }




    public void setGameMap(String gameMapSchema){
        gameMap=new Array<>();
        gameMapParser(gameMapSchema);
        print(gameMap.size);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.position.set(1280/4f,720/4f,0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();


        batch.end();
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }


    public void gameMapParser(String gameSchema){
        String data=gameSchema.replace("[", "").replace("]", "");
        int start = -1;
        int end;
        for (int i = 0; i < data.length(); i++) {
            char ch = data.charAt(i);

            if (ch == '{') {
                start = i;
            } else if (ch == '}' && start != -1) {
                end = i;
                String line = data.substring(start + 1, end);
                Pattern pattern = Pattern.compile("(\\w+):(\\d+\\.\\d+|\\d+)");
                Matcher matcher = pattern.matcher(line);
                float id=0,objType=0,x=0,y=0,rotation=0,scale=0;
                while (matcher.find()) {
                    String key = matcher.group(1);
                    String valueStr = matcher.group(2);
                    float value = Float.parseFloat(valueStr);
                    switch(key){
                        case "id":{
                            id=value;
                        }break;
                        case "type":{
                            objType=value;
                        }break;
                        case "x":{
                            x=value;
                        }break;
                        case "y":{
                            y=value;
                        }break;
                        case "rotation":{
                            rotation=value;
                        }break;
                        case "scale":{
                            scale=value;
                        }break;
                    }
                }
                gameMap.add(new GameMap((int) id,(int) objType,x,y,rotation,scale));
                //add items here!
                start = -1;
            }
        }
    }
}
