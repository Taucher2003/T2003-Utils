package com.gitlab.taucher2003.t2003_utils.tjda.message;

import com.gitlab.taucher2003.t2003_utils.common.i18n.StringFormat;

public final class MarkdownFormatter {

    private MarkdownFormatter() {
    }

    public static StringFormat bold() {
        return s -> String.format("**%s**", s);
    }

    public static StringFormat italic() {
        return s -> String.format("*%s*", s);
    }

    public static StringFormat underlined() {
        return s -> String.format("__%s__", s);
    }

    public static StringFormat codeblock() {
        return s -> String.format("`%s`", s);
    }

    public static StringFormat fencedCodeblock() {
        return fencedCodeblock("text");
    }

    public static StringFormat fencedCodeblock(String language) {
        return s -> String.format("```%s\n%s\n```", language, s);
    }
}
