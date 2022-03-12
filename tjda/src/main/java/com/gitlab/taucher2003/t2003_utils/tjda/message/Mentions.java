package com.gitlab.taucher2003.t2003_utils.tjda.message;

/**
 * Utility class for dealing with mention formatting in Discord
 */
public final class Mentions {

    private Mentions() {
    }

    /**
     * Returns a user mention for the given id.
     *
     * <pre><@id></pre>
     *
     * @param id the id of the user
     * @return the mention
     * @see #user(String)
     */
    public static String user(long id) {
        return user(String.valueOf(id));
    }

    /**
     * Returns a user mention for the given id.
     *
     * <pre><@id></pre>
     *
     * @param id the id of the user
     * @return the mention
     * @see #user(long)
     */
    public static String user(String id) {
        return mention("@", id);
    }

    /**
     * Returns a role mention for the given id.
     *
     * <pre><@&id></pre>
     *
     * @param id the id of the role
     * @return the mention
     * @see #role(String)
     */
    public static String role(long id) {
        return role(String.valueOf(id));
    }

    /**
     * Returns a role mention for the given id.
     *
     * <pre><@&id></pre>
     *
     * @param id the id of the role
     * @return the mention
     * @see #role(long)
     */
    public static String role(String id) {
        return mention("@&", id);
    }

    /**
     * Returns a channel mention for the given id.
     *
     * <pre><#id></pre>
     *
     * @param id the id of the channel
     * @return the mention
     * @see #channel(String)
     */
    public static String channel(long id) {
        return channel(String.valueOf(id));
    }

    /**
     * Returns a channel mention for the given id.
     *
     * <pre><#id></pre>
     *
     * @param id the id of the channel
     * @return the mention
     * @see #channel(long)
     */
    public static String channel(String id) {
        return mention("#", id);
    }

    /**
     * Returns an emote mention for the given name and id.
     *
     * <pre><:name:id></pre>
     *
     * @param name the name of the emote
     * @param id   the id of the emote
     * @return the mention
     * @see #emote(String, String)
     */
    public static String emote(String name, long id) {
        return emote(name, String.valueOf(id));
    }

    /**
     * Returns an emote mention for the given name and id.
     *
     * <pre><:name:id></pre>
     *
     * @param name the name of the emote
     * @param id   the id of the emote
     * @return the mention
     * @see #emote(String, long)
     */
    public static String emote(String name, String id) {
        return mention(String.format(":%s:", name), id);
    }

    /**
     * Returns a custom mention with a given identifier/decorator and an id.
     * This mention may not work if it is not supported by the client.
     * For working mentions, see the other methods in this class
     *
     * @param identifier the mention identifier/decorator
     * @param id         the id
     * @return the mention
     * @see #user(long)
     * @see #user(String)
     * @see #role(long)
     * @see #role(String)
     * @see #channel(long)
     * @see #channel(String)
     * @see #emote(String, long)
     * @see #emote(String, String)
     */
    public static String mention(String identifier, String id) {
        return String.format("<%s%s>", identifier, id);
    }
}
