package me.skyscx.sunland;

import org.bukkit.plugin.java.JavaPlugin;

public final class SunLand extends JavaPlugin {
    private static SunLand instance;
    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
    }
    public static SunLand getInstance(){
        return instance;
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
