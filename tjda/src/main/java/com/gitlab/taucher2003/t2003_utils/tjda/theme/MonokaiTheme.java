package com.gitlab.taucher2003.t2003_utils.tjda.theme;

import java.awt.Color;

public class MonokaiTheme implements Theme {

    @Override
    public Color primary() {
        return new Color(0x78dce8);
    }

    @Override
    public Color secondary() {
        return new Color(0xab9df2);
    }

    @Override
    public Color danger() {
        return new Color(0xff6188);
    }

    @Override
    public Color warning() {
        return new Color(0xffd866);
    }

    @Override
    public Color info() {
        return new Color(0xa9dc76);
    }
}
