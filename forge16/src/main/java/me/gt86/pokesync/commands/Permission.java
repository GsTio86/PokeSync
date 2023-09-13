package me.gt86.pokesync.commands;

import org.jetbrains.annotations.NotNull;

public enum Permission {
    COMMAND_POKESYNC("pokesync.command.pokesync", DefaultAccess.EVERYONE),
    COMMAND_POKESYNC_RELOAD("pokesync.command.pokesync.reload", DefaultAccess.OPERATORS),
    COMMAND_POKESYNC_UPDATE("pokesync.command.pokesync.update", DefaultAccess.OPERATORS),
    COMMAND_USER_DATA("pokesync.command.pmuserdata", DefaultAccess.OPERATORS),
    COMMAND_USER_DATA_MANAGE("pokesync.command.pmuserdata.manage", DefaultAccess.OPERATORS),
    COMMAND_USER_DATA_DUMP("pokesync.command.pmuserdata.dump", DefaultAccess.NOBODY);

    public final String node;
    public final DefaultAccess defaultAccess;

    Permission(@NotNull String node, @NotNull DefaultAccess defaultAccess) {
        this.node = node;
        this.defaultAccess = defaultAccess;
    }

    /**
     * Identifies who gets what permissions by default
     */
    public enum DefaultAccess {
        /**
         * Everyone gets this permission node by default
         */
        EVERYONE,
        /**
         * Nobody gets this permission node by default
         */
        NOBODY,
        /**
         * Server operators ({@code /op}) get this permission node by default
         */
        OPERATORS
    }
}
