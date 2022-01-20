package com.kohio.deflesselle.metroid_nes.Entities;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.kohio.deflesselle.metroid_nes.Metroid;

public class Door extends InteractiveObject{

    public Door(World w, TiledMap m, Rectangle r) {
        super(w,m,r);
        setCategoryFilter(Metroid.DOOR_BIT);
        fixture.setUserData(this);
    }

    @Override
    public void onCollision() {}
}
