package com.gitlab.taucher2003.t2003_utils.tjda.message;

import com.gitlab.taucher2003.t2003_utils.common.i18n.StringFormat;

/**
 * Utility class for dealing with some Markdown formatting in Discord
 */
public final class MarkdownFormatter {

    private MarkdownFormatter() {
    }

    /**
     * Returns a {@link StringFormat} instance used for bold text.
     *
     * <pre>
     *     Input: Text
     *     Output: **Text**
     * </pre>
     *
     * @return the {@link StringFormat} instance
     */
    public static StringFormat bold() {
        return s -> String.format("**%s**", s);
    }

    /**
     * Returns a {@link StringFormat} instance used for italic text.
     *
     * <pre>
     *     Input: Text
     *     Output: *Text*
     * </pre>
     *
     * @return the {@link StringFormat} instance
     */
    public static StringFormat italic() {
        return s -> String.format("*%s*", s);
    }

    /**
     * Returns a {@link StringFormat} instance used for underlined text.
     *
     * <pre>
     *     Input: Text
     *     Output: __Text__
     * </pre>
     *
     * @return the {@link StringFormat} instance
     */
    public static StringFormat underlined() {
        return s -> String.format("__%s__", s);
    }

    /**
     * Returns a {@link StringFormat} instance used for text in a codeblock.
     *
     * <pre>
     *     Input: Text
     *     Output: `Text`
     * </pre>
     *
     * @return the {@link StringFormat} instance
     */
    public static StringFormat codeblock() {
        return s -> String.format("`%s`", s);
    }

    /**
     * Returns a {@link StringFormat} instance used for text in a fenced codeblock.
     * This method infers the <code>text</code> language.
     *
     * <pre>
     *     Input: Text
     *     Output: ```text
     *             Text
     *             ```
     * </pre>
     *
     * @return the {@link StringFormat} instance
     */
    public static StringFormat fencedCodeblock() {
        return fencedCodeblock("text");
    }

    /**
     * Returns a {@link StringFormat} instance used for text in a fenced codeblock.
     * <br>
     * Given the <code>language</code> parameter is "java"
     * <pre>
     *     Input: Text
     *     Output: ```java
     *             Text
     *             ```
     * </pre>
     *
     * @param language the language used for code highlighting
     * @return the {@link StringFormat} instance
     */
    public static StringFormat fencedCodeblock(String language) {
        return s -> String.format("```%s\n%s\n```", language, s);
    }
}
