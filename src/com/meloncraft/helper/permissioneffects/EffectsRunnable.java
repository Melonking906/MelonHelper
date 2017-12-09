package com.meloncraft.helper.permissioneffects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EffectsRunnable implements Runnable
{
    private final int runTime;

    public EffectsRunnable( int runTime )
    {
        this.runTime = runTime + 40;
    }

    @Override
    public void run()
    {
        for( Player player : Bukkit.getOnlinePlayers() )
        {
            EffectUtils.addEffects( player, runTime );
        }
    }
}