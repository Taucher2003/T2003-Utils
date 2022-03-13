package com.gitlab.taucher2003.t2003_utils.tjda.message;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MentionsTest {

    @Test
    void user() {
        assertThat(Mentions.user(444889694002741249L)).isEqualTo("<@444889694002741249>");
        assertThat(Mentions.user("444889694002741249")).isEqualTo("<@444889694002741249>");
    }

    @Test
    void role() {
        assertThat(Mentions.role(853250412730777650L)).isEqualTo("<@&853250412730777650>");
        assertThat(Mentions.role("853250412730777650")).isEqualTo("<@&853250412730777650>");
    }

    @Test
    void channel() {
        assertThat(Mentions.channel(853262454971433001L)).isEqualTo("<#853262454971433001>");
        assertThat(Mentions.channel("853262454971433001")).isEqualTo("<#853262454971433001>");
    }

    @Test
    void emote() {
        assertThat(Mentions.emote("award", 836262739608731648L)).isEqualTo("<:award:836262739608731648>");
        assertThat(Mentions.emote("award", "836262739608731648")).isEqualTo("<:award:836262739608731648>");
    }

    @Test
    void mention() {
        assertThat(Mentions.mention("//", "0")).isEqualTo("<//0>");
    }
}