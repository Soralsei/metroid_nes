package com.kohio.deflesselle.metroid_nes.Tools.Cutscenes.Events;

public class PauseEvent extends CutsceneEvent{

    private final float duration;
    private float timer = 0f;

    private boolean isFinished = false;

    public PauseEvent(float duration){
        this.duration = duration;
    }

    @Override
    public void update(float delta) {
        timer += delta;
        if (timer >= duration) {
            isFinished = true;
        }
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }
}
