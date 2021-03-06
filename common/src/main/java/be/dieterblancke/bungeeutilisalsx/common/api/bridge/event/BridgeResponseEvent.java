/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package be.dieterblancke.bungeeutilisalsx.common.api.bridge.event;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.BridgeType;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.message.BridgedMessage;
import be.dieterblancke.bungeeutilisalsx.common.api.event.AbstractEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@AllArgsConstructor
@EqualsAndHashCode( callSuper = true )
public class BridgeResponseEvent extends AbstractEvent
{

    private final BridgeType type;
    private final UUID identifier;
    private final String from;
    private final String action;
    private final String data;

    public String asString()
    {
        return data;
    }

    public JsonPrimitive asPrimitive()
    {
        return BuX.getGson().fromJson( data, JsonPrimitive.class );
    }

    public JsonObject asObject()
    {
        return BuX.getGson().fromJson( data, JsonObject.class );
    }

    public <T> T asCasted( final Class<T> clazz )
    {
        return BuX.getGson().fromJson( data, clazz );
    }

    public void reply( final Object data )
    {
        BuX.getApi().getBridgeManager().getBridge().sendMessage( new BridgedMessage(
                type,
                identifier,
                ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ),
                Lists.newArrayList( from ),
                null,
                action,
                data
        ) );
    }
}
