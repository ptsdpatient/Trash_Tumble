package com.trash.tumble;

import static com.trash.tumble.Methods.extractSprite;
import static com.trash.tumble.Methods.files;
import static com.trash.tumble.Methods.print;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.Point;

public class LevelEdit implements Screen {
    private TrashTumble game;
    Vector3 touch;
    Vector2 point;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private TextureRegion[] backgrounds=new TextureRegion[4];
    float worldWidth=1280/2f,worldHeight=780/2f;
    Texture background;
    TextureRegion[] editButtonSheet,uiBoxSheet,editObjectSheet;
    String[] levelMenuButton={"close","done","toggle"};
    String[] editObjectName={"rotate","sizeUP","sizeDOWN","delete"};
    Array<EditMenuButton> levelMenuButtonList=new Array<>();
    int currentBG=0;
    boolean showPanel=false;

    public LevelEdit(TrashTumble game){
        this.game=game;
        this.batch=game.batch;
        background=new Texture(files("gameBG_1.png"));
        camera=new OrthographicCamera();
        camera.setToOrtho(false,1280/2f,720/2f);
        viewport=new ExtendViewport(1280/2f,720/2f,camera);
        viewport.apply();
        for(int i=1;i<4;i++){
            backgrounds[i-1]=new TextureRegion(new Texture(files("gameBG_"+i+".png")));
        }
        editButtonSheet=extractSprite(files("edit_button_sheet.png"),64,64);

        int index=0;
        for(String name : levelMenuButton){
            levelMenuButtonList.add(new EditMenuButton(editButtonSheet[index],name,index));
            index++;
        }

        uiBoxSheet=extractSprite(files("ui_box_sheet.png"),64,64);

    }

    public class EditObjectButton{
        Sprite button;
        String name;
        boolean active=false;
        float scale=0.8f;
        int id;
        public EditObjectButton(TextureRegion tex,String name,int id){
            button=new Sprite(tex);
            button.setScale(0.8f);
            switch(id){
                case 0: case 1: button.setPosition(1280/2f-(id+1)*button.getWidth(),720/2f- button.getHeight());break;
                case 2:button.setPosition(0,720/4f-button.getHeight()/2f);break;
            }

            this.name=name;
            this.id=id;
        }
        public void render(SpriteBatch sb){
            button.draw(sb);
            if(id==2){
                button.setFlip(showPanel,false);
            }
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
    public class EditMenuButton{
        Sprite button;
        String name;
        boolean active=false;
        float scale=0.8f;
        int id;
        public EditMenuButton(TextureRegion tex,String name,int id){
            button=new Sprite(tex);
            button.setScale(0.8f);
            switch(id){
                case 0: case 1: button.setPosition(1280/2f-(id+1)*button.getWidth(),720/2f- button.getHeight());break;
                case 2:button.setPosition(0,720/4f-button.getHeight()/2f);break;
            }

            this.name=name;
            this.id=id;
        }
        public void render(SpriteBatch sb){
            button.draw(sb);
            if(id==2){
                button.setFlip(showPanel,false);
            }
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
            for(EditMenuButton btn : levelMenuButtonList){
                if(btn.button.getBoundingRectangle().contains(point)){
                    switch(btn.id) {
                        case 0:
                            game.setMenuScreen();
                            break;
                        case 1:
                            game.startGame();
                            break;
                        case 2:
                            showPanel = !showPanel;
                            btn.button.setX(showPanel ? 200f : 0);
                    }
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
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            touch = new Vector3(screenX,screenY,0);
            camera.unproject(touch);
            point = new Vector2(touch.x,touch.y);
            for(EditMenuButton button : levelMenuButtonList){
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
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.position.set(1280/4f,720/4f,0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background,0,0,worldWidth,worldHeight);

        for(EditMenuButton button :levelMenuButtonList){
            button.render(batch);
        }
        if(showPanel){
            batch.draw(uiBoxSheet[3],0,0,200,720/2f);
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
    public void dispose() {

    }
}
