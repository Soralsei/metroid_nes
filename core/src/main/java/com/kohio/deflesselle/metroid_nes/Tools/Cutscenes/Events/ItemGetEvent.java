package com.kohio.deflesselle.metroid_nes.Tools.Cutscenes.Events;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.kohio.deflesselle.metroid_nes.Tools.Cutscenes.CutscenePlayer;

public class ItemGetEvent extends PauseEvent{

    final Music music;
    private static final String MUSIC_NAME = "music/item_get_fanfare.mp3";

    public ItemGetEvent(AssetManager manager, float duration) {
        super(duration);

        if (manager.isLoaded(MUSIC_NAME)) {
            music = manager.get(MUSIC_NAME);
        } else {
            manager.load(MUSIC_NAME, Music.class);
            manager.finishLoading();
            music = manager.get(MUSIC_NAME);
        }
        music.setOnCompletionListener(Music::dispose);
    }

    @Override
    public void begin(CutscenePlayer player) {
        super.begin(player);
        if (music != null) {
            music.play();
        }
    }
}
