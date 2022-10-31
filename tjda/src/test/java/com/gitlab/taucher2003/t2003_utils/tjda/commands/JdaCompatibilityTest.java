package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class JdaCompatibilityTest {

    @Test
    void slashCommandLocalizationCompatibility() {
        var localizerFunction = mock(LocalizationFunction.class);

        var command = Commands
                .slash("command", "command-description")
                .addSubcommands(
                        new SubcommandData("subcommand", "subcommand-description")
                                .addOptions(new OptionData(OptionType.STRING, "argument", "argument-description"))
                );
        command.setLocalizationFunction(localizerFunction);

        command.toData();

        var captor = ArgumentCaptor.forClass(String.class);

        verify(localizerFunction, times(6)).apply(captor.capture());

        var expectedKeys = new String[]{
                "command.name", "command.description",
                "command.subcommand.name", "command.subcommand.description",
                "command.subcommand.argument.name", "command.subcommand.argument.description"
        };

        assertThat(captor.getAllValues()).containsExactly(expectedKeys);
    }
}
