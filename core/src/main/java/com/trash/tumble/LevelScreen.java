package com.trash.tumble;

import static com.trash.tumble.Methods.extractSprite;
import static com.trash.tumble.Methods.files;

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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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



    public class LevelButton{
        Sprite button;
        String name,gameMap;
        boolean active=false;
        float scale=0.8f,x,y;
        int id;
        public LevelButton(TextureRegion tex,float x, float y , int id,String gameMap){
            button=new Sprite(tex);
            button.setScale(0.8f);
            switch(id){
                case 0: case 1: button.setPosition(1280/2f-(id+1)*button.getWidth(),720/2f- button.getHeight());break;
                case 2:button.setPosition(0,720/4f-button.getHeight()/2f);break;
            }
            this.gameMap=gameMap;
            this.name=name;
            this.id=id;
            this.x=x;
            this.y=y;
        }
        public void render(SpriteBatch sb){
            button.draw(sb);
            dataFont.draw(sb,id+"",x,y);
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

        int i=0,j=0,index=0;
        for(String line : lines){
            if(i>8){
                j++;
                i=0;
            }
            levelButtons.add(new LevelButton(uiBox[2],100+i*64,300+j*64,index,line));
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
