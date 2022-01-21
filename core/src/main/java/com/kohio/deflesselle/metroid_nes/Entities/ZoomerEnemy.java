package com.kohio.deflesselle.metroid_nes.Entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.kohio.deflesselle.metroid_nes.Metroid;

import static com.kohio.deflesselle.metroid_nes.Metroid.GROUND_BIT;

import java.util.LinkedList;

public class ZoomerEnemy extends Enemy{

    private LinkedList<Vector2> normals = new LinkedList<>();

    private final Type color;
    @SuppressWarnings("FieldCanBeLocal")
    private final float FRAME_DURATION = .2f;
    private final boolean orient;

    private final Vector2 speedXY;
    private float rotationAngleBuffer = 0f;
    private boolean canRotate = false;

    public ZoomerEnemy(float x, float y, boolean orientation, float width, float height, float speed, Type color,
                       World world, TextureAtlas atlas) {
        super(x, y, width, height, speed, world, atlas);
        this.color = color;
        orient = orientation;
//        Gdx.app.log("ZoomerEnemy", "Orientation : " + orientation);
        if (!orientation) speed = -speed;
        speedXY = new Vector2(speed, 0);
        defineEnemy(x, y);
        defineAnimations();
    }

    @Override
    protected void defineAnimations() {
        int index = 5;
        if (color == Type.Orange) index = 3;
        else if (color == Type.Red) index = 4;
        setRegion(atlas.getRegions().get(index));
        setBounds(0, 0, width, height);
    }

    @Override
    public void defineEnemy(float x, float y) {

        //Enemy type specific numbers
        switch (color) {
            case Yellow:
                this.totalHealth = 2;
                this.currentHealth = 2;
                this.contactDamage = 7;
                break;
            case Orange:
                this.totalHealth = 3;
                this.currentHealth = 3;
                this.contactDamage = 10;
                break;
            case Red:
                this.totalHealth = 4;
                this.currentHealth = 4;
                this.contactDamage = 15;
                break;
        }

        //Box2D body definition
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        bdef.position.set(x, y);
        bdef.type = BodyDef.BodyType.DynamicBody;
        this.body = world.createBody(bdef);
        this.body.setGravityScale(0f);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((width - .2f) / 2, height  / 2);

        fdef.shape = shape;
        fdef.filter.categoryBits = Metroid.ENEMY_BIT;
        fdef.filter.maskBits = GROUND_BIT;
        fdef.isSensor = true;

        this.body.createFixture(fdef).setUserData(this);

        // If initial orientation is right
        if (orient){
            // Bottom detector for enemy movement
            EdgeShape groundDetector = new EdgeShape();
            EdgeShape wallDetector = new EdgeShape();
            groundDetector.set(
                    new Vector2((-width + .1f) / 2, (-height +.2f) / 2),
                    new Vector2((-width + .1f) / 2, (-height - .5f) / 2)
            );

            fdef.shape = groundDetector;
            fdef.filter.categoryBits = Metroid.ZOOMER_GROUND_BIT;
            this.body.createFixture(fdef).setUserData(this);

            wallDetector.set(
                    new Vector2((width - .2f) / 2, 0),
                    new Vector2((width - .2f) / 2, 0)
            );

            fdef.shape = wallDetector;
            fdef.filter.categoryBits = Metroid.ZOOMER_WALL_BIT;
            this.body.createFixture(fdef).setUserData(this);
        }
        else {
            EdgeShape groundDetector = new EdgeShape();
            EdgeShape wallDetector = new EdgeShape();
            groundDetector.set(
                    new Vector2((width - .1f) / 2, -height / 2),
                    new Vector2((width - .1f) / 2, -height / 2 - .5f)
            );

            fdef.shape = groundDetector;
            fdef.filter.categoryBits = Metroid.ZOOMER_GROUND_BIT;
            this.body.createFixture(fdef).setUserData(this);

            wallDetector.set(
                    new Vector2((-width + .2f) / 2, 0),
                    new Vector2((-width + .1f) / 2, 0)
            );

            fdef.shape = wallDetector;
            fdef.filter.categoryBits = Metroid.ZOOMER_WALL_BIT;
            this.body.createFixture(fdef).setUserData(this);
        }

        this.body.setLinearVelocity(speedXY);
    }


    @Override
    public void update(float deltaTime) {
        animate(deltaTime);
        Vector2 pos = body.getPosition();
        if (rotationAngleBuffer != 0f) {
            this.body.setTransform(this.body.getWorldCenter(), rotationAngleBuffer);
            rotationAngleBuffer = 0f;
        }
        setPosition(pos.x - width / 2f, pos.y - height / 2f);
    }

    @Override
    public void animate(float deltaTime) {

        if (stateTimer < FRAME_DURATION) {
            stateTimer += deltaTime;
        } else {
            flip(true,false);
            stateTimer = 0;
        }
    }

    public void setRotatePossible(boolean value) {
        canRotate = value;
    }

    public void changeDirection(boolean clockWise){

        rotate90(clockWise);

        if (clockWise) {
            speedXY.rotate90(-1);
            rotationAngleBuffer = this.body.getAngle() + MathUtils.degRad * 270;
        } else {
            speedXY.rotate90(1);
            rotationAngleBuffer = this.body.getAngle() + MathUtils.degRad * 90;
        }

        this.body.setLinearVelocity(speedXY);
    }

    @Override
    public void onCollision() {

    }

    public boolean canRotate() {
        return canRotate;
    }

    public void addNormal(Vector2 normal) {
        normals.add(normal);
    }

    public LinkedList<Vector2> getNormals() {
        return normals;
    }

    public enum Type {
        Yellow,
        Orange,
        Red
    }
}
