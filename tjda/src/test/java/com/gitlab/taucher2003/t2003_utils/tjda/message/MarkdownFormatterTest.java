package com.gitlab.taucher2003.t2003_utils.tjda.message;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownFormatterTest {

    @Test
    void bold() {
        assertThat(MarkdownFormatter.bold().apply("Test")).isEqualTo("**Test**");
    }

    @Test
    void italic() {
        assertThat(MarkdownFormatter.italic().apply("Test")).isEqualTo("*Test*");
    }

    @Test
    void underlined() {
        assertThat(MarkdownFormatter.underlined().apply("Test")).isEqualTo("__Test__");
    }

    @Test
    void codeblock() {
        assertThat(MarkdownFormatter.codeblock().apply("Test")).isEqualTo("`Test`");
    }

    @Test
    void fencedCodeblock() {
        assertThat(MarkdownFormatter.fencedCodeblock().apply("Test")).isEqualTo("```text\nTest\n```");
    }

    @Test
    void fencedCodeblockWithLanguage() {
        assertThat(MarkdownFormatter.fencedCodeblock("java").apply("Test")).isEqualTo("```java\nTest\n```");
        assertThat(MarkdownFormatter.fencedCodeblock("javascript").apply("Test")).isEqualTo("```javascript\nTest\n```");
        assertThat(MarkdownFormatter.fencedCodeblock("ruby").apply("Test")).isEqualTo("```ruby\nTest\n```");
    }
}