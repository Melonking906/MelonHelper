package com.meloncraft.helper.commands;

import com.meloncraft.helper.MelonHelper;
import com.meloncraft.helper.RaceUtils;
import net.nifheim.beelzebu.coins.CoinsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class SetRaceCommand implements CommandExecutor
{
    private static final double CHANGE_COST = 1;

    private final MelonHelper plugin;

    public SetRaceCommand( MelonHelper plugin )
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand( CommandSender sender, Command cmd, String s, String[] args )
    {
        if( !sender.hasPermission( "melon.setrace" ) )
        {
            sender.sendMessage( RaceUtils.getRacePrefix() + ChatColor.RED + "You don't have permission to set your race." );
            return true;
        }

        if( args.length < 1 )
        {
            sender.sendMessage( RaceUtils.getRacePrefix() + "Use /setrace <race> (user)" );
            return true;
        }

        //Figure out whos race is being changed.

        Player receiver;

        if( args.length > 1 )
        {
            String receiverName = args[1];

            if( !sender.getName().equalsIgnoreCase( receiverName ) && !sender.hasPermission( "melon.setrace.other" ) )
            {
                sender.sendMessage( RaceUtils.getRacePrefix() + ChatColor.RED + "You don't have permission to set other peoples race!" );
                return true;
            }

            receiver = Bukkit.getPlayer( receiverName );

            if( receiver == null || !receiver.hasPlayedBefore() )
            {
                sender.sendMessage( RaceUtils.getRacePrefix() + ChatColor.RED + receiverName + " has never played before!" );
                return true;
            }
        }
        else
        {
            if( !(sender instanceof Player) )
            {
                sender.sendMessage( RaceUtils.getRacePrefix() + ChatColor.RED + "Please enter a player name, Use /setrace <race> (user)!" );
                return true;
            }

            receiver = (Player) sender;
        }

        //Get race prefix groups.
        Set<String> raceGroups = RaceUtils.getRaceGroups();

        //Validate the race choice.
        String raceChoice = args[0].toLowerCase().trim();

        if( !raceGroups.contains( RaceUtils.getRankMark() + raceChoice ) )
        {
            sender.sendMessage( RaceUtils.getRacePrefix() + ChatColor.RED + "There is no '" + raceChoice + "' race!" );
            return true;
        }

        //Manage coin cost
        if( sender instanceof Player && !sender.hasPermission( "melon.race.free" ) )
        {
            if( CoinsAPI.getCoins( sender.getName() ) < CHANGE_COST )
            {
                sender.sendMessage( RaceUtils.getRacePrefix() + ChatColor.RED + "You don't have enough bux to change! /vote for more ;D" );
                return true;
            }

            CoinsAPI.takeCoins( sender.getName(), CHANGE_COST );
            sender.sendMessage( RaceUtils.getRacePrefix() + "You paid " + CHANGE_COST + " bux to change race!" );
        }

        //+++ Checking is done, apply race to player +++

        //Strip old race groups
        for( String group : MelonHelper.getVaultPerms().getPlayerGroups( receiver ) )
        {
            if( raceGroups.contains( group ) )
            {
                MelonHelper.getVaultPerms().playerRemoveGroup( null, receiver, group );
            }
        }

        //Add the player to the new race.
        MelonHelper.getVaultPerms().playerAddGroup( null, receiver, RaceUtils.getRankMark() + raceChoice );

        //Announce the update
        Bukkit.broadcastMessage( ChatColor.GRAY + "Ethnic update! " + ChatColor.YELLOW + receiver.getDisplayName()
                + ChatColor.GRAY + " became a "
                + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes( '&', MelonHelper.getVaultChat().getGroupSuffix( receiver.getWorld(), RaceUtils.getRankMark() + raceChoice ) )
                + ChatColor.GRAY + "!" );

        receiver.sendMessage( RaceUtils.getRacePrefix() + "You became a " + ChatColor.WHITE + raceChoice + ChatColor.GREEN + "!" );

        if( !sender.getName().equals( receiver.getName() ) )
        {
            sender.sendMessage( RaceUtils.getRacePrefix() + "You set " + receiver.getName() + " as a " + raceChoice + "!" );
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask( plugin, () ->
        {
            //Add items and extra perks
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), "kit " + raceChoice + " " + receiver.getName() );

            //Grapeling Hook
            if( MelonHelper.getVaultPerms().groupHas( (World) null, RaceUtils.getRankMark() + raceChoice, "grapplinghook.pull.self" ) )
            {
                Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), "gh give " + receiver.getName() );
                Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), "bait load pellet " + receiver.getName() + " 3" );
            }

            //Magic perms
            if( MelonHelper.getVaultPerms().groupHas( (World) null, RaceUtils.getRankMark() + raceChoice, "Magic.wand.use" ) )
            {
                Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), "wandp " + receiver.getName() + " beginner" );
            }

            //Brooms
            if( MelonHelper.getVaultPerms().groupHas( (World) null, RaceUtils.getRankMark() + raceChoice, "broomsticks.ride" ) )
            {
                Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), "broom " + receiver.getName() + " Cleansweep_One" );
            }

            //Werewolf and Vampire removal for non races
            if( !MelonHelper.getVaultPerms().groupHas( (World) null, RaceUtils.getRankMark() + raceChoice, "werewolf.becomeinfected" ) )
            {
                Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), "wwa cure " + receiver.getName() );
            }
            if( !MelonHelper.getVaultPerms().groupHas( (World) null, RaceUtils.getRankMark() + raceChoice, "vampire.kit.rank1" ) )
            {
                Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), "v set vampire false " + receiver.getName() );
            }

        }, 40L );

        return true;
    }
}
