package com.trash.tumble;

import static com.trash.tumble.Methods.extractSprite;
import static com.trash.tumble.Methods.files;
import static com.trash.tumble.Methods.print;

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
    TextureRegion[] uiBox;
    Array<LevelButton> levelButtons=new Array<>();
    FileHandle levelFile;
    Vector3 touch;
    Vector2 point;



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

        uiBox=extractSprite(files("ui_box_sheet.png"),64,64);
    }

    public void loadLevels(){
        levelFile= Gdx.files.local("levels.txt");
        String[] lines = levelFile.readString().split("\n");
        String[] bgParse;
        int i=0,j=0,index=0,bg=0;
        for(String line : lines){
            bg=Character.getNumericValue(line.charAt(line.length()-1));
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
        batch.dispose();
    }
}
