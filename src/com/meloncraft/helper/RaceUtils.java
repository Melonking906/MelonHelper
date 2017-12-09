package com.meloncraft.helper;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class RaceUtils
{
    private static final String RANK_MARK = "r_";
    private static final String RACE_PREFIX = ChatColor.YELLOW + "[Race]" + ChatColor.GREEN + " ";

    public static Set<String> getRaceGroups()
    {
        Set<String> raceGroups = new HashSet<>();

        for( String group : MelonHelper.getVaultPerms().getGroups() )
        {
            if( group.contains( RANK_MARK ) )
            {
                raceGroups.add( group );
            }
        }

        return raceGroups;
    }

    public static boolean hasRace( Player player )
    {
        for( String group : MelonHelper.getVaultPerms().getPlayerGroups( player ) )
        {
            if( getRaceGroups().contains( group ) )
            {
                return true;
            }
        }

        return false;
    }

    public static String getRankMark()
    {
        return RANK_MARK;
    }

    public static String getRacePrefix()
    {
        return RACE_PREFIX;
    }
}
