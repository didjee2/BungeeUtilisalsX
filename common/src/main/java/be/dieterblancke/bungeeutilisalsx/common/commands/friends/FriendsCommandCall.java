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

package be.dieterblancke.bungeeutilisalsx.common.commands.friends;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandBuilder;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.ParentCommand;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.commands.friends.sub.*;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.List;

public class FriendsCommandCall extends ParentCommand implements CommandCall
{

    public FriendsCommandCall()
    {
        super( user ->
                {
                    if ( ConfigFiles.FRIENDS_CONFIG.getConfig().getBoolean( "command.send-message" ) )
                    {
                        user.sendLangMessage( "friends.help.message" );
                    }
                }
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "add" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.add" )
                        .executable( new FriendAddSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "accept" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.accept" )
                        .executable( new FriendAcceptSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "deny" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.deny" )
                        .executable( new FriendDenySubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "removerequest" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.removerequest" )
                        .executable( new FriendRemoveRequestSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "remove" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.remove" )
                        .executable( new FriendRemoveSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "list" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.list" )
                        .executable( new FriendListSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "requests" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.requests" )
                        .executable( new FriendRequestsSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "msg" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.msg" )
                        .executable( new FriendMsgSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "reply" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.reply" )
                        .executable( new FriendReplySubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "settings" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.settings" )
                        .executable( new FriendSettingsSubCommandCall() )
                        .build()
        );
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.isEmpty() )
        {
            if ( ConfigFiles.FRIENDS_CONFIG.getConfig().getBoolean( "command.open-gui" ) )
            {
                this.sendGuiOpenPluginMessage( user, "friend" );
            }
        }

        super.onExecute( user, args, parameters );
    }

    private void sendGuiOpenPluginMessage( final User user, final String gui )
    {
        final IProxyServer server = BuX.getInstance().proxyOperations().getServerInfo( user.getServerName() );

        if ( server != null )
        {
            final ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF( "friends:gui" );
            out.writeUTF( "open" );
            out.writeUTF( gui );
            out.writeUTF( user.getName() );

            server.sendPluginMessage( "bux:main", out.toByteArray() );
        }
    }
}
