package com.meloncraft.helper.permissioneffects;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectUtils
{
    public static void addEffects( Player player, int time )
    {
        if( player == null )
        {
            return;
        }

        for( PotionEffectTypes effect : PotionEffectTypes.values() )
        {
            if( player.hasPermission( "melon.pe." + effect.toString().toLowerCase() ) )
            {
                player.removePotionEffect( PotionEffectType.getByName( effect.toString() ) );
                
                for( int i = 1 ; i <= 4 ; i++ )
                {
                    if( player.hasPermission( "melon.pe." + effect.toString().toLowerCase() + "." + i ) )
                    {
                        player.addPotionEffect( new PotionEffect( PotionEffectType.getByName( effect.toString() ), time, i-1, false, false ) );
                    }
                }
                player.addPotionEffect( new PotionEffect( PotionEffectType.getByName( effect.toString() ), time, 0, false, false ) );
            }
        }
    }
}