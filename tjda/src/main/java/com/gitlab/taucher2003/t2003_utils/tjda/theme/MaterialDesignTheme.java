package com.gitlab.taucher2003.t2003_utils.tjda.theme;

import com.gitlab.taucher2003.t2003_utils.common.color.MaterialDesignColors;

import java.awt.Color;

public class MaterialDesignTheme implements Theme {
    @Override
    public Color primary() {
        return MaterialDesignColors.BLUE.getColor();
    }

    @Override
    public Color secondary() {
        return MaterialDesignColors.INDIGO.getColor();
    }

    @Override
    public Color danger() {
        return MaterialDesignColors.RED.getColor();
    }

    @Override
    public Color warning() {
        return MaterialDesignColors.ORANGE.getColor();
    }

    @Override
    public Color info() {
        return MaterialDesignColors.GREEN.getColor();
    }
}
