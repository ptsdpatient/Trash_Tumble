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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
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
    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    Array<GameMap> gameMap = new Array<>();
    private World world;
    private Box2DDebugRenderer debugRenderer;
    boolean gameRun=true,gameWon=false;
    float simulationSpeed=1,time=0f;
    int gameBG=0;
    Sprite nextGameButton;
    Vector3 touch;
    Vector2 point;
    TextureRegion[] gameButtonSheet,backgrounds,objectSheet,specialSheet,trashCanSheet,trashBagSheet;
    Array<GameButton> gameButtonList = new Array<>();
    Array<ObjectInstance> objectInstances=new Array<>();
    Array<SpecialInstance> specialInstances=new Array<>();
    Array<TrashBagInstance> trashBagInstances=new Array<>();
    Array<TrashCanInstance> trashCanInstances=new Array<>();
    Array<Body> worldBodies=new Array<>();
    GameContactListener listener;


    String[] gameButtonNames={"slow","fast","restart","pause","play","back","home"};

    public void setBG(int currentBG) {
        gameBG=currentBG;
    }

    public void worldSet(){
        world.setContactListener(null);
        time=0f;
        gameWon=false;
        gameRun=true;
        objectInstances.clear();
        specialInstances.clear();
        trashBagInstances.clear();
        trashCanInstances.clear();
        world.dispose();
        world = new World(new Vector2(0, -9.8f*5*(gameBG==1?0.4f:1)), true);
        debugRenderer = new Box2DDebugRenderer();

        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(1280/4f, 1);
        Body groundBody = world.createBody(groundBodyDef);

        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(1280/2f, 2);
        groundBody.createFixture(groundBox, 0.0f);
        groundBox.dispose();
        listener=new GameContactListener(trashCanInstances, trashBagInstances, objectInstances,specialInstances);
        world.setContactListener(listener);
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
        float width=0,height=0,density=0,radius=0,friction=0,restitution=0;
        float xDiff=0,yDiff=0,linearDamping=0,angularDamping=0;
        private Body body;
        boolean makeBox=true,makeCircle=false;

        public ObjectInstance(TextureRegion tex,float x,float y,float rotation,float size,int id){
            object=new Sprite(tex);
            object.setPosition(x,y);
            object.setScale(size);
            object.setRotation(rotation);
            this.id=id;
            switch(id){
                case 0:{
                    width=7;
                    height=object.getHeight();
                    density=200;
                    friction=10;
                    restitution=0.6f;
                }break;
                case 1:{
                    width=14;
                    height=object.getHeight()/1.4f;
                    xDiff=3;
                    density=2000;
                    friction=10;
                    restitution=0.7f;
                }break;
                case 2:{
                    makeBox=false;
                    makeCircle=true;
                    radius = (object.getWidth() * object.getScaleX()) / 2;
                    density=30;
                    friction=10;
                    restitution=0.8f;
                }break;
                case 3:{
                    width=16;
                    height=object.getHeight()/1.2f;
                    density=2000;
                    friction=10;
                    restitution=0.7f;
                }break;
                case 4:{
                    width=28;
                    xDiff=-2;
                    height=object.getHeight()/1.2f;
                    density=200;
                    friction=10;
                    restitution=0.4f;
                }break;
                case 5:{
                    width=object.getWidth()/1.2f;
                    xDiff=-2;
                    yDiff=2;
                    height=object.getHeight()/1.7f;
                    density=200;
                    friction=10;
                    restitution=0.4f;
                }break;
                case 6:{
                    width=15;
                    xDiff=0;
                    height=object.getHeight()/1.15f;
                    density=20;
                    friction=30;
                    restitution=0.4f;
                }break;
                case 7:{
                    width=30;
                    xDiff=0;
                    yDiff=2;
                    height=object.getHeight()/1.55f;
                    density=200;
                    friction=30;
                    restitution=0.3f;
                }break;
                case 8:{
                    width=30;
                    xDiff=0;
                    yDiff=0;
                    height=object.getHeight()/1.1f;
                    density=4000;
                    friction=40;
                    restitution=0.2f;
                }break;
                case 9:{
                    width=object.getWidth();
                    xDiff=0;
                    yDiff=0;
                    height=object.getHeight()/1.5f;
                    density=4000;
                    friction=40;
                    restitution=0.3f;
                }break;
                case 10:{
                    width=40;
                    xDiff=0;
                    yDiff=15;
                    height=object.getHeight()/1.6f;
                    density=4000;
                    friction=40;
                    restitution=0.3f;
                }break;
                case 11:{
                    width=26;
                    xDiff=0;
                    yDiff=13;
                    height=object.getHeight()/1.4f;
                    density=4000;
                    friction=40;
                    restitution=0.3f;
                }break;
                case 12:{
                    width=object.getWidth()/1.35f;
                    xDiff=0;
                    yDiff=0;
                    height=object.getHeight()/1.4f;
                    density=4000;
                    friction=40;
                    restitution=0.3f;
                }break;
                case 13:{
                    linearDamping=0.5f;
                    angularDamping=0.5f;
                    makeCircle=true;
                    makeBox=false;
                    radius = (object.getWidth()/2f * object.getScaleX()) / 2;
                    xDiff=0;
                    yDiff=24;
                    density=4000;
                    friction=40;
                    restitution=0.3f;
                }break;
                case 14:{
                    width=object.getWidth()/1.35f;
                    xDiff=0;
                    yDiff=0;
                    height=object.getHeight()/1.4f;
                    density=4000;
                    friction=40;
                    restitution=0.4f;
                }break;
                case 15:{
                    width=object.getWidth()/1.35f;
                    xDiff=0;
                    yDiff=0;
                    height=object.getHeight()/1.1f;
                    density=4000;
                    friction=40;
                    restitution=0.3f;
                }break;
                case 16:{
                    width=object.getWidth()/1.35f;
                    xDiff=0;
                    yDiff=40;
                    height=object.getHeight()/3f;
                    density=4000;
                    friction=40;
                    restitution=0.3f;
                }break;
            }
            if(makeBox)createBox(x, y,width,height,density,friction,restitution, size, world);
            if(makeCircle) createCircle(x, y,radius,size,density,friction,restitution, world);
        }

        private void createCircle(float x, float y,float radius,float size,float density,float friction,float restitution,World world){
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x, y);
            body = world.createBody(bodyDef);
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(radius);
            body.setTransform(new Vector2(x, y), object.getRotation()* MathUtils.degreesToRadians);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = circleShape;
            fixtureDef.density = density;
            fixtureDef.friction = friction;
            fixtureDef.restitution = restitution;
            body.createFixture(fixtureDef);
            circleShape.dispose();
            body.setLinearDamping(linearDamping);
            body.setAngularDamping(angularDamping);
        }

        private void createBox(float x, float y,float width,float height,float density,float friction,float restitution, float size, World world) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x, y);
            bodyDef.angle = (float) Math.toRadians(object.getRotation());

            body = world.createBody(bodyDef);
            body.setTransform(new Vector2(x, y), object.getRotation()* MathUtils.degreesToRadians);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width / 2f * object.getScaleX(), height / 2f * object.getScaleY());

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = density;
            fixtureDef.friction = friction;
            fixtureDef.restitution = restitution;

            body.createFixture(fixtureDef);

            shape.dispose();
            body.setLinearDamping(linearDamping);
            body.setAngularDamping(angularDamping);

        }

        public void render(SpriteBatch sb){
            object.setPosition(body.getPosition().x+xDiff - object.getWidth() / 2, body.getPosition().y+yDiff - object.getHeight() / 2);
            object.setRotation((float) Math.toDegrees(body.getAngle()));
            object.draw(sb);
        }
    }
    public class SpecialInstance{
        Sprite object;
        int id;
        boolean triggered=false;
        float width=0,height=0,density=0,radius=0,friction=0,restitution=0;
        float xDiff=0,yDiff=0,linearDamping=0,angularDamping=0;
        private Body body;
        boolean makeBox=true,makeCircle=false;

        public SpecialInstance(TextureRegion tex,float x,float y,float rotation,float size,int id){
            object=new Sprite(tex);
            object.setPosition(x,y);
            object.setScale(size);
            object.setRotation(rotation);
            this.id=id;
            switch(id){
                case 0:{
                    width=58;
                    yDiff=6;
                    height=object.getHeight()/1.2f;
                    density=2000;
                    friction=10;
                    restitution=0.7f;
                }break;
                case 1:{
                    width=58;
                    yDiff=6;
                    height=object.getHeight()/1.2f;
                    density=2000;
                    friction=10;
                    restitution=0.7f;
                }break;
                case 2:{
                    width=52;
                    yDiff=6;
                    height=object.getHeight()/1.5f;
                    density=2000;
                    friction=10;
                    restitution=0.4f;
                }break;
                case 3:{
                    width=52;
                    yDiff=6;
                    height=object.getHeight()/1.5f;
                    density=2000;
                    friction=10;
                    restitution=0.4f;
                }break;
                case 4:{
                    width=52;
                    yDiff=6;
                    height=object.getHeight()/1.5f;
                    density=2000;
                    friction=10;
                    restitution=0.4f;
                }break;
                case 5:{
                    width=52;
                    yDiff=25;
                    height=object.getHeight()/3f;
                    density=2000;
                    friction=10;
                    restitution=0.4f;
                }break;
                case 6:{
                    width=52;
                    yDiff=25;
                    height=object.getHeight()/3f;
                    density=2000;
                    friction=10;
                    restitution=0.4f;
                }break;
                case 7:{
                    width=52;
                    yDiff=25;
                    height=object.getHeight()/3f;
                    density=2000;
                    friction=10;
                    restitution=0.4f;
                }break;
                case 8:{
                    width=38;
                    yDiff=10;
                    height=50;
                    density=2000;
                    friction=10;
                    restitution=0.4f;
                }break;

            }
            if(makeBox)createBox(x, y,width,height,density,friction,restitution, size, world);
            if(makeCircle) createCircle(x, y,radius,size,density,friction,restitution, world);

        }

        private void createCircle(float x, float y,float radius,float size,float density,float friction,float restitution,World world){
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x, y);
            body = world.createBody(bodyDef);
            body.setTransform(new Vector2(x, y), object.getRotation()* MathUtils.degreesToRadians);


            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(radius);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = circleShape;
            fixtureDef.density = density;
            fixtureDef.friction = friction;
            fixtureDef.restitution = restitution;
//            fixtureDef.isSensor=true;
            body.createFixture(fixtureDef);
            circleShape.dispose();
            body.setLinearDamping(linearDamping);
            body.setAngularDamping(angularDamping);
        }

        private void createBox(float x, float y,float width,float height,float density,float friction,float restitution, float size, World world) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x, y);
            bodyDef.angle = (float) Math.toRadians(object.getRotation());
            body = world.createBody(bodyDef);
            body.setTransform(new Vector2(x, y), object.getRotation()* MathUtils.degreesToRadians);


            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width / 2f * object.getScaleX(), height / 2f * object.getScaleY());
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = density;
            fixtureDef.friction = friction;
            fixtureDef.restitution = restitution;
//            fixtureDef.isSensor=true;
            body.createFixture(fixtureDef);
            shape.dispose();
            body.setLinearDamping(linearDamping);
            body.setAngularDamping(angularDamping);
        }

        public void render(SpriteBatch sb){
            object.setPosition(body.getPosition().x+xDiff - object.getWidth() / 2, body.getPosition().y+yDiff - object.getHeight() / 2);
            object.setRotation((float) Math.toDegrees(body.getAngle()));
            object.draw(sb);
        }


    }
    public class TrashBagInstance{
        Sprite object;
        int id;
        float width=0,height=0,density=0,radius=0,friction=0,restitution=0,scale=0;
        float xDiff=0,yDiff=0,linearDamping=0,angularDamping=0;
        private Body body;
        boolean makeBox=true,makeCircle=false;

        public TrashBagInstance(TextureRegion tex,float x,float y,float rotation,int id,float size){
            object=new Sprite(tex);
            object.setPosition(x,y);
            this.scale=size;
            object.setScale(size);
            object.setRotation(rotation);
            this.id=id;

            switch(id) {
                case 0:{
                    makeBox=false;
                    makeCircle=true;
                    radius = (object.getWidth() * object.getScaleX()) / 2;
                    density=30;
                    friction=30;
                    restitution=0.3f;
                }break;
                case 1:{
                    makeBox=false;
                    makeCircle=true;
                    radius = (object.getWidth() * object.getScaleX()) / 2;
                    density=30;
                    friction=30;
                    restitution=0.3f;
                }break;
                case 2:{
                    makeBox=false;
                    makeCircle=true;
                    radius = (object.getWidth() * object.getScaleX()) / 2;
                    density=30;
                    friction=30;
                    restitution=0.3f;
                }break;
                case 3:{
                    makeBox=false;
                    makeCircle=true;
                    radius = (object.getWidth() * object.getScaleX()) / 2;
                    density=30;
                    friction=30;
                    restitution=0.3f;
                }break;

            }
            if(makeBox)createBox(x, y,width,height,density,friction,restitution, size, world);
            if(makeCircle) createCircle(x, y,radius,size,density,friction,restitution, world);




        }
        private void createCircle(float x, float y,float radius,float size,float density,float friction,float restitution,World world){
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x, y);
            body = world.createBody(bodyDef);
            body.setTransform(new Vector2(x, y), object.getRotation()* MathUtils.degreesToRadians);

            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(radius);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = circleShape;
            fixtureDef.density = density;
            fixtureDef.friction = friction;
            fixtureDef.restitution = restitution;
            body.createFixture(fixtureDef);
            circleShape.dispose();
            body.setLinearDamping(linearDamping);
            body.setAngularDamping(angularDamping);
        }

        private void createBox(float x, float y,float width,float height,float density,float friction,float restitution, float size, World world) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x, y);
            bodyDef.angle = (float) Math.toRadians(object.getRotation());
            body = world.createBody(bodyDef);
            body.setTransform(new Vector2(x, y), object.getRotation()* MathUtils.degreesToRadians);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width / 2f * object.getScaleX(), height / 2f * object.getScaleY());
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = density;
            fixtureDef.friction = friction;
            fixtureDef.restitution = restitution;
            body.createFixture(fixtureDef);
            shape.dispose();
            body.setLinearDamping(linearDamping);
            body.setAngularDamping(angularDamping);
        }
        public void render(SpriteBatch sb){


            object.setPosition(body.getPosition().x+xDiff - object.getWidth() / 2, body.getPosition().y+yDiff - object.getHeight() / 2);
            object.setRotation((float) Math.toDegrees(body.getAngle()));
            object.draw(sb);
        }
    }
    public class TrashCanInstance{
        Sprite object;
        int id;
        float width=0,height=0,density=0,radius=0,friction=0,restitution=0;
        float xDiff=0,yDiff=0,linearDamping=0,angularDamping=0;
        private Body body;
        boolean makeBox=true,makeCircle=false;

        public TrashCanInstance(TextureRegion tex,float x,float y,float rotation,int id,float size){
            object=new Sprite(tex);
            object.setPosition(x,y);
            object.setScale(size);
            object.setRotation(rotation);
            this.id=id;
            switch(id) {
                case 0:{
                    width=object.getWidth()/1.5f;
                    height=object.getHeight()/1.1f;
                    density=30;
                    friction=30;
                    restitution=0.3f;
                }break;
                case 1:{
                    width=object.getWidth()/1.5f;
                    height=object.getHeight()/1.1f;
                    density=30;
                    friction=30;
                    restitution=0.3f;
                }break;
                case 2:{
                    width=object.getWidth()/1.5f;
                    height=object.getHeight()/1.1f;
                    density=30;
                    friction=30;
                    restitution=0.3f;
                }break;
                case 3:{
                    width=object.getWidth()/1.5f;
                    height=object.getHeight()/1.1f;
                    density=30;
                    friction=30;
                    restitution=0.3f;
                }break;


            }
            if(makeBox)createBox(x, y,width,height,density,friction,restitution, size, world);
            if(makeCircle) createCircle(x, y,radius,size,density,friction,restitution, world);

        }
        private void createCircle(float x, float y,float radius,float size,float density,float friction,float restitution,World world){
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x, y);
            body = world.createBody(bodyDef);
            body.setTransform(new Vector2(x, y), object.getRotation()* MathUtils.degreesToRadians);

            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(radius);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = circleShape;
            fixtureDef.density = density;
            fixtureDef.friction = friction;
            fixtureDef.restitution = restitution;
            body.createFixture(fixtureDef);
            circleShape.dispose();
            body.setLinearDamping(linearDamping);
            body.setAngularDamping(angularDamping);
        }

        private void createBox(float x, float y,float width,float height,float density,float friction,float restitution, float size, World world) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.KinematicBody;
            bodyDef.position.set(x, y);
            bodyDef.angle = (float) Math.toRadians(object.getRotation());
            body = world.createBody(bodyDef);
            body.setTransform(new Vector2(x, y), object.getRotation()* MathUtils.degreesToRadians);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width / 2f * object.getScaleX(), height / 2f * object.getScaleY());
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = density;
            fixtureDef.friction = friction;
            fixtureDef.restitution = restitution;
            body.createFixture(fixtureDef);
            shape.dispose();
            body.setLinearDamping(linearDamping);
            body.setAngularDamping(angularDamping);
        }
        public void render(SpriteBatch sb){
            object.setPosition(body.getPosition().x+xDiff - object.getWidth() / 2, body.getPosition().y+yDiff - object.getHeight() / 2);
            object.setRotation((float) Math.toDegrees(body.getAngle()));

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
            button.setPosition(1280/2f-(60*gameButtonSheet.length)+id*42,720/2f-55);
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
        world = new World(new Vector2(0, -9.8f*5), true);
        worldSet();
        shapeRenderer=new ShapeRenderer();
        gameButtonSheet=extractSprite(files("game_button_sheet.png"),64,64);
        objectSheet=extractSprite(files("objects_sheet.png"),64,64);
        specialSheet=extractSprite(files("special_object_sheet.png"),64,64);
        trashCanSheet=extractSprite(files("trashcan_sheet.png"),192,192);
        trashBagSheet=extractSprite(files("trashbag_sheet.png"),128,128);
        nextGameButton=new Sprite(new Texture(files("nextLevel.png")));
        nextGameButton.setPosition(1280/4f-nextGameButton.getWidth()/2f,720/4f-nextGameButton.getHeight()/2f);


        int index=0;
        for(String name : gameButtonNames){
            gameButtonList.add(new GameButton(gameButtonSheet[index],name,index));
            index++;
        }

        backgrounds=new TextureRegion[3];
        for(int i=1;i<4;i++){
            backgrounds[i-1]=new TextureRegion(new Texture(files("gameBG_"+i+".png")));
        }



    }

    private void initializeWorld() {
        for(GameMap map : gameMap){
            switch(map.type){
                case 0:{
                    trashBagInstances.add(new TrashBagInstance(trashBagSheet[map.id],map.x,map.y,map.rotation,map.id,map.scale));
                }break;
                case 1:{
                    trashCanInstances.add(new TrashCanInstance(trashCanSheet[map.id],map.x,map.y,map.rotation,map.id,map.scale));
                }break;
                case 2:{
                    objectInstances.add(new ObjectInstance(objectSheet[map.id],map.x,map.y,map.rotation,map.scale,map.id));
                }break;
                case 3:{
                    specialInstances.add(new SpecialInstance(specialSheet[map.id],map.x,map.y,map.rotation,map.scale,map.id));
                }break;
            }
        }
    }


    public void setGameMap(String gameMapSchema){
        gameMap=new Array<>();
        gameMapParser(gameMapSchema);
        initializeWorld();
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
            time+=delta;
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

        if(gameWon){
            nextGameButton.draw(batch);
        }
        batch.end();
        debugRenderer.render(world, camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.end();
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
                if(nextGameButton.getBoundingRectangle().contains(point)&&gameWon&&!gameRun){
                    game.setLevelScreen();
                }
                for(GameButton btn :gameButtonList){
                    if(btn.button.getBoundingRectangle().contains(point)){
                        switch(btn.name){
                            case "slow":{
                                simulationSpeed=0.3f;
                            }break;
                            case "fast":{
                                simulationSpeed=3f;
                            }break;
                            case "restart":{
//                                world.dispose();
                                worldSet();
//                                objectInstances.clear();
//                                specialInstances.clear();
//                                trashBagInstances.clear();
//                                trashCanInstances.clear();
                                initializeWorld();
                            }break;
                            case "pause":{
                                gameRun=!gameRun;
                            }break;
                            case "play":{
                                gameRun=true;
                                simulationSpeed=1f;
                            }break;
                            case "back":{
                                game.setLevelScreen();
                            }break;
                            case "home":{
                                game.setMenuScreen();
                            }break;

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
        for(TextureRegion tex : gameButtonSheet) tex.getTexture().dispose();
        for(TextureRegion tex : backgrounds) tex.getTexture().dispose();
        for(TextureRegion tex : objectSheet) tex.getTexture().dispose();
        for(TextureRegion tex : specialSheet) tex.getTexture().dispose();
        for(TextureRegion tex : trashCanSheet) tex.getTexture().dispose();
        for(TextureRegion tex : trashBagSheet) tex.getTexture().dispose();
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


    public class GameContactListener implements ContactListener {
        private Array<TrashCanInstance> trashcans;
        private Array<TrashBagInstance> trashbags;
        private Array<ObjectInstance> objects;
        private Array<SpecialInstance> specialObjects;
        public GameContactListener(Array<TrashCanInstance> trashcans,Array<TrashBagInstance> trashbags,Array<ObjectInstance> objects, Array<SpecialInstance> specialObjects) {
            this.trashcans = trashcans;
            this.specialObjects = specialObjects;
            this.objects=objects;
            this.trashbags=trashbags;
        }

        @Override
        public void endContact(Contact contact) {
            // Implement if needed
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {
            // Implement if needed
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {
            // Implement if needed
        }

        @Override
        public void beginContact(Contact contact) {
            Fixture fixtureA = contact.getFixtureA();
            Fixture fixtureB = contact.getFixtureB();

//            Fixture otherFixture = (fixtureA.isSensor()) ? fixtureB : fixtureA;
//            Body otherBody = otherFixture.getBody();

            for (SpecialInstance specialObj : specialObjects) {
                if (isContactWithTrashCan(fixtureA, fixtureB) ||
                    isContactWithTrashBag(fixtureA, fixtureB) ||
                    isContactWithObject(fixtureA, fixtureB)) {

                    if(!specialObj.triggered && time>2f){


                        specialObj.triggered = true;
                        switch(specialObj.id){
                            case 0:{
                                Vector2 force = new Vector2(0, 700f); // Force vector (change magnitude as needed)
                                fixtureA.getBody().applyForceToCenter(force, true);
                                fixtureB.getBody().applyForceToCenter(force, true);
                            }break;

                            case 1:{
                                Vector2 force = new Vector2(0, 2000f); // Force vector (change magnitude as needed)
                                fixtureA.getBody().applyForceToCenter(force, true);
                                fixtureB.getBody().applyForceToCenter(force, true);
                            }break;
                            case 2:{
                                print("beam sent");
                                float angle = specialObj.object.getRotation();
                                Vector2 direction = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle));
//                                otherBody.applyLinearImpulse(direction.scl(80000), otherBody.getWorldCenter(), true);

                            }break;

                            case 6:{
                                world.setGravity(new Vector2(0,-world.getGravity().y));
                            }break;
                        }
                    }
                }
            }
            for(TrashBagInstance bag : trashbags){
                if(isContactWithTrashCanAbsolute(fixtureA,fixtureB,bag.id)){
                   gameWon=true;
                   gameRun=false;
                   trashbags.removeValue(bag,true);
                }
            }
        }
        private boolean isContactWithTrashCan(Fixture fixtureA, Fixture fixtureB) {
            for (TrashCanInstance trashCan : trashcans) {
                if (trashCan.body.getFixtureList().contains(fixtureA, true) ||
                    trashCan.body.getFixtureList().contains(fixtureB, true)) {
                    return true;
                }
            }
            return false;
        }
        private boolean isContactWithTrashCanAbsolute(Fixture fixtureA, Fixture fixtureB,int id) {
            for (TrashCanInstance trashCan : trashcans) {
                if (trashCan.body.getFixtureList().contains(fixtureA, true) ||
                    trashCan.body.getFixtureList().contains(fixtureB, true)) {
                    if(trashCan.id==id)return true;
                }
            }
            return false;
        }

        private boolean isContactWithTrashBag(Fixture fixtureA, Fixture fixtureB) {
            for (TrashBagInstance trashBag : trashbags) {
                if (trashBag.body.getFixtureList().contains(fixtureA, true) ||
                    trashBag.body.getFixtureList().contains(fixtureB, true)) {
                    return true;
                }
            }
            return false;
        }

        private boolean isContactWithObject(Fixture fixtureA, Fixture fixtureB) {
            for (ObjectInstance object : objects) {
                if (object.body.getFixtureList().contains(fixtureA, true) ||
                    object.body.getFixtureList().contains(fixtureB, true)) {
                    return true;
                }
            }
            return false;
        }
    }


}
