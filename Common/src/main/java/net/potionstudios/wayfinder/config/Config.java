package net.potionstudios.wayfinder.config;

public class Config {

    public Wayfinder wayfinder = new Wayfinder();

    public static class Wayfinder {
        public int MAX_SEARCH_DISTANCE_IN_CHUNKS = 100;
        public int COOLDOWN_IN_SECONDS = 60;
        public int EMERALD_COST_MULTIPLIER = 1;
        public int WAYFINDER_HEART_BLOCK_COOLDOWN_IN_SECONDS = 15;
        public int SCARED_PROJECTILE_MOB_DISTANCE = 10;
    }
}
