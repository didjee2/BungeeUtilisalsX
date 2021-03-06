package be.dieterblancke.bungeeutilisalsx.common.api.hubbalancer;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
public class ServerData
{

    private Set<HubServerType> types;
    private String name;
    private boolean online;

    public IProxyServer getServerInfo()
    {
        return BuX.getInstance().proxyOperations().getServerInfo( name );
    }

    public int getCount()
    {
        return BuX.getApi().getPlayerUtils().getPlayerCount( name );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final ServerData data = (ServerData) o;
        return Objects.equals( name, data.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name );
    }

    public boolean isType( final HubServerType type )
    {
        return types.contains( type );
    }

}
