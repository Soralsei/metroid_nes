package com.kohio.deflesselle.metroid_nes.Tools.Cutscenes.Events;

import com.kohio.deflesselle.metroid_nes.Tools.Cutscenes.CutscenePlayer;

public abstract class CutsceneEvent {

    protected CutscenePlayer player;

    public void begin(CutscenePlayer player) {
        this.player = player;
    }

    protected CutscenePlayer getPlayer() {
        return player;
    }

    public abstract void update(float delta);
    public abstract boolean isFinished();

}
