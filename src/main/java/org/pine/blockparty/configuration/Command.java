package org.pine.blockparty.configuration;

import java.util.Arrays;

public enum Command {

    START_GAME      ("bpstart"),
    STOP_GAME       ("bpstop"),
    ARENA_INFO      ("bplvlinfo"),
    SET_ARENA       ("bplvl"),
    FIREWORK_SHOW   ("bpfw"),
    STATS_SHOW      ("bpstats"),
    SPAWN_POWERUP   ("bppower"),
    HELP            ("bphelp"),
    SPECTATE_TOGGLE ("bpspec");

    private final String triggerKeyword;

    Command(String triggerKeyword) {
        this.triggerKeyword = triggerKeyword;
    }

    public String getTriggerKeyword() {
        return triggerKeyword;
    }

    public static Command mapFromBukkitCommand(org.bukkit.command.Command bukkitCommand) {
        return Arrays.stream(values())
                .filter(command -> command.getTriggerKeyword().equals(bukkitCommand.getName()))
                .findFirst()
                .orElse(null);
    }
}
