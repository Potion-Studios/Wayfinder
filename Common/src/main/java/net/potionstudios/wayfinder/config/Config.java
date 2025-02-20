package net.potionstudios.wayfinder.config;

public class Config {

    public Wayfinder wayfinder = new Wayfinder();

    public static class Wayfinder {
        public int MAX_SEARCH_DISTANCE_IN_BLOCKS = 10000;
        public int COOLDOWN_IN_SECONDS = 60;
        public int SCARED_PROJECTILE_MOB_DISTANCE_IN_BLOCKS = 10;
    }

    public WayfinderHeartBlock wayfinderHeartBlock = new WayfinderHeartBlock();

    public static class WayfinderHeartBlock {
        public int EMERALD_DEATH_COST_MULTIPLIER = 1;
        public int ACTIVATION_COOLDOWN_IN_SECONDS = 15;
    }
}
