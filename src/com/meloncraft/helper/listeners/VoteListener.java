package com.meloncraft.helper.listeners;

import com.meloncraft.helper.MelonHelper;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.nifheim.beelzebu.coins.CoinsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;

public class VoteListener implements Listener
{
    private final Set<String> offlineVoters;

    public VoteListener()
    {
        this.offlineVoters = new HashSet<>();
    }

    @EventHandler( priority = EventPriority.NORMAL )
    public void onVotifierEvent(VotifierEvent event)
    {
        Vote vote = event.getVote();

        Player player = Bukkit.getServer().getPlayer( vote.getUsername() );

        if( player != null )
        {
            Bukkit.getServer().broadcastMessage( MelonHelper.getMollyPrefix() + "Yass! " + player.getDisplayName() + " voted for Meloncraft ;3" );
            givePlayerGifts( player );
        }
        else
        {
            Bukkit.getServer().broadcastMessage( MelonHelper.getMollyPrefix() + "Yass! " + vote.getUsername() + " voted for Meloncraft ;3" );
            offlineVoters.add( vote.getUsername().toLowerCase() );
        }
    }

    @EventHandler( priority = EventPriority.LOWEST )
    public void onPlayerJoin( PlayerJoinEvent event )
    {
        Player player = event.getPlayer();
        if( player == null || !player.isOnline() )
        {
            return;
        }

        String lowerName = player.getName().toLowerCase();

        if( offlineVoters.contains( lowerName ) )
        {
            offlineVoters.remove( lowerName );
            givePlayerGifts( player );
        }
    }

    private void givePlayerGifts( Player player )
    {
        String playerName = player.getDisplayName();

        System.out.println( "Promoting " + playerName + "..." );

        player.sendMessage( MelonHelper.getMollyPrefix() + "Now promoting you to Voter Rank ;D - You got 1 bux!" );

        Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), "lp user " + playerName + " parent addtemp voter 30m" );
        Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), "lp user " + playerName + " parent add member" );

        CoinsAPI.addCoins( player.getUniqueId(), 1.0, false );

        //Give the player an I voted cookie.
        PlayerInventory inventory = player.getInventory();

        String myDisplayName = "I Voted!";

        ItemStack myItem = new ItemStack( Material.COOKIE, 1 );
        ItemMeta im = myItem.getItemMeta();
        im.setDisplayName( myDisplayName );
        myItem.setItemMeta( im );

        inventory.addItem( myItem );
    }
}