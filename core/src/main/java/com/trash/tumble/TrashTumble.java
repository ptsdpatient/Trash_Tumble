package com.trash.tumble;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
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
public class TrashTumble extends ApplicationAdapter {

    SpriteBatch batch;
    World world;
    Box2DDebugRenderer debugRenderer;

    @Override
    public void create() {
        batch = new SpriteBatch();

        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();

        // Create ground body with custom width and height
        createGround(640, 20); // Wide ground across the screen

        // Create multiple objects with different shapes and properties
        createFallingObject(new Vector2(320, 480), "box", 1f, 0.5f, 0.3f);
        createFallingObject(new Vector2(340, 480), "circle", 0.5f, 0.2f, 0.6f);
        createFallingObject(new Vector2(360, 480), "polygon", 2f, 0.8f, 0.1f);
    }

    private void createGround(float width, float height) {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(width / 2f, height / 2f);  // Position at the center of the bottom edge
        Body groundBody = world.createBody(groundBodyDef);

        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(width / 2f, height / 2f);  // Set the ground dimensions
        groundBody.createFixture(groundBox, 0);
        groundBox.dispose();
    }

    private void createFallingObject(Vector2 position, String shapeType, float density, float friction, float restitution) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);

        Body body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;

        switch (shapeType) {
            case "box":
                PolygonShape box = new PolygonShape();
                box.setAsBox(50, 50); // Larger box
                fixtureDef.shape = box;
                break;
            case "circle":
                CircleShape circle = new CircleShape();
                circle.setRadius(25f); // Larger circle
                fixtureDef.shape = circle;
                break;
            case "polygon":
                PolygonShape polygon = new PolygonShape();
                polygon.set(new Vector2[]{
                    new Vector2(-50, 0), new Vector2(-25, 50),
                    new Vector2(25, 50), new Vector2(50, 0),
                    new Vector2(0, -50)
                });
                fixtureDef.shape = polygon;
                break;
        }

        body.createFixture(fixtureDef);
        if (shapeType.equals("box")) {
            ((PolygonShape) fixtureDef.shape).dispose();
        } else if (shapeType.equals("circle")) {
            ((CircleShape) fixtureDef.shape).dispose();
        } else if (shapeType.equals("polygon")) {
            ((PolygonShape) fixtureDef.shape).dispose();
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(1 / 60f, 6, 2);

        debugRenderer.render(world, batch.getProjectionMatrix());
    }

    @Override
    public void dispose() {
        batch.dispose();
        world.dispose();
        debugRenderer.dispose();
    }
}
