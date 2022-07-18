package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import com.gitlab.taucher2003.t2003_utils.common.i18n.Localizer;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PrefixedLocalizationFunctionTest {

    @Test
    void apply() {
        var localizer = spy(mock(Localizer.class));
        var keyCaptor = ArgumentCaptor.forClass(String.class);
        var localeCaptor = ArgumentCaptor.forClass(Locale.class);
        var function = new PrefixedLocalizationFunction(localizer, "prefix");

        var translationMap = function.apply("key");

        assertThat(translationMap).doesNotContainKey(DiscordLocale.UNKNOWN);

        verify(localizer, times(DiscordLocale.values().length)).localize(keyCaptor.capture(), localeCaptor.capture());

        assertThat(keyCaptor.getValue()).isEqualTo("prefix.key");
        assertThat(keyCaptor.getAllValues().size()).isEqualTo(DiscordLocale.values().length);

        assertThat(localeCaptor.getAllValues())
                .containsAll(
                        Arrays.stream(DiscordLocale.values())
                                .map(DiscordLocale::getLocale)
                                .map(Locale::forLanguageTag)
                                .collect(Collectors.toList())
                );
    }
}
