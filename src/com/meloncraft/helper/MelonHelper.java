package com.meloncraft.helper;

import com.meloncraft.helper.commands.GiveEveryoneCommand;
import com.meloncraft.helper.commands.SetRaceCommand;
import com.meloncraft.helper.listeners.MollyBotListener;
import com.meloncraft.helper.listeners.RaceListener;
import com.meloncraft.helper.listeners.RespawnKitListener;
import com.meloncraft.helper.listeners.VoteListener;
import com.meloncraft.helper.permissioneffects.EffectsRunnable;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class MelonHelper extends JavaPlugin
{
    private static final String MOLLY = ChatColor.DARK_GRAY + "Server " + ChatColor.LIGHT_PURPLE + "Molly" + ChatColor.GOLD + ": ";
    private static final int EFFECT_REFRESH_TIME = 120;

    private static Permission perms = null;
    private static Chat chat = null;

    @Override
    public void onEnable()
    {
        saveDefaultConfig();

        if( !setupPermission() || !setupChat() )
        {
            getLogger().info( "Vault connection error!" );
        }

        //Events
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents( new VoteListener(), this );
        pm.registerEvents( new RaceListener( this ), this );
        pm.registerEvents( new MollyBotListener( this ), this );
        pm.registerEvents( new RespawnKitListener( this ), this );

        //Commands
        getCommand( "setrace" ).setExecutor( new SetRaceCommand( this ) );
        getCommand( "giveeveryone" ).setExecutor( new GiveEveryoneCommand() );

        //Runnables
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

        scheduler.scheduleSyncRepeatingTask( this, new EffectsRunnable( EFFECT_REFRESH_TIME ), EFFECT_REFRESH_TIME, EFFECT_REFRESH_TIME );
    }

    public static String getMollyPrefix()
    {
        return MOLLY;
    }

    //+++ VAULT +++

    public static Permission getVaultPerms()
    {
        return perms;
    }

    public static Chat getVaultChat()
    {
        return chat;
    }

    private boolean setupPermission() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }
}
