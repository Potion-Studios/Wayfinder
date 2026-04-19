package net.potionstudios.wayfinder.config;

public class Config {

    public Wayfinder wayfinder = new Wayfinder();

    public static class Wayfinder {
        public ConfigUtils.CommentValue<Integer> MAX_SEARCH_DISTANCE =
                ConfigUtils.CommentValue.of("Max Biome Search Distance in Blocks as a radius around the Wayfinder's current position",10000);
        public ConfigUtils.CommentValue<Integer> COOLDOWN =
                ConfigUtils.CommentValue.of("Cooldown in seconds before the player can use the Wayfinder again", 15);
        public ConfigUtils.CommentValue<Integer> TELEPORT_TO_OWNER =
                ConfigUtils.CommentValue.of("Amount of time in Seconds before the wayfinder attempts to teleport to it's owner if it's out of range",5);
        public ConfigUtils.CommentValue<Boolean> ENABLE_SOUNDS =
                ConfigUtils.CommentValue.of("Enable Sounds for the Wayfinder", true);
        public ConfigUtils.CommentValue<Boolean> DISABLE_SOUNDS_WHEN_SITTING =
                ConfigUtils.CommentValue.of("Disable Sounds for the Wayfinder when sitting", false);
    }

    public WayfinderHeartBlock wayfinderHeartBlock = new WayfinderHeartBlock();

    public static class WayfinderHeartBlock {
        public ConfigUtils.CommentValue<Integer> EMERALD_DEATH_COST_MULTIPLIER =
                ConfigUtils.CommentValue.of("Amount of Wayfinder Deaths the player has multiplied by the number set here will calculate the emerald cost for them to summon a new wayfinder", 2);
        public ConfigUtils.CommentValue<Integer> ACTIVATION_COOLDOWN =
                ConfigUtils.CommentValue.of("Amount Of Time in Seconds till the Wayfinder Heart Block can be Activated (Spawn in wayfinder) again after the last time", 15);
    }
}
