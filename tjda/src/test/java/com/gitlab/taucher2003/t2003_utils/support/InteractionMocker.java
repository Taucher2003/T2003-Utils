package com.gitlab.taucher2003.t2003_utils.support;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class InteractionMocker {

    private InteractionMocker() {
    }

    static <T extends CommandInteractionPayload> T commandInteractionPayload(
            Class<T> clazz,
            String name,
            String groupName,
            String subcommandName
    ) {
        var interaction = mock(clazz);

        when(interaction.getName()).thenReturn(name);
        when(interaction.getSubcommandGroup()).thenReturn(groupName);
        when(interaction.getSubcommandName()).thenReturn(subcommandName);
        when(interaction.getCommandType()).thenReturn(Command.Type.SLASH);
        when(interaction.getFullCommandName()).thenCallRealMethod();

        if (interaction instanceof CommandAutoCompleteInteraction) {
            var autocompleteInteraction = (CommandAutoCompleteInteraction) interaction;

            var autocompleteCallbackActionMock = new AutoCompleteCallbackActionMock();

            when(autocompleteInteraction.replyChoices(Collections.emptyList())).thenReturn(autocompleteCallbackActionMock);
        }

        return interaction;
    }

    public static CommandInteractionPayloadMockBuilder<CommandInteraction> commandInteraction() {
        return new CommandInteractionPayloadMockBuilder<>(CommandInteraction.class);
    }

    public static CommandInteractionPayloadMockBuilder<CommandAutoCompleteInteraction> autoComplete() {
        return new CommandInteractionPayloadMockBuilder<>(CommandAutoCompleteInteraction.class);
    }

    public static class CommandInteractionPayloadMockBuilder<T extends CommandInteractionPayload> {

        private final Class<? extends T> clazz;
        private String name;
        private String groupName;
        private String subcommandName;

        public CommandInteractionPayloadMockBuilder(Class<? extends T> clazz) {
            this.clazz = clazz;
        }

        public CommandInteractionPayloadMockBuilder<T> setName(String name) {
            this.name = name;
            return this;
        }

        public CommandInteractionPayloadMockBuilder<T> setGroupName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        public CommandInteractionPayloadMockBuilder<T> setSubcommandName(String subcommandName) {
            this.subcommandName = subcommandName;
            return this;
        }

        public T build() {
            return commandInteractionPayload(
                    clazz,
                    name,
                    groupName,
                    subcommandName
            );
        }
    }
}
