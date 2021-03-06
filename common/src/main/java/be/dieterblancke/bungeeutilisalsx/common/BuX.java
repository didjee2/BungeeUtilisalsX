package be.dieterblancke.bungeeutilisalsx.common;

import com.google.gson.Gson;

import java.util.logging.Logger;

public class BuX
{
    private static final Gson GSON = new Gson();

    public static <T extends AbstractBungeeUtilisalsX> T getInstance()
    {
        return (T) AbstractBungeeUtilisalsX.getInstance();
    }

    public static IBuXApi getApi()
    {
        return getInstance().getApi();
    }

    public static Gson getGson()
    {
        return GSON;
    }

    public static Logger getLogger( final String name )
    {
        return getLogger();
    }

    public static Logger getLogger()
    {
        return getInstance().getLogger();
    }
}
