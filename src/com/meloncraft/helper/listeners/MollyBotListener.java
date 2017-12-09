package com.meloncraft.helper.listeners;

import com.meloncraft.helper.MelonHelper;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;

public class MollyBotListener implements Listener
{
    private final MelonHelper plugin;
    private final HashMap<String, HashSet<String> > replies;

    private long lastTime;

    public MollyBotListener( MelonHelper plugin )
    {
        this.plugin = plugin;

        replies = new HashMap<>();
        lastTime = System.currentTimeMillis();

        List<String> rawReplies = plugin.getConfig().getStringList( "replies" );
        // "Gosh that's cenver!=joy+cat+horse"

        for( String rawReplie : rawReplies )
        {
            String[] temp = rawReplie.split( "=" );

            String reply = ChatColor.translateAlternateColorCodes( '&', MelonHelper.getMollyPrefix() + temp[0] );
            HashSet<String> words = new HashSet<>( Arrays.asList( temp[1].split( "\\+" ) ) );

            replies.put( reply, words );
        }
    }

    @EventHandler( priority = EventPriority.LOWEST )
    public void onChat( AsyncPlayerChatEvent event )
    {
        if( event.isCancelled() )
        {
            return;
        }

        String reply = getReply( event.getMessage() );

        if( reply == null )
        {
            return;
        }

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        for( Player player : players )
        {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask( plugin, () -> player.sendMessage( PlaceholderAPI.setPlaceholders( event.getPlayer(), reply ) ), 20L );
        }
    }

    private String getReply( String msg )
    {
        msg = formatMsg( msg );

        for( Map.Entry<String, HashSet<String>> reply : replies.entrySet() )
        {
            HashSet<String> keys = reply.getValue();
            String[] wordsArray = msg.split( "\\s+" ); // Split msg into a list.
            Collection<String> words = Arrays.asList( wordsArray );

            boolean isSubset = words.containsAll( keys ); // Check if the msg contains all keys.

            if( isSubset )
            {
                long time = System.currentTimeMillis();
                if( time <= (lastTime + 10 * 1000) )
                {
                    return null;
                }

                lastTime = time;

                return reply.getKey();
            }
        }

        return null;
    }

    private String formatMsg( String msg )
    {
        msg = msg.replace( '?', ' ' );
        msg = msg.replace( '!', ' ' );
        msg = msg.replace( '.', ' ' );
        msg = msg.replace( ',', ' ' );
        msg = msg.toLowerCase();

        return msg;
    }
}