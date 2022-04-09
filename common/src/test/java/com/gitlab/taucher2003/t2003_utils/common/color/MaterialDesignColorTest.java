package com.gitlab.taucher2003.t2003_utils.common.color;

import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.assertj.core.api.Assertions.assertThat;

class MaterialDesignColorTest {

    @Test
    void asName() {
        assertThat(MaterialDesignColor.ACCENT_DEEP_ORANGE.asName()).isEqualTo("accent-deep-orange");
        assertThat(MaterialDesignColor.ACCENT_DEEP_PURPLE.asName()).isEqualTo("accent-deep-purple");
        assertThat(MaterialDesignColor.LIGHTEN_DEEP_ORANGE.asName()).isEqualTo("lighten-deep-orange");
        assertThat(MaterialDesignColor.RED.asName()).isEqualTo("red");
    }

    @Test
    void fromNameDefault() {
        assertColor("accent-deep-orange", MaterialDesignColor.ACCENT_DEEP_ORANGE);
        assertColor("accent-deep-purple", MaterialDesignColor.ACCENT_DEEP_PURPLE);
        assertColor("lighten-deep-orange", MaterialDesignColor.LIGHTEN_DEEP_ORANGE);
        assertColor("red", MaterialDesignColor.RED);
    }

    @Test
    void fromNameUnderscore() {
        assertColor("accent_deep_orange", MaterialDesignColor.ACCENT_DEEP_ORANGE);
        assertColor("accent_deep_purple", MaterialDesignColor.ACCENT_DEEP_PURPLE);
        assertColor("lighten_deep_orange", MaterialDesignColor.LIGHTEN_DEEP_ORANGE);
    }

    @Test
    void fromNameIgnoreCase() {
        assertColor("ACCENT-deep-ORANGE", MaterialDesignColor.ACCENT_DEEP_ORANGE);
        assertColor("ACCENT-DEEP-PURPLE", MaterialDesignColor.ACCENT_DEEP_PURPLE);
        assertColor("LiGhTeN-DeEp_oRaNgE", MaterialDesignColor.LIGHTEN_DEEP_ORANGE);
        assertColor("Red", MaterialDesignColor.RED);
    }

    @Test
    void discordFriendlyWhiteAndBlack() {
        assertThat(MaterialDesignColor.WHITE.getColor()).isEqualTo(new Color(0xfffffe));
        assertThat(MaterialDesignColor.BLACK.getColor()).isEqualTo(new Color(0x000001));
    }

    private void assertColor(String colorName, MaterialDesignColor expected) {
        assertThat(MaterialDesignColor.fromName(colorName)).isNotEmpty().get().isEqualTo(expected);
    }
}