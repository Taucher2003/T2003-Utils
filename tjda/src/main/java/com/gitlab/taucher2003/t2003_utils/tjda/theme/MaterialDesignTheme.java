package com.gitlab.taucher2003.t2003_utils.tjda.theme;

import com.gitlab.taucher2003.t2003_utils.common.color.Colors;

import java.awt.Color;

public class MaterialDesignTheme implements Theme {
    @Override
    public Color primary() {
        return Colors.BLUE.getColor();
    }

    @Override
    public Color secondary() {
        return Colors.INDIGO.getColor();
    }

    @Override
    public Color danger() {
        return Colors.RED.getColor();
    }

    @Override
    public Color warning() {
        return Colors.ORANGE.getColor();
    }

    @Override
    public Color info() {
        return Colors.GREEN.getColor();
    }
}
