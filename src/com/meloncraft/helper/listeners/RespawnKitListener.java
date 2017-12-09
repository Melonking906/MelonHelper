package com.meloncraft.helper.listeners;

import com.meloncraft.helper.MelonHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

public class RespawnKitListener implements Listener
{
    private final MelonHelper plugin;

    private final HashMap<UUID,Long> cooldowns;

    public RespawnKitListener( MelonHelper plugin )
    {
        this.plugin = plugin;
        this.cooldowns = new HashMap<>();
    }

    @EventHandler( priority = EventPriority.LOWEST )
    public void onPlayerRespawn( PlayerRespawnEvent event )
    {
        Player player = event.getPlayer();

        long epoch = Instant.now().getEpochSecond();
        Long cacheEpoch = cooldowns.get( player.getUniqueId() );

        //Do bypass if a cached cooldown is less than 4 mins old
        if( cacheEpoch != null && epoch < cacheEpoch + 240  )
        {
            player.sendMessage( ChatColor.YELLOW + "* " + ChatColor.GRAY + "You died too soon to get another respawn kit!" );
            return;
        }

        cooldowns.put( player.getUniqueId(), epoch );

        plugin.getServer().getScheduler().scheduleSyncDelayedTask( plugin, () ->
        {
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), "kit respawn " + player.getName() );
            player.sendMessage( ChatColor.YELLOW + "* " + ChatColor.GRAY + "You got a respawn kit! Thanks for playing <3" );

        } , 20L );
    }
}
