package com.trash.tumble;

import static com.trash.tumble.Methods.extractSprite;
import static com.trash.tumble.Methods.files;
import static com.trash.tumble.Methods.print;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
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
    private World world;
    private Box2DDebugRenderer debugRenderer;
    boolean gameRun=true;
    float simulationSpeed=1;
    int gameBG=0;
    Vector3 touch;
    Vector2 point;
    TextureRegion[] gameButtonSheet,backgrounds,objectSheet,specialSheet,trashCanSheet,trashBagSheet;

    Array<GameButton> gameButtonList = new Array<>();
    Array<ObjectInstance> objectInstances=new Array<>();
    Array<SpecialInstance> specialInstances=new Array<>();
    Array<TrashBagInstance> trashBagInstances=new Array<>();
    Array<TrashCanInstance> trashCanInstances=new Array<>();


    String[] gameButtonNames={"slow","fast","restart","pause","play","levels","home","next"};

    public void setBG(int currentBG) {
        gameBG=currentBG;
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
    public class ObjectInstance{
        Sprite object;
        int id;

        public ObjectInstance(TextureRegion tex,float x,float y,float rotation,int id,float size){
            object=new Sprite(tex);
            object.setPosition(x,y);
            object.setScale(size);
            object.setRotation(rotation);
            this.id=id;
        }
        public void render(SpriteBatch sb){
            object.draw(sb);
        }
    }
    public class SpecialInstance{
        Sprite object;
        int id;

        public SpecialInstance(TextureRegion tex,float x,float y,float rotation,int id,float size){
            object=new Sprite(tex);
            object.setPosition(x,y);
            object.setScale(size);
            object.setRotation(rotation);
            this.id=id;
        }
        public void render(SpriteBatch sb){
            object.draw(sb);
        }
    }
    public class TrashBagInstance{
        Sprite object;
        int id;

        public TrashBagInstance(TextureRegion tex,float x,float y,float rotation,int id,float size){
            object=new Sprite(tex);
            object.setPosition(x,y);
            object.setScale(size);
            object.setRotation(rotation);
            this.id=id;
        }
        public void render(SpriteBatch sb){
            object.draw(sb);
        }
    }
    public class TrashCanInstance{
        Sprite object;
        int id;

        public TrashCanInstance(TextureRegion tex,float x,float y,float rotation,int id,float size){
            object=new Sprite(tex);
            object.setPosition(x,y);
            object.setScale(size);
            object.setRotation(rotation);
            this.id=id;
        }
        public void render(SpriteBatch sb){
            object.draw(sb);
        }
    }

    public class GameButton{
        Sprite button;
        String name;
        int id;
        boolean active=false;
        float scale=0.6f;
        public GameButton(TextureRegion tex,String name,int id){
            button=new Sprite(tex);
            button.setScale(scale);
            button.setPosition(1280/2f-(45*gameButtonSheet.length)+id*42,720/2f-55);
            this.name=name;
            this.id=id;
        }
        public void render(SpriteBatch sb){
            button.draw(sb);
            if(active){
                if(scale<0.67f){
                    scale+=Gdx.graphics.getDeltaTime()*1.1f;
                }
                button.setScale(scale);
            }else{
                if(scale>0.6f){
                    scale-=Gdx.graphics.getDeltaTime();
                    button.setScale(scale);
                }
            }
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
        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();

        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(1280/4f, 1);
        Body groundBody = world.createBody(groundBodyDef);

        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(1280/2f, 2);
        groundBody.createFixture(groundBox, 0.0f);
        groundBox.dispose();

        gameButtonSheet=extractSprite(files("game_button_sheet.png"),64,64);
        objectSheet=extractSprite(files("objects_sheet.png"),64,64);
        specialSheet=extractSprite(files("special_object_sheet.png"),64,64);
        trashCanSheet=extractSprite(files("trashbag_sheet.png"),128,128);
        trashBagSheet=extractSprite(files("trashcan_sheet.png"),192,192);

        int index=0;
        for(String name : gameButtonNames){
            gameButtonList.add(new GameButton(gameButtonSheet[index],name,index));
            index++;
        }

        backgrounds=new TextureRegion[3];
        for(int i=1;i<4;i++){
            backgrounds[i-1]=new TextureRegion(new Texture(files("gameBG_"+i+".png")));
        }

        gameMap.add(new GameMap(0,1,50,60,0,1));

        initializeWorld();
    }

    private void initializeWorld() {
        for(GameMap map : gameMap){
            switch(map.type){
                case 0:{

                }break;
                case 1:{

                }break;
            }
        }
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
        batch.draw(backgrounds[gameBG],0,0,1280/2f,720/2f);
        if(gameRun){
            world.step(delta*simulationSpeed, 6, 2);
        }

        for(GameButton btn : gameButtonList){
            btn.render(batch);
        }
        for(ObjectInstance obj : objectInstances){
            obj.render(batch);
        }
        for(SpecialInstance obj : specialInstances){
            obj.render(batch);
        }
        for(TrashCanInstance obj : trashCanInstances){
            obj.render(batch);
        }
        for(TrashBagInstance obj : trashBagInstances){
            obj.render(batch);
        }
        batch.end();
        debugRenderer.render(world, camera.combined);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                touch = new Vector3(screenX,screenY,0);
                camera.unproject(touch);
                point = new Vector2(touch.x,touch.y);
                for(GameButton button : gameButtonList){
                    button.active=button.button.getBoundingRectangle().contains(point);
                }
                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                return false;
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        world.dispose();
        batch.dispose();
        debugRenderer.dispose();

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
                start = -1;
            }
        }
    }
}
