package com.kohio.deflesselle.metroid_nes.Scenes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameUI {

    private final Stage stage;
    private final Viewport vPort;

    private Integer missileCount;
    private Integer totalEnergy;

    private Array<Image> energyTanks;
    private Label currentEnergy;
    private Image missile;
    private Label currentMissiles;

    public GameUI(SpriteBatch batch){
        vPort = new FitViewport(500,500, new OrthographicCamera());
        stage  = new Stage(vPort, batch);
    }
}
