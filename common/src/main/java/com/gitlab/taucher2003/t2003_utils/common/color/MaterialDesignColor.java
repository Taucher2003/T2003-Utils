package com.gitlab.taucher2003.t2003_utils.common.color;

import java.awt.Color;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum MaterialDesignColor {
    // Base Colors
    RED(new Color(0xf44336)),
    PINK(new Color(0xe91e63)),
    PURPLE(new Color(0x9c27b0)),
    DEEP_PURPLE(new Color(0x673ab7)),
    INDIGO(new Color(0x3f51b5)),
    BLUE(new Color(0x2196f3)),
    LIGHT_BLUE(new Color(0x03a9f4)),
    CYAN(new Color(0x00bcd4)),
    TEAL(new Color(0x009688)),
    GREEN(new Color(0x4caf50)),
    LIGHT_GREEN(new Color(0x8bc34a)),
    LIME(new Color(0xcddc39)),
    YELLOW(new Color(0xffeb3b)),
    AMBER(new Color(0xffc107)),
    ORANGE(new Color(0xff9800)),
    DEEP_ORANGE(new Color(0xff5722)),
    BROWN(new Color(0x795548)),
    BLUE_GREY(new Color(0x607d8b)),
    GREY(new Color(0x9e9e9e)),

    // lighten-2
    LIGHTEN_RED(new Color(0xe57373)),
    LIGHTEN_PING(new Color(0xf06292)),
    LIGHTEN_PURPLE(new Color(0xba68c8)),
    LIGHTEN_DEEP_PURPLE(new Color(0x9575cd)),
    LIGHTEN_INDIGO(new Color(0x7986cb)),
    LIGHTEN_BLUE(new Color(0x64b5f6)),
    LIGHTEN_LIGHT_BLUE(new Color(0x4fc3f7)),
    LIGHTEN_CYAN(new Color(0x4dd0e1)),
    LIGHTEN_TEAL(new Color(0x4db6ac)),
    LIGHTEN_GREEN(new Color(0x81c784)),
    LIGHTEN_LIGHT_GREEN(new Color(0xaed581)),
    LIGHTEN_LIME(new Color(0xdce775)),
    LIGHTEN_YELLOW(new Color(0xfff176)),
    LIGHTEN_AMBER(new Color(0xffd54f)),
    LIGHTEN_ORANGE(new Color(0xffb74d)),
    LIGHTEN_DEEP_ORANGE(new Color(0xff8a65)),
    LIGHTEN_BROWN(new Color(0xa1887f)),
    LIGHTEN_BLUE_GREY(new Color(0x90a4ae)),
    LIGHTEN_GREY(new Color(0xe0e0e0)),

    // darken-3
    DARKEN_RED(new Color(0xc62828)),
    DARKEN_PINK(new Color(0xad1457)),
    DARKEN_PURPLE(new Color(0x6a1b9a)),
    DARKEN_DEEP_PURPLE(new Color(0x4527a0)),
    DARKEN_INDIGO(new Color(0x283593)),
    DARKEN_BLUE(new Color(0x1565c0)),
    DARKEN_LIGHT_BLUE(new Color(0x0277bd)),
    DARKEN_CYAN(new Color(0x00838f)),
    DARKEN_TEAL(new Color(0x00695c)),
    DARKEN_GREEN(new Color(0x2e7d32)),
    DARKEN_LIGHT_GREEN(new Color(0x558b2f)),
    DARKEN_LIME(new Color(0x9e9d24)),
    DARKEN_YELLOW(new Color(0xf9a825)),
    DARKEN_AMBER(new Color(0xff8f00)),
    DARKEN_ORANGE(new Color(0xef6c00)),
    DARKEN_DEEP_ORANGE(new Color(0xd84315)),
    DARKEN_BROWN(new Color(0x4e342e)),
    DARKEN_BLUE_GREY(new Color(0x37474f)),
    DARKEN_GREY(new Color(0x424242)),

    // accent-3
    ACCENT_RED(new Color(0xff1744)),
    ACCENT_PINK(new Color(0xf50057)),
    ACCENT_PURPLE(new Color(0xd500f9)),
    ACCENT_DEEP_PURPLE(new Color(0x651fff)),
    ACCENT_INDIGO(new Color(0x3d5afe)),
    ACCENT_BLUE(new Color(0x2979ff)),
    ACCENT_LIGHT_BLUE(new Color(0x00b0ff)),
    ACCENT_CYAN(new Color(0x00e5ff)),
    ACCENT_TEAL(new Color(0x1de9b6)),
    ACCENT_GREEN(new Color(0x00e676)),
    ACCENT_LIGHT_GREEN(new Color(0x76ff03)),
    ACCENT_LIME(new Color(0xc6ff00)),
    ACCENT_YELLOW(new Color(0xffea00)),
    ACCENT_AMBER(new Color(0xffc400)),
    ACCENT_ORANGE(new Color(0xff9100)),
    ACCENT_DEEP_ORANGE(new Color(0xff3d00)),

    // just white and black
    WHITE(new Color(0xfffffe)),
    BLACK(new Color(0x000001));

    private final Color color;

    MaterialDesignColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public String asName() {
        return name().toLowerCase(Locale.ROOT).replace("_", "-");
    }

    public static Optional<MaterialDesignColor> fromName(String name) {
        return Arrays.stream(values())
                .filter(color -> color.asName().equalsIgnoreCase(name))
                .findFirst();
    }
}
