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

package be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.punishments;

import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;

import java.util.List;
import java.util.UUID;

public interface KickAndWarnDao
{

    PunishmentInfo insertWarn( UUID uuid, String user, String ip, String reason, String server, String executedby );

    PunishmentInfo insertKick( UUID uuid, String user, String ip, String reason, String server, String executedby );

    List<PunishmentInfo> getKicks( final UUID uuid );

    List<PunishmentInfo> getWarns( final UUID uuid );

    List<PunishmentInfo> getKicksExecutedBy( String name );

    List<PunishmentInfo> getWarnsExecutedBy( String name );

    PunishmentInfo getKickById( final String id );

    PunishmentInfo getWarnById( final String id );
}
