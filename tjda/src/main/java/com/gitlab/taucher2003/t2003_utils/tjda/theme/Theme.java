package com.gitlab.taucher2003.t2003_utils.tjda.theme;

import java.awt.Color;

/**
 * A theme declares the color scheme for the bot messages
 */
@SuppressWarnings("ClassNamePrefixedWithPackageName")
public interface Theme {

    Color primary();

    Color secondary();

    Color danger();

    Color warning();

    Color info();
}
