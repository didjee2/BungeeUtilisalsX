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

package be.dieterblancke.bungeeutilisalsx.common.commands.friends.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.StaffUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;

import java.util.List;
import java.util.Optional;

public class FriendMsgSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 2 )
        {
            user.sendLangMessage( "friends.msg.usage" );
            return;
        }
        final String name = args.get( 0 );

        if ( user.getFriends().stream().noneMatch( data -> data.getFriend().equalsIgnoreCase( name ) ) )
        {
            user.sendLangMessage( "friends.msg.not-friend", "{user}", name );
            return;
        }

        if ( BuX.getApi().getPlayerUtils().isOnline( name ) && !StaffUtils.isHidden( name ) )
        {
            final Optional<User> optional = BuX.getApi().getUser( name );
            final String message = String.join( " ", args.subList( 1, args.size() ) );

            if ( optional.isPresent() && !optional.get().isVanished() )
            {
                final User target = optional.get();

                if ( !target.getFriendSettings().isMessages() )
                {
                    user.sendLangMessage( "friends.msg.disallowed" );
                    return;
                }

                if ( target.getStorage().getIgnoredUsers().stream().anyMatch( ignored -> ignored.equalsIgnoreCase( user.getName() ) ) )
                {
                    user.sendLangMessage( "friends.msg.ignored" );
                    return;
                }

                user.getStorage().setData( "FRIEND_MSG_LAST_USER", target.getName() );
                target.getStorage().setData( "FRIEND_MSG_LAST_USER", user.getName() );

                target.sendLangMessage(
                        "friends.msg.format.receive",
                        false,
                        Utils::c,
                        null,
                        "{sender}", user.getName(),
                        "{message}", message
                );
                user.sendLangMessage(
                        "friends.msg.format.send",
                        false,
                        Utils::c,
                        null,
                        "{receiver}", target.getName(),
                        "{message}", message
                );
            }
            else
            {
                user.sendLangMessage( "offline" );
            }
        }
        else
        {
            user.sendLangMessage( "offline" );
        }
    }
}
