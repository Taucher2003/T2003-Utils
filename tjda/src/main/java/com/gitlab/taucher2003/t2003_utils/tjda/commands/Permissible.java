package com.gitlab.taucher2003.t2003_utils.tjda.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface Permissible {

    Permissible UNRESTRICTED = (context) -> true;

    @SuppressWarnings("AnonymousInnerClass")
    Permissible ADMINISTRATOR_ONLY = new Permissible() {
        @Override
        public List<Permission> defaultMemberPermissions() {
            return List.of(Permission.ADMINISTRATOR);
        }

        @Override
        public boolean permitted(PermissibleContext context) {
            return context.getMember()
                    .map(m -> m.hasPermission(Permission.ADMINISTRATOR))
                    .orElse(false);
        }
    };

    boolean permitted(PermissibleContext context);

    default List<Permission> defaultMemberPermissions() {
        return Collections.emptyList();
    }

    default Permissible or(Permissible permissible) {
        return (context) -> permitted(context) || permissible.permitted(context);
    }

    default Permissible and(Permissible permissible) {
        return (context) -> permitted(context) && permissible.permitted(context);
    }

    class PermissibleContext implements ISnowflake {

        private final Guild guild;
        private final Member member;
        private final User user;

        public PermissibleContext(Guild guild, Member member, User user) {
            this.guild = guild;
            this.member = member;
            this.user = user;
        }

        public Optional<Guild> getGuild() {
            return Optional.ofNullable(guild);
        }

        public Optional<Member> getMember() {
            return Optional.ofNullable(member);
        }

        public User getUser() {
            return user;
        }

        @Override
        public long getIdLong() {
            return user.getIdLong();
        }
    }
}
