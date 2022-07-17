package com.gitlab.taucher2003.t2003_utils.tjda.i18n;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.Command;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.CommandArgument;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.Permissible;
import com.gitlab.taucher2003.t2003_utils.tjda.commands.SubCommand;
import com.gitlab.taucher2003.t2003_utils.tjda.theme.Theme;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

class DiscordLocalizerTest {

    DiscordLocalizer localizer = new DiscordGuildLocalizer("i18n.discordLocale", null);
    Command command = new TestCommand();

    @Test
    void buildsDiscordTranslations() {
        var appender = setupAppender();

        var data = command.asJdaObject(localizer).toData().toMap();
        var json = new JSONObject(data);

        assertTranslation(getTranslations(json, "name_localizations"), "name");
        assertTranslation(getTranslations(json, "description_localizations"), "description");

        var subcommand = json.getJSONArray("options").getJSONObject(0);
        assertTranslation(getTranslations(subcommand, "name_localizations"), "sub-name");
        assertTranslation(getTranslations(subcommand, "description_localizations"), "sub-description");

        var argument = subcommand.getJSONArray("options").getJSONObject(0);
        assertTranslation(getTranslations(argument, "name_localizations"), "arg-name");
        assertTranslation(getTranslations(argument, "description_localizations"), "arg-description");

        assertThat(appender.list).isEmpty();
    }

    void assertTranslation(Map<String, Object> translations, String type) {
        locales().forEach(locale -> assertThat(translations.get(locale.getLocale())).isEqualTo(type + "-" + locale.getLocale().toLowerCase(Locale.ROOT)));
    }

    Map<String, Object> getTranslations(JSONObject json, String key) {
        return json.getJSONObject(key).toMap();
    }

    List<DiscordLocale> locales() {
        return Arrays.stream(DiscordLocale.values())
                .filter(Predicate.not(DiscordLocale.UNKNOWN::equals))
                .collect(Collectors.toList());
    }

    ListAppender<ILoggingEvent> setupAppender() {
        var appender = new ListAppender<ILoggingEvent>();
        appender.start();
        var root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
        root.addAppender(appender);
        return appender;
    }

    private static final class TestCommand extends Command {

        private TestCommand() {
            super(
                    "test",
                    "test-description",
                    new SubCommand[]{new TestSubCommand()}
            );
        }

        @Override
        public void execute(CommandInteraction event, Theme theme, Permissible.PermissibleContext permissibleContext) {
        }
    }

    private static final class TestSubCommand extends SubCommand {

        private TestSubCommand() {
            super(
                    "test-sub",
                    "test-sub-description",
                    new CommandArgument[]{new CommandArgument(OptionType.STRING, "test-sub-arg", "test-sub-arg-description")}
            );
        }

        @Override
        public void execute(CommandInteraction event, Theme theme, Permissible.PermissibleContext permissibleContext) {
        }
    }
}
