package com.kohio.deflesselle.metroid_nes.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.kohio.deflesselle.metroid_nes.Entities.Player;
import com.kohio.deflesselle.metroid_nes.Screens.GameScreen;

public class GameInputProcessor implements InputProcessor {

    private final GameScreen screen;

    public GameInputProcessor(GameScreen screen) {
        this.screen = screen;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode){
            case Input.Keys.D:
                screen.setCurrentState(Player.MoveState.RUN);
                screen.samus.isGoingRight = true;
                break;
            case Input.Keys.A:
                screen.setCurrentState(Player.MoveState.RUN);
                screen.samus.isGoingLeft = true;
                break;
            case Input.Keys.S:
                screen.fireMorphBallAction();
                break;
            case Input.Keys.W:
                screen.fireStandAction();
                break;
            case Input.Keys.SPACE:
                screen.fireJumpAction();
                if (screen.isPlayerJumping()) screen.setJumpKeyHeld(true);
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode){
            case Input.Keys.D:
                screen.samus.isGoingRight = false;
                screen.samus.updatePrevious(keycode);
                break;
            case Input.Keys.A:
                screen.samus.isGoingLeft = false;
                screen.samus.updatePrevious(keycode);
                break;
            case Input.Keys.SPACE:
                if (screen.isPlayerJumping()) screen.setJumpKeyHeld(false);
                break;
        }
        if (!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D))
            screen.setCurrentState(Player.MoveState.IDLE);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
