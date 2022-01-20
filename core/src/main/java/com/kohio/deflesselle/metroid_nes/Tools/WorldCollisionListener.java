package com.kohio.deflesselle.metroid_nes.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.kohio.deflesselle.metroid_nes.Entities.Enemy;
import com.kohio.deflesselle.metroid_nes.Entities.InteractiveObject;
import com.kohio.deflesselle.metroid_nes.Entities.Player;
import com.kohio.deflesselle.metroid_nes.Metroid;
import com.kohio.deflesselle.metroid_nes.Screens.GameScreen;
import com.kohio.deflesselle.metroid_nes.Tools.Cutscenes.Events.ItemGetEvent;

public class WorldCollisionListener implements ContactListener {
    private final GameScreen screen;

    public WorldCollisionListener(GameScreen screen){
        this.screen = screen;
    }

    @Override
    public void beginContact(Contact contact) {

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        short bitA = fixA.getFilterData().categoryBits;
        short bitB = fixB.getFilterData().categoryBits;

        Object userDataA = fixA.getUserData();
        Object userDataB = fixB.getUserData();

        //Gdx.app.log("Collision", "bitA = " + bitA + " | bitB = " + bitB);
        //Gdx.app.log("Collision", String.valueOf(bitA|bitB));
        switch (bitA | bitB){
            case Metroid.FEET_BIT | Metroid.GROUND_BIT: {
                screen.setPlayerGrounded(true);
                break;
            }
            case Metroid.HEAD_BIT | Metroid.GROUND_BIT: {
                screen.setPlayerCanStand(false);
                break;
            }
            case Metroid.PLAYER_BIT | Metroid.ITEM_BIT: {
                Gdx.app.log("Collision", "Player | ITEM || DOOR");
                Object dataBody = bitA == Metroid.PLAYER_BIT ? userDataA : userDataB;
                Object dataO = dataBody == userDataA ? userDataB : userDataA;
                ((InteractiveObject) dataO).onCollision();
                ((Player) dataBody).onCollision(dataO);
                screen.queueEvent(
                        new ItemGetEvent(
                                screen.getGame().assetManager,
                                4.5f
                        )
                );
                break;
            }
            case Metroid .PLAYER_BIT | Metroid.DOOR_BIT : {
                Gdx.app.log("Collision", "Player | ITEM || DOOR");
                Object dataBody = bitA == Metroid.PLAYER_BIT ? userDataA : userDataB;
                Object dataO = dataBody == userDataA ? userDataB : userDataA;
                ((InteractiveObject) dataO).onCollision();
                ((Player) dataBody).onCollision(dataO);
                break;
            }
            case Metroid .PLAYER_BIT | Metroid.ENEMY_BIT : {
                Gdx.app.log("Collision", "Player | ITEM || ENEMY");
                Object dataBody = bitA == Metroid.PLAYER_BIT ? userDataA : userDataB;
                Object dataO = dataBody == userDataA ? userDataB : userDataA;
                ((Enemy) dataO).onCollision();
                ((Player) dataBody).onCollision(dataO);
                break;
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        short bitA = fixA.getFilterData().categoryBits;
        short bitB = fixB.getFilterData().categoryBits;

        switch (bitA | bitB){
            case Metroid.FEET_BIT | Metroid.GROUND_BIT: {
                screen.setPlayerGrounded(false);
                break;
            }
            case Metroid.HEAD_BIT | Metroid.GROUND_BIT: {
                screen.setPlayerCanStand(true);
                break;
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
