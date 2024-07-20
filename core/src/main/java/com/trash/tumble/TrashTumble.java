package com.trash.tumble;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class TrashTumble extends Game {
    public SpriteBatch batch;

    private StartMenu startMenu;
    private GameScreen gameScreen;
    private LevelEdit levelEdit;
    private PauseScreen pauseScreen;
    public InputMultiplexer inputMultiplexer;


    @Override
    public void create() {
        Gdx.graphics.setCursor( Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("cursor.png")), 0, 0));
        batch=new SpriteBatch();
        startMenu=new StartMenu(this);
        gameScreen=new GameScreen(this);
        levelEdit=new LevelEdit(this);
        pauseScreen=new PauseScreen(this);
        setScreen(startMenu);
    }

    public void startGame(){
        this.setScreen(gameScreen);
    }
    public void startEdit(){
        this.setScreen(levelEdit);
    }
    public void pauseScreen(){
        this.setScreen(pauseScreen);
    }
    public void setMenuScreen(){
        this.setScreen(startMenu);
    }

    @Override
    public void render(){
        super.render();
    }
    @Override
    public void dispose(){
        batch.dispose();
    }

}
