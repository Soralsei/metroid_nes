package com.kohio.deflesselle.metroid_nes.Entities;

import static com.kohio.deflesselle.metroid_nes.Metroid.PPU;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.kohio.deflesselle.metroid_nes.Metroid;

public class Player extends Sprite {

    private final World world;
    public Body body;

    //Edge Shapes
    private static final float EDGE_X1 = -6 / PPU;
    private static final float EDGE_X2 = 6 / PPU;
    private static final float EDGE_Y1 = 17 / PPU;
    private static final float EDGE_Y2 = -17 / PPU;
    //Animations
    private TextureRegion samusIdle;
    private Animation<TextureRegion> runAnim;
    private Animation<TextureRegion> curlAnim;
    private Animation<TextureRegion> ballMove;
    private Animation<TextureRegion> jumpAnim;
    private Animation<TextureRegion> spinJumpAnim;

    //State of movement
    private MoveState currentState;

    //Animation variables and states
    private AnimState currentAnimState;
    private AnimState previousAnimState;
    private float stateTimer = 0;

    //Player flags
    public boolean isMorphBall = false;
    public boolean isSpinJumping = false;
    public boolean isGrounded = true;
    public boolean isJumping = false;
    public boolean canStand = true;
    public boolean isGoingRight = false;
    public boolean isGoingLeft = false;
    public boolean jumpKeyHeld = false;

    //Util variables for player animation and orientation
    private static final float VERTICAL_THRESHOLD = 0.001f;
    public static final short LEFT = 1;
    public static final short RIGHT = 2;
    public short previous = LEFT;

    //Player variables like health and ammo
    public static final float JUMP = 13f;
    public static final float SPEED = 4.5f;
    private int energy;
    private int energyTanks = 0;
    private int missileTanks = 0;
    private int currentMissiles;
    //Inventory
    private short inventory;
    private boolean isInvincible = false;
    private float invincibleTimer = 0f;
    private static final float INVINCIBILITY_TIME = 2f;


    public Player(World world, TextureAtlas atlas){

        super();
        this.world = world;

        currentState = MoveState.IDLE;
        currentAnimState = AnimState.IDLE;
        previousAnimState = AnimState.IDLE;

        //Box2DBody definition
        definePlayer();

        //Animations setup
        defineAnimations(atlas);
        setBounds(0,0, 20/ PPU, 32/ PPU);
        setRegion(samusIdle);

        //Inventory setup
        inventory = 0;
    }

    //All player animations
    private void defineAnimations(TextureAtlas atlas) {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas.AtlasRegion texture = atlas.getRegions().get(0);

        samusIdle = new TextureRegion(texture, 0, 0, 20, 32);
        for (int i = 20; i < 80; i += 20) {
            frames.add(new TextureRegion(texture, i, 0, 20, 32));
        }
        runAnim = new Animation<>(.05f, frames, Animation.PlayMode.LOOP);

        TextureRegion prepJump = frames.get(0);
        frames.clear();
        frames.add(prepJump, new TextureRegion(texture, 80, 0, 20, 32));
        jumpAnim = new Animation<>(.05f, frames);

        frames.clear();
        for (int i = 100; i < 180; i += 20) {
            frames.add(new TextureRegion(texture, i, 0, 20, 32));
        }
        spinJumpAnim = new Animation<>( .03f,frames, Animation.PlayMode.LOOP);

        texture = atlas.getRegions().get(1);
        frames.clear();
        for (int i = 0; i < 60; i+=20) {
            frames.add(new TextureRegion(texture, i, 0, 20, 32));
        }
        curlAnim = new Animation<>(.06f, frames);

        frames.clear();
        for (int i = 60; i < 140; i+=20) {
            frames.add(new TextureRegion(texture, i, 0, 20, 32));
        }
        ballMove = new Animation<>(.04f, frames, Animation.PlayMode.LOOP);
        frames.clear();
    }

    //Box2D player definition
    private void definePlayer() {

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        bdef.position.set(40.5f,4);
        bdef.type = BodyDef.BodyType.DynamicBody;
        this.body = world.createBody(bdef);
        this.body.setFixedRotation(true);

        PolygonShape body = new PolygonShape();
        body.setAsBox(7.5f/ PPU, 15f/ PPU);

        fdef.shape = body;
        fdef.friction = 0;
        fdef.filter.categoryBits = Metroid.PLAYER_BIT;
        fdef.filter.maskBits = Metroid.GROUND_BIT | Metroid.ITEM_BIT | Metroid.DOOR_BIT | Metroid.ENEMY_BIT;

        this.body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(EDGE_X1, EDGE_Y1), new Vector2(EDGE_X2, EDGE_Y1));
        fdef.shape = head;
        fdef.isSensor = true;
        fdef.filter.categoryBits = Metroid.HEAD_BIT;
        fdef.filter.maskBits = Metroid.GROUND_BIT;
        fdef.density = 0;

        this.body.createFixture(fdef);

        EdgeShape feet = new EdgeShape();
        feet.set(new Vector2(EDGE_X1, EDGE_Y2), new Vector2(EDGE_X2, EDGE_Y2));
        fdef.shape = feet;
        fdef.filter.categoryBits = Metroid.FEET_BIT;

        this.body.createFixture(fdef);
    }

    public void update(float deltaTime){
        Vector2 pos = body.getPosition();
        setPosition(pos.x - getWidth() / 2, pos.y - getHeight() / 2);
        handleMovement();
        //############### Variable height jump #####################
        float yVel = body.getLinearVelocity().y;
        if (!jumpKeyHeld && yVel > 0){
            stopJump();
        }
        //Jumping flag
        if(yVel == 0) isJumping = false;
        //Invincibility frames
        if (invincibleTimer > 0) {
            invincibleTimer -= deltaTime;
        }
        else if (isInvincible){
            invincibleTimer = 0;
            isInvincible = false;
            Gdx.app.log("Player", "Invincibility stop");
        }
        animate(deltaTime);
    }

    private void handleMovement() {
        Vector2 vel = body.getLinearVelocity();
        if (currentState == Player.MoveState.RUN) {
            if (isGoingLeft && isGoingRight) {
                body.setLinearVelocity(new Vector2(0, vel.y));
            } else if (isGoingRight) {
                body.setLinearVelocity(new Vector2(SPEED, vel.y));
            } else {
                body.setLinearVelocity(new Vector2(-SPEED, vel.y));
            }
        } else {
            if (!isSpinJumping || isGrounded)
                body.setLinearVelocity(0, body.getLinearVelocity().y);
        }
    }

    private void animate(float deltaTime) {

        TextureRegion frame;

        previousAnimState = currentAnimState;
        currentAnimState = getAnimState();

        /*if (previousAnimState != currentAnimState)
            Gdx.app.log("Animation state : ", String.valueOf(currentAnimState));*/
        if(isMorphBall){
            frame = curlAnim.isAnimationFinished(stateTimer) ?
                    ballMove.getKeyFrame(stateTimer) : curlAnim.getKeyFrame(stateTimer);
        }
        else {
            switch (currentAnimState) {
                case RUN: {
                    frame = runAnim.getKeyFrame(stateTimer);
                    break;
                }
                case JUMP:
                case FALL:{
                        if (currentAnimState == AnimState.JUMP) {
                            frame = isSpinJumping ?
                                    spinJumpAnim.getKeyFrame(stateTimer) : jumpAnim.getKeyFrame(stateTimer);
                        }
                        else {
                            frame = isSpinJumping ?
                                    spinJumpAnim.getKeyFrame(stateTimer) : jumpAnim.getKeyFrame(.05f);
                        }
                    }
                    break;
                default:
                        frame = samusIdle;
            }
        }

        if (isGoingRight && isGoingLeft){
            if (previous == RIGHT && !frame.isFlipX()) {
                frame.flip(true, false);
            }
            else if (previous == LEFT && frame.isFlipX()) {
                frame.flip(true, false);
            }
        }
        else if (!isGoingRight && !isGoingLeft) {
            if (previous == LEFT && !frame.isFlipX()) {
                frame.flip(true, false);
            }
            else if (previous == RIGHT && frame.isFlipX()) {
                frame.flip(true, false);
            }
        }
        else {
            // Si à l'état précédent ou actual samus regardais à gauche
            // et que la texture n'est pas flip sur X, flip.
            if (isGoingLeft && !frame.isFlipX()) {
                frame.flip(true, false);
            }
            // Si à l'état précédent ou actual samus regardais à droite et
            // que la texture est flip sur X, remettre dans la texture à l'endroit.
            else if (isGoingRight && frame.isFlipX()) {
                frame.flip(true, false);
            }
        }
        if (currentAnimState == previousAnimState) {
            stateTimer += deltaTime;
        }
        else {
            if (!isMorphBall) stateTimer = 0;
        }
        previousAnimState = currentAnimState;
        setRegion(frame);
    }

    public void setCurrentState(MoveState state) {
        if(state != currentState){
            currentState = state;
        }
    }

    private AnimState getAnimState(){
        Vector2 vel = body.getLinearVelocity();
        if (vel.y > VERTICAL_THRESHOLD){
            return AnimState.JUMP;
        } else if (vel.y < -VERTICAL_THRESHOLD) {
            return AnimState.FALL;
        }else if (vel.x > 0 || vel.x < 0) {
            return AnimState.RUN;
        } else {
            return AnimState.IDLE;
        }
    }

    public void jump() {
        if (isJumping || currentAnimState == AnimState.FALL) {
            isSpinJumping = !isSpinJumping;
            float speed = previous == RIGHT ? SPEED : -SPEED;
            if (isSpinJumping) body.setLinearVelocity(speed, body.getLinearVelocity().y);
        }
        if(isGrounded && !isMorphBall){
            isJumping = true;
            body.applyLinearImpulse(new Vector2(0, JUMP), body.getWorldCenter(), true);
            Vector2 vel = body.getLinearVelocity();
            if (!isSpinJumping && (vel.x > 0 || vel.x < 0)) isSpinJumping = true;
        }
    }
    private void stopJump() {
        body.applyForce(new Vector2(0, -JUMP), body.getWorldCenter(), true);
    }

    public void morphBall() {
        if (isGrounded && inventoryContains(Metroid.MORPH_BALL) && !isMorphBall) {

            body.getFixtureList().first().setRestitution(.6f);
            isMorphBall = true;

            Array<Fixture> fixtures = body.getFixtureList();
            float centerY = body.getLocalCenter().y;
            ((PolygonShape) fixtures.first().getShape()).setAsBox(
                    7.5f/ PPU, 7.5f/ PPU
            );
            ((EdgeShape)fixtures.get(1).getShape())
                    .set(
                            new Vector2(EDGE_X1, 9.5f/ PPU),
                            new Vector2(EDGE_X2, 9.5f/ PPU)
                    );
            ((EdgeShape)fixtures.get(2).getShape())
                    .set(new Vector2(EDGE_X1, centerY - 9.5f / PPU), new Vector2(EDGE_X2, centerY - 9.5f / PPU));
            body.setAwake(true);
            stateTimer = 0;
        }
    }
    public void stand() {
        if(isMorphBall && canStand){
            Array<Fixture> fixtures = body.getFixtureList();
            body.getFixtureList().first().setRestitution(0);
            isMorphBall = false;
            ((PolygonShape) body.getFixtureList().first().getShape()).setAsBox(7.5f / PPU, 15f / PPU);
            ((EdgeShape)fixtures.get(1).getShape()).set(new Vector2(EDGE_X1, EDGE_Y1), new Vector2(EDGE_X2, EDGE_Y1));
            ((EdgeShape)fixtures.get(2).getShape()).set(new Vector2(EDGE_X1, EDGE_Y2), new Vector2(EDGE_X2, EDGE_Y2));
            body.setAwake(true);
            stateTimer = 0;
        }
    }

    public void onCollision(Object data) {
        if (data instanceof MorphBall) {
            inventory = (short) (inventory | Metroid.MORPH_BALL);
        }
        else if (data instanceof Enemy && !isInvincible) {
            Enemy enemy = (Enemy) data;
            damage(enemy.contactDamage);
            knockback(enemy.body.getPosition().x);
        }
    }

    private void knockback(float x) {
        Gdx.app.log("Player", "Knockback from " + body.getPosition().x + " to " + x);
    }
    private void damage(int contactDamage) {
        energy -= contactDamage;
        Gdx.app.log("Player", "Invincibility start");
        isInvincible = true;
        invincibleTimer = INVINCIBILITY_TIME;
    }

    private boolean inventoryContains(short item){
        return (inventory & item) == item;
    }

    public void setGrounded(boolean b) {
        isGrounded = b;
        if(isGrounded) isSpinJumping = false;
    }

    public void updatePrevious(int keycode) {
        if(keycode == Input.Keys.A){
            previous = LEFT;
        }
        else {
            previous = RIGHT;
        }
    }

    public enum MoveState{
        IDLE, RUN
    }

    public enum AnimState {
        IDLE, RUN, JUMP, FALL
    }
}
