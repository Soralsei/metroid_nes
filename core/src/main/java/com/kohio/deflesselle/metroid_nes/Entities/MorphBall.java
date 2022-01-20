package com.kohio.deflesselle.metroid_nes.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.kohio.deflesselle.metroid_nes.Metroid;

public class MorphBall extends Item{

    public MorphBall(World world, TiledMap map, Rectangle bounds) {
        super(world, map, bounds);
    }

    @Override
    public void onCollision(){
        Gdx.app.log("MorphBall", "Collision");
        setCategoryFilter(Metroid.DESTROYED_BIT);
        TiledMapTileLayer.Cell cell = getCell("solid");
        if (cell != null) getCell("solid").setTile(null);
    }
}
