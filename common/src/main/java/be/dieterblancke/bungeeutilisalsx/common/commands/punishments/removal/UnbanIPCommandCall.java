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

package be.dieterblancke.bungeeutilisalsx.common.commands.punishments.removal;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.punishment.UserPunishRemoveEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.commands.punishments.PunishmentCommand;

import java.util.List;

public class UnbanIPCommandCall extends PunishmentCommand
{

    public UnbanIPCommandCall()
    {
        super( null, false );
    }

    @Override
    public void onPunishmentExecute( final User user, final List<String> args, final List<String> parameters, final PunishmentArgs punishmentArgs )
    {
        // do nothing
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final PunishmentRemovalArgs punishmentRemovalArgs = loadRemovalArguments( user, args );

        if ( punishmentRemovalArgs == null )
        {
            user.sendLangMessage( "punishments.unbanip.usage" + ( useServerPunishments() ? "-server" : "" ) );
            return;
        }
        if ( !punishmentRemovalArgs.hasJoined() )
        {
            user.sendLangMessage( "never-joined" );
            return;
        }
        final UserStorage storage = punishmentRemovalArgs.getStorage();
        if ( !dao().getPunishmentDao().getBansDao().isIPBanned( storage.getIp(), punishmentRemovalArgs.getServerOrAll() ) )
        {
            user.sendLangMessage( "punishments.unbanip.not-banned" );
            return;
        }

        if ( punishmentRemovalArgs.launchEvent( UserPunishRemoveEvent.PunishmentRemovalAction.UNBANIP ) )
        {
            return;
        }
        final IPunishmentHelper executor = BuX.getApi().getPunishmentExecutor();
        dao().getPunishmentDao().getBansDao().removeCurrentIPBan(
                storage.getIp(),
                user.getName(),
                punishmentRemovalArgs.getServerOrAll()
        );

        final PunishmentInfo info = new PunishmentInfo();
        info.setUser( punishmentRemovalArgs.getPlayer() );
        info.setId( "-1" );
        info.setExecutedBy( user.getName() );
        info.setRemovedBy( user.getName() );
        info.setServer( punishmentRemovalArgs.getServerOrAll() );

        user.sendLangMessage( "punishments.unbanip.executed", executor.getPlaceHolders( info ).toArray( new Object[0] ) );

        if ( !parameters.contains( "-s" ) )
        {
            if ( parameters.contains( "-nbp" ) )
            {
                BuX.getApi().langBroadcast(
                        "punishments.unbanip.broadcast",
                        executor.getPlaceHolders( info ).toArray( new Object[]{} )
                );
            }
            else
            {
                BuX.getApi().langPermissionBroadcast(
                        "punishments.unbanip.broadcast",
                        ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "commands.unbanip.broadcast" ),
                        executor.getPlaceHolders( info ).toArray( new Object[]{} )
                );
            }
        }
    }
}