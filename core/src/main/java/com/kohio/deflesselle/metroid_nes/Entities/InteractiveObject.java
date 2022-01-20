package com.kohio.deflesselle.metroid_nes.Entities;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.kohio.deflesselle.metroid_nes.Metroid;

public abstract class InteractiveObject {

    protected final World world;
    protected final TiledMap map;
    protected final Rectangle bounds;
    protected final Body body;

    protected final Fixture fixture;

    public InteractiveObject(World world, TiledMap map, Rectangle bounds){
        this.world = world;
        this.map = map;
        this.bounds = bounds;

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX() + bounds.getWidth() / 2) / Metroid.PPU, (bounds.getY() + bounds.getHeight()  / 2) / Metroid.PPU);

        body = world.createBody(bdef);

        shape.setAsBox(bounds.getWidth() / 2 / Metroid.PPU, bounds.getHeight() / 2 / Metroid.PPU);
        fdef.shape = shape;
        fdef.isSensor = true;
        fixture = body.createFixture(fdef);
    }

    public abstract void onCollision();

    public void setCategoryFilter(short category){
        Filter f = new Filter();
        f.categoryBits = category;
        fixture.setFilterData(f);
    }

    public TiledMapTileLayer.Cell getCell(String layerName){
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(layerName);
        Vector2 coords = body.getPosition();
        return layer.getCell((int) coords.x, (int) coords.y);
    }

}
