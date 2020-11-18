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

package com.dbsoftwares.bungeeutilisalsx.common.api.event.event;

public class Priority
{

    public static final int LOWEST = -2000;
    public static final int LOW = -1000;
    public static final int NORMAL = 0;
    public static final int HIGH = 1000;
    public static final int HIGHEST = 2000;
    public static final int DEBUG = Integer.MAX_VALUE;

    private Priority()
    {
    }
}