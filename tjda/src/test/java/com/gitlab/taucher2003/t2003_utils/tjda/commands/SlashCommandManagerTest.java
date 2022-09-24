package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gitlab.taucher2003.t2003_utils.support.AutoCompleteCallbackActionMock;
import com.gitlab.taucher2003.t2003_utils.support.DummyCommand;
import com.gitlab.taucher2003.t2003_utils.support.DummySubCommand;
import com.gitlab.taucher2003.t2003_utils.support.InteractionMocker;
import com.gitlab.taucher2003.t2003_utils.support.SlashCommandManagerHookMock;
import com.gitlab.taucher2003.t2003_utils.tjda.theme.MonokaiTheme;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

class SlashCommandManagerTest {

    SlashCommandManager manager = new SlashCommandManager(new MonokaiTheme());

    @Test
    void dispatch() {
        var dummyCommand = registeredCommand("dummy");

        var interaction = InteractionMocker.commandInteraction()
                .setName("dummy")
                .build();

        manager.dispatch(interaction);


        assertThat(dummyCommand.executedTimes()).isEqualTo(1);
    }

    @Test
    void unregister() {
        var dummyCommand = registeredCommand("dummy");

        var interaction = InteractionMocker.commandInteraction()
                .setName("dummy")
                .build();

        var appender = setupAppender();

        manager.dispatch(interaction);
        assertThat(dummyCommand.executedTimes()).isEqualTo(1);

        assertThat(appender.list).isEmpty();

        manager.unregisterCommand(dummyCommand);
        manager.dispatch(interaction);
        assertThat(dummyCommand.executedTimes()).isEqualTo(1);

        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("Received interaction for unknown command dummy", Level.WARN));
    }

    @Test
    void dispatchSubcommand() {
        var dummySubCommand = new DummySubCommand("dummy-sub");
        var dummyCommand = registeredCommand("dummy", dummySubCommand);

        var interaction = InteractionMocker.commandInteraction()
                .setName("dummy")
                .setSubcommandName("dummy-sub")
                .build();

        manager.dispatch(interaction);

        assertThat(dummyCommand.executedTimes()).isEqualTo(0);
        assertThat(dummySubCommand.executedTimes()).isEqualTo(1);
    }

    @Test
    void autocomplete() {
        var dummyCommand = registeredCommand("dummy");

        var interaction = InteractionMocker.autoComplete()
                .setName("dummy")
                .build();


        manager.autocomplete(interaction);

        assertThat(dummyCommand.autocompleteTimes()).isEqualTo(1);
    }

    @Test
    void autocompleteSubcommand() {
        var dummySubCommand = new DummySubCommand("dummy-sub");
        var dummyCommand = registeredCommand("dummy", dummySubCommand);

        var interaction = InteractionMocker.autoComplete()
                .setName("dummy")
                .setSubcommandName("dummy-sub")
                .build();

        manager.autocomplete(interaction);

        assertThat(dummyCommand.autocompleteTimes()).isEqualTo(0);
        assertThat(dummySubCommand.autocompleteTimes()).isEqualTo(1);
    }

    @Test
    void logsInteractionsForUnknownCommand() {
        var commandInteraction = InteractionMocker.commandInteraction()
                .setName("dummy")
                .build();
        var autocompleteInteraction = InteractionMocker.autoComplete()
                .setName("dummy")
                .build();

        var appender = setupAppender();

        manager.dispatch(commandInteraction);
        manager.autocomplete(autocompleteInteraction);

        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(
                        Tuple.tuple("Received interaction for unknown command dummy", Level.WARN),
                        Tuple.tuple("Received autocomplete for unknown command dummy", Level.WARN)
                );
    }

    @Test
    void logsInteractionsForUnknownSubCommand() {
        registeredCommand("dummy");
        var commandInteraction = InteractionMocker.commandInteraction()
                .setName("dummy")
                .setSubcommandName("dummy-sub")
                .build();
        var autocompleteInteraction = InteractionMocker.autoComplete()
                .setName("dummy")
                .setSubcommandName("dummy-sub")
                .build();

        var appender = setupAppender();

        manager.dispatch(commandInteraction);
        manager.autocomplete(autocompleteInteraction);

        assertThat(appender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(
                        Tuple.tuple("Received interaction for unknown sub-command dummy/dummy-sub", Level.WARN),
                        Tuple.tuple("Received autocomplete for unknown sub-command dummy/dummy-sub", Level.WARN)
                );
    }

    @Test
    void unpermittedCallsHook() {
        var hook = new SlashCommandManagerHookMock() {
            @Override
            public Permissible getGlobalPermissible() {
                return context -> false;
            }
        };
        var localManager = new SlashCommandManager(MonokaiTheme::new, hook);
        var dummyCommand = new DummyCommand("dummy");
        localManager.registerCommand(dummyCommand);

        var interaction = InteractionMocker.commandInteraction()
                .setName("dummy")
                .build();

        localManager.dispatch(interaction);

        assertThat(hook.unpermittedTimes()).isEqualTo(1);
    }

    @Test
    void unpermittedCallsHookSubcommand() {
        var hook = new SlashCommandManagerHookMock();
        var localManager = new SlashCommandManager(MonokaiTheme::new, hook);
        var dummyCommand = new DummyCommand("dummy", new DummySubCommand[]{new DummySubCommand("dummy-sub", context -> false)});
        localManager.registerCommand(dummyCommand);

        var interaction = InteractionMocker.commandInteraction()
                .setName("dummy")
                .setSubcommandName("dummy-sub")
                .build();

        localManager.dispatch(interaction);

        assertThat(hook.unpermittedTimes()).isEqualTo(1);
    }

    @Test
    void unpermittedReturnsEmptyChoices() {
        var hook = new SlashCommandManagerHookMock() {
            @Override
            public Permissible getGlobalPermissible() {
                return context -> false;
            }
        };
        var localManager = new SlashCommandManager(MonokaiTheme::new, hook);
        var dummyCommand = new DummyCommand("dummy");
        localManager.registerCommand(dummyCommand);


        var interaction = InteractionMocker.autoComplete()
                .setName("dummy")
                .build();
        var autocompleteCallbackActionMock = (AutoCompleteCallbackActionMock) interaction.replyChoices(Collections.emptyList());
        assertThat(autocompleteCallbackActionMock.executeTimes()).isEqualTo(0);

        localManager.autocomplete(interaction);

        assertThat(autocompleteCallbackActionMock.executeTimes()).isEqualTo(1);
    }

    @Test
    void unpermittedReturnsEmptyChoicesSubcommand() {
        registeredCommand("dummy", new DummySubCommand("dummy-sub", context -> false));

        var interaction = InteractionMocker.autoComplete()
                .setName("dummy")
                .setSubcommandName("dummy-sub")
                .build();
        var autocompleteCallbackActionMock = (AutoCompleteCallbackActionMock) interaction.replyChoices(Collections.emptyList());
        assertThat(autocompleteCallbackActionMock.executeTimes()).isEqualTo(0);

        manager.autocomplete(interaction);

        assertThat(autocompleteCallbackActionMock.executeTimes()).isEqualTo(1);
    }

    @AfterEach
    void cleanup() {
        manager.unregisterAllCommands();
    }

    DummyCommand registeredCommand(String name) {
        var dummyCommand = new DummyCommand(name);
        manager.registerCommand(dummyCommand);
        return dummyCommand;
    }

    DummyCommand registeredCommand(String name, SubCommand... subCommands) {
        var dummyCommand = new DummyCommand(name, subCommands);
        manager.registerCommand(dummyCommand);
        return dummyCommand;
    }

    ListAppender<ILoggingEvent> setupAppender() {
        var appender = new ListAppender<ILoggingEvent>();
        appender.start();
        var root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
        root.addAppender(appender);
        return appender;
    }
}
