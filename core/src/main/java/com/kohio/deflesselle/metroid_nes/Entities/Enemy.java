package com.kohio.deflesselle.metroid_nes.Entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Enemy extends Sprite {

    protected final float width;
    protected final float height;

    protected final float speed;

    protected int totalHealth;
    protected int currentHealth;
    protected int contactDamage;

    protected final World world;
    protected final TextureAtlas atlas;
    public Body body;

    protected float stateTimer;

    public Enemy(float x, float y, float width, float height, float speed, World world, TextureAtlas atlas) {
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.world = world;
        this.atlas = atlas;
    }

    protected abstract void defineAnimations();
    public abstract void defineEnemy(float x, float y);
    public abstract void update(float deltaTime);
    public abstract void animate(float deltaTime);
    public abstract void onCollision();
    
}
