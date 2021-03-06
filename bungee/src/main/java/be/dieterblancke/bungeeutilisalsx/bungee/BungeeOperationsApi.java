package be.dieterblancke.bungeeutilisalsx.bungee;

import be.dieterblancke.bungeeutilisalsx.bungee.utils.BungeeServer;
import be.dieterblancke.bungeeutilisalsx.bungee.utils.CommandHolder;
import be.dieterblancke.bungeeutilisalsx.common.ProxyOperationsApi;
import be.dieterblancke.bungeeutilisalsx.common.api.command.Command;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.dump.PluginInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BungeeOperationsApi implements ProxyOperationsApi
{

    private final LoadingCache<String, IProxyServer> proxyServerCache = CacheBuilder.newBuilder()
            .expireAfterWrite( 1, TimeUnit.MINUTES )
            .build( new CacheLoader<String, IProxyServer>()
            {
                @Override
                public IProxyServer load( final String serverName )
                {
                    final ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo( serverName );

                    if ( serverInfo == null )
                    {
                        throw new RuntimeException( "Could not find server " + serverName + "!" );
                    }

                    return new BungeeServer( serverInfo );
                }
            } );

    private final Map<Command, CommandHolder> commandHolders = new HashMap<>();

    @Override
    public void registerCommand( final Command command )
    {
        if ( !commandHolders.containsKey( command ) )
        {
            final CommandHolder commandHolder = new CommandHolder( command );

            ProxyServer.getInstance().getPluginManager().registerCommand( Bootstrap.getInstance(), commandHolder );

            commandHolders.put( command, commandHolder );
        }
    }

    @Override
    public void unregisterCommand( final Command command )
    {
        if ( commandHolders.containsKey( command ) )
        {
            ProxyServer.getInstance().getPluginManager().unregisterCommand( this.commandHolders.remove( command ) );
        }
    }

    @Override
    public List<IProxyServer> getServers()
    {
        return ProxyServer.getInstance()
                .getServers()
                .values()
                .stream()
                .map( ServerInfo::getName )
                .map( this::getServerInfo )
                .collect( Collectors.toList() );
    }

    @Override
    public IProxyServer getServerInfo( final String serverName )
    {
        try
        {
            return this.proxyServerCache.get( serverName );
        }
        catch ( ExecutionException e )
        {
            final ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo( serverName );
            return serverInfo == null ? null : new BungeeServer( serverInfo );
        }
    }

    @Override
    public String getProxyIdentifier()
    {
        return ProxyServer.getInstance().getName() + " " + ProxyServer.getInstance().getVersion();
    }

    @Override
    public List<PluginInfo> getPlugins()
    {
        return ProxyServer.getInstance().getPluginManager().getPlugins()
                .stream()
                .map( Plugin::getDescription )
                .map( this::getPluginInfo )
                .collect( Collectors.toList() );
    }

    @Override
    public Optional<PluginInfo> getPlugin( final String pluginName )
    {
        final Plugin plugin = ProxyServer.getInstance().getPluginManager().getPlugin( pluginName );

        return Optional.ofNullable( plugin )
                .map( Plugin::getDescription )
                .map( this::getPluginInfo );
    }

    private PluginInfo getPluginInfo( final PluginDescription pluginDescription )
    {
        return new PluginInfo(
                pluginDescription.getName(),
                pluginDescription.getVersion(),
                pluginDescription.getAuthor(),
                pluginDescription.getDepends(),
                pluginDescription.getSoftDepends(),
                pluginDescription.getDescription()
        );
    }
}
