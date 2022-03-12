package com.gitlab.taucher2003.t2003_utils.tjda.message;

public final class Mentions {

    private Mentions() {
    }

    public static String user(long id) {
        return user(String.valueOf(id));
    }

    public static String user(String id) {
        return mention("@", id);
    }

    public static String role(long id) {
        return role(String.valueOf(id));
    }

    public static String role(String id) {
        return mention("@&", id);
    }

    public static String channel(long id) {
        return channel(String.valueOf(id));
    }

    public static String channel(String id) {
        return mention("#", id);
    }

    public static String emote(String name, long id) {
        return emote(name, String.valueOf(id));
    }

    public static String emote(String name, String id) {
        return mention(String.format(":%s:", name), id);
    }

    public static String mention(String identifier, String id) {
        return String.format("<%s%s>", identifier, id);
    }
}
