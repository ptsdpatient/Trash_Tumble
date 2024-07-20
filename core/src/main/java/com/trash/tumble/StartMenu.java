package com.trash.tumble;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class StartMenu implements Screen {
    private TrashTumble game;
    private SpriteBatch batch;
    private BitmapFont font;
    Texture background;

    public StartMenu(TrashTumble game){
        this.game=game;
        this.batch=game.batch;
        font=new BitmapFont();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

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
}
