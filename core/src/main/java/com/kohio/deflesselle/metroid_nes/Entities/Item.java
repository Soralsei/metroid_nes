package com.kohio.deflesselle.metroid_nes.Entities;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.kohio.deflesselle.metroid_nes.Metroid;

public class Item extends InteractiveObject{

    public Item(World world, TiledMap map, Rectangle bounds) {
        super(world, map, bounds);
        setCategoryFilter(Metroid.ITEM_BIT);
        fixture.setUserData(this);
    }

    @Override
    public void onCollision() {}

}
