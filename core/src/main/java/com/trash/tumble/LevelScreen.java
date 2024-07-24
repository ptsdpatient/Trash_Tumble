package com.trash.tumble;

import static com.trash.tumble.Methods.extractSprite;
import static com.trash.tumble.Methods.files;
import static com.trash.tumble.Methods.print;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LevelScreen implements Screen {
    private TrashTumble game;
    Json json;
    private SpriteBatch batch;
    private static OrthographicCamera camera;
    private Viewport viewport;
    BitmapFont titleFont,dataFont;
    GlyphLayout titleLayout;
    Texture background;
    TextureRegion[] uiBox,buttonSheet;
    Array<LevelButton> levelButtons=new Array<>();
    Array<GameButton> gameButtons=new Array<>();
    FileHandle levelFile;
    String levels;
    Vector3 touch;
    Vector2 point;


    public class GameButton{
        Sprite button;
        String name;
        int id;
        boolean active=false;
        float scale=0.6f;
        public GameButton(TextureRegion tex,String name,int id){
            button=new Sprite(tex);
            button.setScale(scale);
            button.setPosition(1280/2f-80,720/2f-64);
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
    public class LevelButton{
        Sprite button;
        String gameMap;
        boolean active=false;
        float scale=0.8f,x,y;
        GlyphLayout layout;
        int id,bg;
        public LevelButton(TextureRegion tex,float x, float y , int id,String gameMap,int bg){
            button=new Sprite(tex);
            button.setPosition(x,y);
            button.setScale(0.8f);
            this.gameMap=gameMap;
            this.bg=bg;
            this.id=id;
            this.x=x;
            this.y=y;
            layout=new GlyphLayout(dataFont,id+"");
        }
        public void render(SpriteBatch sb){
            button.draw(sb);
            dataFont.draw(sb,id+"",x+ button.getWidth()/2f-layout.width/2f,y+12+button.getHeight()/2f);
            button.setAlpha(active?1f:0.5f);
            if(active){
                if(scale<0.9f){
                    scale+=Gdx.graphics.getDeltaTime()*1.1f;
                }
                button.setScale(scale);
            }else{
                if(scale>0.8f){
                    scale-=Gdx.graphics.getDeltaTime();
                    button.setScale(scale);
                }
            }
        }
    }

    public LevelScreen(TrashTumble game){
        json = new Json();
        json.setSerializer(LevelEdit.GameMap.class, new GameMapSerializer());
        this.game=game;
        this.batch=game.batch;
        camera=new OrthographicCamera();
        camera.setToOrtho(false,1280/2f,720/2f);
        viewport=new ExtendViewport(1280/2f,720/2f,camera);
        viewport.apply();
        titleFont=new BitmapFont(files("title.fnt"));
        dataFont=new BitmapFont(files("data.fnt"));
        titleLayout=new GlyphLayout(titleFont,"SELECT LEVEL");
        background=new Texture(files("level_select.png"));

        levelFile= Gdx.files.local("levels.txt");


        uiBox=extractSprite(files("ui_box_sheet.png"),64,64);
        buttonSheet=extractSprite(files("game_button_sheet.png"),64,64);

        gameButtons.add(new GameButton(buttonSheet[6],"close",0));

        levels="[{id:2,type:1,x:262,y:90.250015,rotation:0,scale:0.39925587},{id:2,type:0,x:255.5,y:223.25003,rotation:0,scale:0.33949754}],2\n[{id:1,type:1,x:452,y:82.00001,rotation:0,scale:0.6},{id:1,type:0,x:329,y:309.00003,rotation:0,scale:0.85121155},{id:0,type:2,x:247,y:111.00001,rotation:180,scale:3.2978265},{id:0,type:2,x:345,y:96.000015,rotation:180,scale:2.7908616},{id:3,type:2,x:305,y:217.00002,rotation:255,scale:2.8958943}],1\n[{id:6,type:3,x:516,y:47.000015,rotation:0,scale:1.5477045},{id:2,type:2,x:507,y:216.00003,rotation:0,scale:1.5515044},{id:0,type:1,x:333,y:276,rotation:540,scale:0.5979814},{id:9,type:2,x:234,y:37,rotation:0,scale:1.5530595},{id:5,type:2,x:420,y:32,rotation:0,scale:1.5620508},{id:0,type:0,x:330,y:129,rotation:0,scale:0.7}],0\n[{id:0,type:3,x:327,y:75,rotation:345,scale:1.5513041},{id:0,type:0,x:354,y:254.00003,rotation:15,scale:0.69548595},{id:0,type:1,x:468,y:51,rotation:0,scale:0.6}],0\n[{id:2,type:3,x:252,y:112.000015,rotation:0,scale:1.5472399},{id:5,type:2,x:278,y:37,rotation:0,scale:1.5508593},{id:0,type:2,x:543,y:120.000015,rotation:0,scale:3.449668},{id:8,type:2,x:607,y:94.00001,rotation:0,scale:2.6041427},{id:0,type:1,x:484,y:46,rotation:0,scale:0.38754106},{id:0,type:0,x:379,y:133,rotation:0,scale:0.39932206},{id:14,type:2,x:376,y:43,rotation:0,scale:1.552988},{id:2,type:2,x:255,y:278.00003,rotation:0,scale:1.5519629}],0\n[{id:2,type:3,x:302,y:70.00001,rotation:0,scale:1.5530918},{id:2,type:2,x:408,y:83,rotation:0,scale:1.5530584},{id:13,type:2,x:302,y:157.00002,rotation:0,scale:0.9506175}],0\n";

    }

    public void loadLevels(){
        levelButtons.clear();
//        String[] lines=levels.split("\n");


        levelFile=Gdx.files.local("levels.txt");
        String[] lines = levelFile.readString().split("\n");

        int i=0,j=0,index=0;
        for(String line : lines){
            int bg=line.charAt(line.length()-1)-'0';
            if(i>8){
                j++;
                i=0;
            }
            levelButtons.add(new LevelButton(uiBox[3],24+i*64,235+j*-64,index,line,bg));
            i++;
            index++;
        }

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.position.set(1280/4f,720/4f,0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background,0,0,1280/2f,720/2f);
        titleFont.draw(batch,"SELECT LEVEL",1280/4f-titleLayout.width/2f,720/2f-10);
        for(LevelButton btn : levelButtons){
            btn.render(batch);
        }
        for(GameButton btn : gameButtons){
            btn.render(batch);
        }
        batch.end();
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
                touch = new Vector3(screenX,screenY,0);
                camera.unproject(touch);
                point = new Vector2(touch.x,touch.y);
                for(LevelButton btn : levelButtons){
                    if(btn.button.getBoundingRectangle().contains(point)){
                        game.startGame(btn.gameMap,btn.bg);
                        break;
                    }
                }
                for(GameButton btn : gameButtons){
                    if(btn.button.getBoundingRectangle().contains(point)){
                        game.setMenuScreen();
                        break;
                    }
                }

                return false;
            }

            @Override
            public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                touch = new Vector3(screenX,screenY,0);
                camera.unproject(touch);
                point = new Vector2(touch.x,touch.y);
                for(LevelButton btn : levelButtons){
                    btn.active=btn.button.getBoundingRectangle().contains(point);
                }
                for(GameButton btn : gameButtons){
                    btn.active=btn.button.getBoundingRectangle().contains(point);
                }
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                touch = new Vector3(screenX,screenY,0);
                camera.unproject(touch);
                point = new Vector2(touch.x,touch.y);
                for(LevelButton btn : levelButtons){
                    btn.active=btn.button.getBoundingRectangle().contains(point);
                }
                for(GameButton btn : gameButtons){
                    btn.active=btn.button.getBoundingRectangle().contains(point);
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
    public void dispose() {
        for(TextureRegion tex : uiBox) tex.getTexture().dispose();
        for(TextureRegion tex : buttonSheet) tex.getTexture().dispose();
        background.dispose();
        titleFont.dispose();
        dataFont.dispose();
        batch.dispose();
    }
}
