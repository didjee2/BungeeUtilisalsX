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

package be.dieterblancke.bungeeutilisalsx.common.motd.handlers;

import be.dieterblancke.bungeeutilisalsx.common.motd.ConditionHandler;
import be.dieterblancke.bungeeutilisalsx.common.motd.MotdConnection;

public class NameConditionHandler extends ConditionHandler
{

    public NameConditionHandler( String condition )
    {
        super( condition.replaceFirst( "name ", "" ) );
    }

    @Override
    public boolean checkCondition( final MotdConnection connection )
    {
        final String[] args = condition.split( " " );
        final String operator = args[0];
        final String value = args[1];

        if ( operator.equalsIgnoreCase( "==" ) )
        {
            if ( connection.getName() == null )
            {
                return value.equalsIgnoreCase( "null" );
            }
            return connection.getName().equalsIgnoreCase( value );
        }
        else if ( operator.equalsIgnoreCase( "!=" ) )
        {
            if ( connection.getName() == null )
            {
                return !value.equalsIgnoreCase( "null" );
            }
            return !connection.getName().equalsIgnoreCase( value );
        }

        return false;
    }
}