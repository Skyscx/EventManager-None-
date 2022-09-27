package me.skyscx.sunland;

import com.sun.tools.javac.comp.Todo;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sun.security.provider.Sun;

import java.util.ArrayList;
import java.util.List;

public class System implements Listener, CommandExecutor {

    private int Xp;
    private int Yp;
    private int Zp;
    private int health;
    private int food;
    private World w;
    private String GM;

    private boolean STATUS = false;
    private List<Player> playersEventWorld = new ArrayList<>();
    private String NameEvent;
    private Location EventSpawnLocation;
    private String NameOwnerMP;


    private Location SaveData(Player player){
        this.Xp = player.getLocation().getBlockX()+1;
        this.Yp = player.getLocation().getBlockY()+1;
        this.Zp = player.getLocation().getBlockZ()+1;
        this.w = player.getWorld();
        this.health = (int) player.getHealth();
        this.food = player.getFoodLevel();
        this.GM = String.valueOf(player.getGameMode());
        return null;
    }
    private void BackToWorld(Player player){
        Location LocBack = new Location(w,Xp,Yp,Zp);
        player.teleport(LocBack);
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(health);
        player.setFoodLevel(food);
        player.setGameMode(GameMode.valueOf(GM));
    }
    public void GoToEventWorld(Player player){
        SaveData(player);
        int SpawnX = SunLand.getInstance().getConfig().getInt("EventWorld.Spawn.X");
        int SpawnY = SunLand.getInstance().getConfig().getInt("EventWorld.Spawn.Y");
        int SpawnZ = SunLand.getInstance().getConfig().getInt("EventWorld.Spawn.Z");
        Location Loc = new Location(Bukkit.getWorld("build"),SpawnX,SpawnY,SpawnZ);
        player.teleport(Loc);
        player.setGameMode(GameMode.ADVENTURE);
    }
    public void QuidWorldEvent(Player player){
        if (player.getWorld().equals("build")){
            BackToWorld(player);
        }
    }
    private void OpenMP_(Player player){
        String Name = player.getDisplayName();
        SendAllMessage("Игрок " + ChatColor.AQUA + Name + ChatColor.WHITE + " открыл для игроков мероприятие.");
        SendAllMessage("Для участия в мероприятии, пропишите - " + ChatColor.AQUA + "/eventgo");
        this.NameOwnerMP = player.getDisplayName();
        STATUS = true;
        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "EVENTS" + ChatColor.GRAY + "]" + ChatColor.WHITE + "Вы открыли мероприятие для игроков.");

    }
    private void CloseMP_(Player player){
        String Name1 = player.getDisplayName();
        if (Name1 == NameOwnerMP){
            STATUS = false;
            player.sendMessage(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "EVENTS" + ChatColor.GRAY + "]" + ChatColor.WHITE + "Вы закрыли мероприятие для игроков.");
            player.sendMessage(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "EVENTS" + ChatColor.GRAY + "]" + ChatColor.WHITE + "Игроки были телепортированы в основной мир.");
        }else {

        }
    }
    public void ConnectPlayer(Player player){
        playersEventWorld.add(player);
        player.teleport(EventSpawnLocation);
        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "EVENTS" + ChatColor.GRAY + "]" + ChatColor.WHITE + "Вы телепортировались на мероприятие.");
        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "EVENTS" + ChatColor.GRAY + "]" + ChatColor.WHITE + "Чтобы покинуть мероприятие, пропишите - " + ChatColor.AQUA + "/eventback");
    }
    public void SendArenaMessage(String msg){
        for (Player player: playersEventWorld){
            player.sendMessage(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "EVENTS" + ChatColor.GRAY + "]" + ChatColor.WHITE + "msg");
        }
    }
    public void SendAllMessage(String msg){
        Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "EVENTS" + ChatColor.GRAY + "]" + ChatColor.WHITE + "msg");
    }


    @EventHandler
    public void QuitGame(PlayerQuitEvent event){
        Player player = event.getPlayer();
        QuidWorldEvent(player);
    }
    @EventHandler
    public void OnClickMenu(InventoryClickEvent event){
        if (event.getView().getTitle().equals(ChatColor.GREEN + "Мероприятия."))
        if(event.getCurrentItem().getItemMeta() != null){
            if (event.getCurrentItem().getItemMeta().getDisplayName() != null){
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "EVENT WORLD")){
                    Player player = (Player) event.getWhoClicked();
                    if (player.getWorld().equals("build")){
                        player.sendMessage(ChatColor.GRAY + "Вы уже в мире мероприятий.");
                        event.setCancelled(true);
                    }
                    if (player.getWorld().equals("login_world")){
                        player.sendMessage(ChatColor.GRAY + "Вы не в игровом мире. Вернитесь в игровой мир.");
                        event.setCancelled(true);
                    }
                    player.sendMessage(ChatColor.GRAY + "Вы были перемещены в мир мероприятий.");
                    GoToEventWorld(player);
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Default World")){
                    Player player = (Player) event.getWhoClicked();
                    if (player.getWorld().equals("world")){
                        player.sendMessage(ChatColor.GRAY + "Вы уже в игровом мире.");
                        event.setCancelled(true);
                    }
                    player.sendMessage(ChatColor.GRAY + "Вы были перемещены в мир мероприятий.");
                    QuidWorldEvent(player);
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Запустить мероприятие.")){
                    Player player = (Player) event.getWhoClicked();
                    if (player.getWorld().equals("build")){
                        player.closeInventory();
                        OpenMP_(player);
                    }
                    else {
                        player.sendMessage("Ты не находишься в мире мероприятий.");
                    }
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Закрыть мероприятие.")){
                    Player player = (Player) event.getWhoClicked();
                    if (player.getWorld().equals("build")){
                        player.closeInventory();
                        //ДОДЕЛАТЬ ЛОГИКУ откл мероприятия.
                    }
                    else {
                        player.sendMessage("Ты не находишься в мире мероприятий.");
                    }
                }
            }


        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            Inventory MenuEvents = Bukkit.createInventory(null, 18, ChatColor.GREEN + "Мероприятия.");
            //Отправиться в мир Мероприятий
            if (player.hasPermission("em.gotoEventWorld")) {
                ItemStack GoToWorldEvent = new ItemStack(Material.EMERALD_BLOCK);
                ItemMeta meta_GoToWorldEvent = GoToWorldEvent.getItemMeta();
                meta_GoToWorldEvent.setDisplayName(ChatColor.AQUA + "EVENT WORLD");
                GoToWorldEvent.setItemMeta(meta_GoToWorldEvent);
                MenuEvents.addItem(GoToWorldEvent);
            }else{
                ItemStack ARRAY = new ItemStack(Material.EMERALD_BLOCK);
                ItemMeta meta_ARRAY = ARRAY.getItemMeta();
                meta_ARRAY.setDisplayName(" ");
                ARRAY.setItemMeta(meta_ARRAY);
                MenuEvents.addItem(ARRAY);
            }
            //Вернуться с мира Мероприятий в Обычный мир
            if (player.hasPermission("em.gotoEventWorld")){
                ItemStack GoToWorld = new ItemStack(Material.GRASS_BLOCK);
                ItemMeta meta_GoToWorld = GoToWorld.getItemMeta();
                meta_GoToWorld.setDisplayName(ChatColor.AQUA + "Default World");
                GoToWorld.setItemMeta(meta_GoToWorld);
                MenuEvents.addItem(GoToWorld);
            }else{
                ItemStack ARRAY = new ItemStack(Material.EMERALD_BLOCK);
                ItemMeta meta_ARRAY = ARRAY.getItemMeta();
                meta_ARRAY.setDisplayName(" ");
                ARRAY.setItemMeta(meta_ARRAY);
                MenuEvents.addItem(ARRAY);
            }

        }
        return false;
    }
}

