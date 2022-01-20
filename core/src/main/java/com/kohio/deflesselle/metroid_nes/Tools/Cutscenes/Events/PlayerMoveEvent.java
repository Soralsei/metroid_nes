package com.kohio.deflesselle.metroid_nes.Tools.Cutscenes.Events;

import com.badlogic.gdx.math.Vector2;
import com.kohio.deflesselle.metroid_nes.Entities.Player;

public class PlayerMoveEvent extends CutsceneEvent{

    private final float endX;
    private final float endY;
    private final float speed;

    private final Player player;

    private boolean isFinished = false;

    public PlayerMoveEvent(Player player, float endX, float endY, float speed){
        this.player = player;
        this.endX = endX;
        this.endY = endY;
        this.speed = speed;
    }


    @Override
    public void update(float delta) {
        Vector2 pos =  player.body.getPosition();
        if (pos.x != endX || pos.y != endY){
            if (pos.x != endX) {
                player.body.setTransform(pos.x + speed * delta, pos.y, 0);
            }
            if (pos.y != endY) {
                player.body.setTransform(pos.x, pos.y + speed * delta, 0);
            }
            player.update(delta);
        } else {
            isFinished = true;
        }
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }
}
