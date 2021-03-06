package be.dieterblancke.bungeeutilisalsx.common.bridge.redis.data;

import be.dieterblancke.bungeeutilisalsx.common.api.bridge.redis.IRedisDataManager;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.redis.RedisManager;
import be.dieterblancke.bungeeutilisalsx.common.api.cache.CacheHelper;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.SimpleCacheLoader;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class RedisDataManager implements IRedisDataManager
{

    private final RedisManager redisManager;
    private final LoadingCache<String, Long> domainCountCache = CacheHelper.<String, Long>builder()
            .build( builder ->
            {
                builder.maximumSize( 100 );
                builder.expireAfterWrite( 3, TimeUnit.MINUTES );
            }, this::getAmountOfOnlineUsersOnDomainUncached );

    @Override
    public void loadRedisUser( final User user )
    {
        final String uuid = user.getUuid().toString();
        final String domain = user.getJoinedHost();

        this.redisManager.execute( commands ->
        {
            commands.sadd( RedisDataConstants.DOMAIN_PREFIX + domain, uuid );
        } );
    }

    @Override
    public void unloadRedisUser( final User user )
    {
        final String uuid = user.getUuid().toString();
        final String domain = user.getJoinedHost();

        this.redisManager.execute( commands ->
        {
            commands.srem( RedisDataConstants.DOMAIN_PREFIX + domain, uuid );
        } );
    }

    @Override
    public long getAmountOfOnlineUsersOnDomain( final String domain )
    {
        try
        {
            return domainCountCache.get( domain );
        }
        catch ( ExecutionException e )
        {
            e.printStackTrace();
            return this.getAmountOfOnlineUsersOnDomainUncached( domain );
        }
    }

    private long getAmountOfOnlineUsersOnDomainUncached( final String domain )
    {
        return redisManager.execute( commands ->
        {
            return commands.scard( RedisDataConstants.DOMAIN_PREFIX + domain );
        } );
    }

    private static final class RedisDataConstants
    {
        private static final String DOMAIN_PREFIX = "domain:";
    }
}
