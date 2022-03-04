package com.gitlab.taucher2003.t2003_utils.tjda.theme;

import com.gitlab.taucher2003.t2003_utils.common.color.MaterialDesignColor;

import java.awt.Color;

public class MaterialDesignTheme implements Theme {
    @Override
    public Color primary() {
        return MaterialDesignColor.BLUE.getColor();
    }

    @Override
    public Color secondary() {
        return MaterialDesignColor.INDIGO.getColor();
    }

    @Override
    public Color danger() {
        return MaterialDesignColor.RED.getColor();
    }

    @Override
    public Color warning() {
        return MaterialDesignColor.ORANGE.getColor();
    }

    @Override
    public Color info() {
        return MaterialDesignColor.GREEN.getColor();
    }
}
