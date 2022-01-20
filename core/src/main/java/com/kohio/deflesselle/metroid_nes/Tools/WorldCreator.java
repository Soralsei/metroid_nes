package com.kohio.deflesselle.metroid_nes.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.kohio.deflesselle.metroid_nes.Entities.Door;
import com.kohio.deflesselle.metroid_nes.Entities.MorphBall;
import com.kohio.deflesselle.metroid_nes.Entities.ZoomerEnemy;
import com.kohio.deflesselle.metroid_nes.Screens.GameScreen;

import static com.kohio.deflesselle.metroid_nes.Metroid.PPU;

public class WorldCreator {

    private TiledMap map;
    private final GameScreen screen;
    private World world;

    public WorldCreator(World w, TiledMap m, GameScreen screen) {

        map = m;
        this.screen = screen;
        this.world = w;

        BodyDef bdef = new BodyDef();
        Body body;
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        //Setup ground body collisions
        for (MapObject object :
                m.getLayers().get("ground").getObjects().getByType(RectangleMapObject.class)) {

            Rectangle r = ((RectangleMapObject) object).getRectangle();
            Gdx.app.log("WorldCreation : ground X", Float.toString(r.x));
            Gdx.app.log("WorldCreation : ground Y", Float.toString(r.y));

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((r.getX() + r.getWidth() / 2) / PPU, (r.getY() + r.getHeight() / 2) / PPU);

            body = w.createBody(bdef);

            shape.setAsBox(r.getWidth() / 2 / PPU, r.getHeight() / 2 / PPU);
            fdef.shape = shape;
            body.createFixture(fdef).setUserData("ground");

        }

        for (MapObject object :
                m.getLayers().get("items_collision").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle r = ((RectangleMapObject)object).getRectangle();
            new MorphBall(w, m, r);
        }

        for (MapObject object :
                m.getLayers().get("door").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle r = ((RectangleMapObject)object).getRectangle();
            new Door(w, m, r);
        }

        loadEnemies();
    }

    private void loadEnemies() {
        for (MapObject object :
            map.getLayers().get("yellow_zoomer").getObjects().getByType(RectangleMapObject.class)) {
            MapProperties args = object.getProperties();
            Rectangle r = ((RectangleMapObject) object).getRectangle();
            screen.enemies.add(
                    new ZoomerEnemy(
                            (r.getX() + r.getWidth() / 2) / PPU,
                            (r.y + r.getHeight() / 2) / PPU,
                            args.get("orientation", Boolean.class),
                            1f, 1f, 2f,
                            ZoomerEnemy.Type.Yellow,
                            world, screen.getAtlas())
            );
        }
    }

}
