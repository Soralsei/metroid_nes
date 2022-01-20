package com.kohio.deflesselle.metroid_nes.Tools.Cutscenes;

import com.kohio.deflesselle.metroid_nes.Tools.Cutscenes.Events.CutsceneEvent;

public interface CutscenePlayer {

    void queueEvent(CutsceneEvent event);

}
