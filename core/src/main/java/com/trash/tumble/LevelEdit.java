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
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.Point;
import java.awt.Rectangle;

public class LevelEdit implements Screen {
    private TrashTumble game;
    Vector3 touch;
    Vector2 point;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture[] backgrounds=new Texture[4];
    float worldWidth=1280/2f,worldHeight=780/2f;

    TextureRegion[] editButtonSheet,uiBoxSheet,editObjectSheet,editPanelButtonSheet,trashBagSheet,trashCanSheet,panelObjectSheet,specialObjectSheet;
    String[] levelMenuButton={"close","done","toggle"};
    String[] editObjectName={"rotate","sizeUP","sizeDOWN","delete"};

    Array<EditMenuButton> levelMenuButtonList=new Array<>();
    Array<EditObjectButton> editObjectList=new Array<>();
    Array<EditPanelButton> editPanelButtons=new Array<>();
    Array<PanelBackgroundButton> panelBackgroundButtons = new SnapshotArray<>();
    Array<PanelTrashBagButton> panelTrashBagButtons = new Array<>();
    Array<PanelTrashCanButton> panelTrashCanButtons = new Array<>();
    Array<PanelTrashBag> panelTrashBags = new Array<>();
    Array<PanelTrashCan> panelTrashCans = new Array<>();
    Array<PanelObject> panelObjects= new Array<>();
    Array<SpecialObject> specialObjects= new Array<>();


    int currentBG=0,panelIndex=0;
    boolean showPanel=false;

    public LevelEdit(TrashTumble game){
        this.game=game;
        this.batch=game.batch;

        for(int i =0;i<3;i++)backgrounds[i]=new Texture(files("gameBG_"+(i+1)+".png"));

        camera=new OrthographicCamera();
        camera.setToOrtho(false,1280/2f,720/2f);
        viewport=new ExtendViewport(1280/2f,720/2f,camera);
        viewport.apply();

        editButtonSheet=extractSprite(files("edit_button_sheet.png"),64,64);
        int index=0;
        for(String name : levelMenuButton){
            levelMenuButtonList.add(new EditMenuButton(editButtonSheet[index],name,index));
            index++;
        }
        editObjectSheet=extractSprite(files("edit_item_sheet.png"),64,64);
        index=0;
        for(String name : editObjectName){
            editObjectList.add(new EditObjectButton(editObjectSheet[index],name,index));
            index++;
        }

        editPanelButtonSheet=extractSprite(files("edit_panel_button_sheet.png"),64,64);
        trashBagSheet=extractSprite(files("trashbag_sheet.png"),128,128);
        trashCanSheet=extractSprite(files("trashcan_sheet.png"),192,192);
        panelObjectSheet=extractSprite(files("objects_sheet.png"),64,64);
        specialObjectSheet=extractSprite(files("special_object_sheet.png"),64,64);

        for(int i=0;i<3;i++){
            editPanelButtons.add(new EditPanelButton(editPanelButtonSheet[i],i));
        }

        for(int i=0;i<2;i++){
            panelBackgroundButtons.add(new PanelBackgroundButton(editButtonSheet[3],i));
            panelTrashBagButtons.add(new PanelTrashBagButton(editButtonSheet[3],i));
            panelTrashCanButtons.add(new PanelTrashCanButton(editButtonSheet[3],i));
        }

        panelTrashBags.add(new PanelTrashBag(trashBagSheet[0],0,true));
        panelTrashCans.add(new PanelTrashCan(trashCanSheet[0],0,true));

        index=0;
        for(int i =0;i<6;i++){
            for(int j=0;j<3;j++){
                if(index>16)break;
                panelObjects.add(new PanelObject(panelObjectSheet[index],j*64,720/2f-100-(15+i*50),index,true));
                index++;
            }
        }

        index=0;
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                specialObjects.add(new SpecialObject(specialObjectSheet[index],j*64,720/2f-100-(15+i*50),index,true));
                index++;
            }
        }

        uiBoxSheet=extractSprite(files("ui_box_sheet.png"),64,64);


    }

    public class SpecialObject{
        Sprite button;
        Rectangle bounds;
        boolean active=false,spawn=false;
        float scale=0.65f;

        public SpecialObject(TextureRegion tex,float x,float y,int id,boolean spawn){

            button=new Sprite(tex);
            button.setPosition(x,y);
            if(spawn){
                switch(id){
                    case 0 :{

                    }break;
                }
            }
            button.setScale(scale);
        }
        public void render(SpriteBatch sb){
            sb.draw(uiBoxSheet[3],button.getX()+10,button.getY()+11,button.getWidth()/1.5f,button.getHeight()/1.5f);

            button.draw(sb);

            if(active){
                if(scale<0.75f){
                    scale+=Gdx.graphics.getDeltaTime()*1.7f;
                }
                button.setScale(scale);
            }else{
                if(scale>0.65f){
                    scale-=Gdx.graphics.getDeltaTime();
                    button.setScale(scale);
                }
            }
        }
    }

    public class PanelObject{
        Sprite button;
        Rectangle bounds;
        boolean active=false,spawn=false;
        float scale=0.65f;


        public PanelObject(TextureRegion tex,float x,float y,int id,boolean spawn){

            button=new Sprite(tex);
            button.setPosition(x,y);
            if(spawn){
                switch(id){
                    case 0 :{

                    }break;
                }
            }
            button.setScale(scale);
        }
        public void render(SpriteBatch sb){
            sb.draw(uiBoxSheet[3],button.getX()+10,button.getY()+11,button.getWidth()/1.5f,button.getHeight()/1.5f);

            button.draw(sb);


            if(active){
                if(scale<0.75f){
                    scale+=Gdx.graphics.getDeltaTime()*1.7f;
                }
                button.setScale(scale);
            }else{
                if(scale>0.65f){
                    scale-=Gdx.graphics.getDeltaTime();
                    button.setScale(scale);
                }
            }
        }
    }


    public class PanelTrashCan{
        boolean spawn=false;
        Sprite button;
        boolean active=false;
        float scale=0.8f;
        int id;
        public PanelTrashCan(TextureRegion tex,int id,boolean spawn){
            button=new Sprite(tex);
            button.setScale(0.4f);
            button.setY(-40);
            button.setX(15f);
            this.spawn=spawn;
            this.id=id;
            if(id==1)button.flip(true,false);
        }
        public void render(SpriteBatch sb){
            button.draw(sb);
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
    public class PanelTrashBag{
        boolean spawn=false;
        Sprite button;
        boolean active=false;
        float scale=0.8f;
        int id;
        public PanelTrashBag(TextureRegion tex,int id,boolean spawn){
            button=new Sprite(tex);
            button.setScale(0.5f);
            button.setY(90);
            button.setX(46f);
            this.spawn=spawn;
            this.id=id;
            if(id==1)button.flip(true,false);
        }
        public void render(SpriteBatch sb){
            button.draw(sb);
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

    public class PanelTrashBagButton{
        Sprite button;
        boolean active=false;
        float scale=0.8f;
        int id;
        public PanelTrashBagButton(TextureRegion tex,int id){
            button=new Sprite(tex);
            button.setScale(0.8f);
            button.setY(120);
            button.setX(id*120+20);
            this.id=id;
            if(id==1)button.flip(true,false);
        }
        public void render(SpriteBatch sb){
            button.draw(sb);
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
    public class PanelTrashCanButton{
        Sprite button;
        boolean active=false;
        float scale=0.8f;
        int id;
        public PanelTrashCanButton(TextureRegion tex,int id){
            button=new Sprite(tex);
            button.setScale(0.8f);
            button.setY(30);
            button.setX(id*120+20);
            this.id=id;
            if(id==1)button.flip(true,false);
        }
        public void render(SpriteBatch sb){
            button.draw(sb);
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
    public class PanelBackgroundButton{
        Sprite button;
        boolean active=false;
        float scale=0.8f;
        int id;
        public PanelBackgroundButton(TextureRegion tex,int id){
            button=new Sprite(tex);
            button.setScale(0.8f);
            button.setY(210);
            button.setX(id*185-10);
            this.id=id;
            if(id==1)button.flip(true,false);
        }
        public void render(SpriteBatch sb){
            button.draw(sb);
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

    public class EditPanelButton{
        Sprite button;
        boolean active=false;
        float scale=0.8f;
        int id;
        public EditPanelButton(TextureRegion tex,int id){
            button=new Sprite(tex);
            button.setScale(0.8f);
            button.setY(720/2f-button.getHeight());
            button.setX(id*60);
            this.id=id;
        }
        public void render(SpriteBatch sb){
            button.draw(sb);
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

    public class EditObjectButton{
        Sprite button;
        String name;
        boolean active=false;
        float scale=0.8f;
        int id;
        public EditObjectButton(TextureRegion tex,String name,int id){
            button=new Sprite(tex);
            button.setScale(0.8f);
            button.setY(720/2f-button.getHeight());
            button.setX(220+id*70);
            this.name=name;
            this.id=id;
        }
        public void render(SpriteBatch sb){
            button.draw(sb);
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
                button.setFlip(!showPanel,false);
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

            for(EditPanelButton btn : editPanelButtons){
                if(btn.button.getBoundingRectangle().contains(point))panelIndex=btn.id;
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
            for(EditObjectButton button : editObjectList){
                button.active=button.button.getBoundingRectangle().contains(point);
            }
            for(EditPanelButton button : editPanelButtons){
                button.active=button.button.getBoundingRectangle().contains(point);
            }
            for(PanelBackgroundButton button : panelBackgroundButtons){
                button.active=button.button.getBoundingRectangle().contains(point);
            }
            for(PanelTrashBagButton button : panelTrashBagButtons){
                button.active=button.button.getBoundingRectangle().contains(point);
            }
            for(PanelTrashCanButton button : panelTrashCanButtons){
                button.active=button.button.getBoundingRectangle().contains(point);
            }
            for(PanelObject button : panelObjects){
                button.active=button.button.getBoundingRectangle().contains(point);
            }
            for(SpecialObject button : specialObjects){
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
        batch.draw(backgrounds[currentBG],0,0,worldWidth,worldHeight);

        for(EditMenuButton button :levelMenuButtonList){
            button.render(batch);
        }

        for(EditObjectButton button : editObjectList){
            button.render(batch);
        }

        if(showPanel){

            for(EditPanelButton buttons : editPanelButtons){
                buttons.render(batch);
            }

            switch(panelIndex){
                case 0:{
                    batch.draw(backgrounds[currentBG],42,200,145,80);
                    for(PanelBackgroundButton button : panelBackgroundButtons){
                        button.render(batch);
                    }
                    for(PanelTrashBagButton button : panelTrashBagButtons)button.render(batch);
                    for(PanelTrashCanButton button : panelTrashCanButtons)button.render(batch);
                    for(PanelTrashBag button : panelTrashBags)button.render(batch);
                    for(PanelTrashCan button : panelTrashCans)button.render(batch);
                }break;
                case 1:{
                    for(PanelObject obj : panelObjects){
                        obj.render(batch);
                    }
                }break;
                case 2:{
                    for(SpecialObject obj : specialObjects){
                        obj.render(batch);
                    }
                }break;
            }
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
