package com.trash.tumble;

import static com.trash.tumble.Methods.extractSprite;
import static com.trash.tumble.Methods.files;
import static com.trash.tumble.Methods.print;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.trash.tumble.Methods;

public class StartMenu implements Screen {
    private TrashTumble game;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private Viewport viewport;
    Texture background;
    static int activeButtonIndex=4;
    Vector3 touch;
    Vector2 point;

    TextureRegion[] buttonSheet;

    String[] buttonNameList={"PLAY","LEVEL EDITOR","EXIT"};
    Array<Button> buttonList=new Array<>();




    public static class Button{
        Sprite button;
        String name;
        int id;
        float scale=1;
        public Button(TextureRegion tex, String name,int id){
            button=new Sprite(tex);
            
            button.setX(1280/4f-button.getWidth()/2f);
            button.setY(720/4f-70-id*(button.getHeight()+3f));

            button.setScale(0.8f);

            this.name=name;
            this.id=id;
        }

        public void render(SpriteBatch sb){
            button.draw(sb);
            if(activeButtonIndex==id){
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

    public StartMenu(TrashTumble game){
        this.game=game;
        this.batch=game.batch;
        font=new BitmapFont();
        background=new Texture(files("startScreenBG.png"));
        font=new BitmapFont(files("data.fnt"));

        buttonSheet=extractSprite(files("startButtonSheet.png"),310,46);

        int index=0;

        for(String name : buttonNameList){
            buttonList.add(new Button(buttonSheet[index], name, index));
            index++;
        }
        index=0;


        camera=new OrthographicCamera();
        camera.setToOrtho(false,1280/2f,720/2f);
        viewport=new ExtendViewport(1280/2f,720/2f,camera);
        viewport.apply();


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
                for(Button button : buttonList){
                    if(button.button.getBoundingRectangle().contains(point)){
                    activeButtonIndex=button.id;
                    }

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
    public void show() {

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


    for(Button button : buttonList){
        button.render(batch);
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

    }

    @Override
    public void dispose() {
        font.dispose();
        for(TextureRegion tex : buttonSheet){
            tex.getTexture().dispose();
        }
        batch.dispose();
    }
}
