package com.titammods.easyxp.xp;

import net.minecraft.world.entity.player.Player;

public final class XpMath {
    private XpMath() {}

    public static int costForLevel(int level) {
        if (level >= 30) return 112 + (level - 30) * 9;
        if (level >= 15) return 37 + (level - 15) * 5;
        return 7 + level * 2;
    }

    public static int pointsToReach(int level) {
        int points = 0;
        for (int l = 0; l < level; l++) points += costForLevel(l);
        return points;
    }

    public static int totalPointsOf(Player player) {
        int level = player.experienceLevel;
        int partial = Math.round(player.experienceProgress * costForLevel(level));
        return pointsToReach(level) + partial;
    }

    public static int pointOfOneLevel(Player player) {
        int level = player.experienceLevel;
        int partial = Math.round(player.experienceProgress * costForLevel(level));
        return partial + (level > 0 ? costForLevel(level - 1) : 0);
    }
}
