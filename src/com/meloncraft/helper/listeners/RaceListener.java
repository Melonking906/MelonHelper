package com.meloncraft.helper.listeners;

import com.meloncraft.helper.MelonHelper;
import com.meloncraft.helper.RaceUtils;
import net.nifheim.beelzebu.coins.CoinsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RaceListener implements Listener
{
    private final MelonHelper plugin;

    public RaceListener( MelonHelper plugin )
    {
        this.plugin = plugin;
    }

    @EventHandler( priority = EventPriority.NORMAL )
    public void onPlayerJoin( PlayerJoinEvent event )
    {
        if( RaceUtils.hasRace( event.getPlayer() ) )
        {
            return;
        }

        giveCoin( event.getPlayer() );

        plugin.getServer().getScheduler().scheduleSyncDelayedTask( plugin, () -> Bukkit.dispatchCommand( event.getPlayer(), "menu open welcome" ), 10L );
    }

    @EventHandler( priority = EventPriority.LOWEST )
    public void onPlayerMove( PlayerMoveEvent event )
    {
        if( event.getFrom().getBlock().equals( event.getTo().getBlock() ) )
        {
            return;
        }

        if( RaceUtils.hasRace( event.getPlayer() ) )
        {
            return;
        }
        event.getPlayer().sendMessage( RaceUtils.getRacePrefix() + ChatColor.GRAY + "You must pick a race!" );
        Bukkit.dispatchCommand( event.getPlayer(), "menu open race" );
        giveCoin( event.getPlayer() );
    }

    private void giveCoin( Player player )
    {
        if( CoinsAPI.getCoins( player.getUniqueId() ) < 1 )
        {
            CoinsAPI.addCoins( player.getUniqueId(), 1.0 );
        }
    }
}
