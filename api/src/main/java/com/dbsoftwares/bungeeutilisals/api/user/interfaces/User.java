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

package com.dbsoftwares.bungeeutilisals.api.user.interfaces;

import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.user.UserCooldowns;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.utils.Version;
import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public interface User extends ExperimentalUser {

    /**
     * Loads the user in RAM.
     * @param parent The parent player, null if console
     */
    void load(ProxiedPlayer parent);

    /**
     * Unloads the User from storage.
     */
    void unload();

    /**
     * Saves the local user data onto the database.
     */
    void save();

    /**
     * @return User data is being stored in here.
     */
    UserStorage getStorage();

    /**
     * @return A small and simple user cooldown utility.
     */
    UserCooldowns getCooldowns();

    /**
     * @return The IP of the User.
     */
    String getIP();

    /**
     * @return The language of the User.
     */
    Language getLanguage();

    /**
     * Sets the language of the User.
     *
     * @param language The new language.
     */
    void setLanguage(Language language);

    /**
     * @return The User casted to CommandSender.
     */
    CommandSender sender();

    /**
     * Sends a raw message to the User, without CentrixCore prefix.
     *
     * @param message The message which has to be sent.
     */
    void sendRawMessage(String message);

    /**
     * Sends a raw message to the User, without CentrixCore prefix, but with colors replaced.
     *
     * @param message The message which has to be sent, will be colored.
     */
    void sendRawColorMessage(String message);

    /**
     * Sends a message to the User with the CentrixCore prefix + colors will be replaced.
     *
     * @param message The message which has to be sent. The CentrixCore prefix will appear before.
     */
    void sendMessage(String message);

    /**
     * Searches a message in the user's language configuration and sends that message.
     *
     * @param path The path to the message in the language file.
     */
    void sendLangMessage(String path);

    /**
     * Searches a message in the user's language configuration and sends that message formatted with placeholders.
     *
     * @param path         The path to the message in the language file.
     * @param placeholders The placeholders and their values (placeholder on odd place, value on even place behind placeholder)
     */
    void sendLangMessage(String path, Object... placeholders);

    /**
     * Searches a message in the user's language configuration and sends that message.
     *
     * @param prefix Should a prefix be added in front of the message or not?
     * @param path   The path to the message in the language file.
     */
    void sendLangMessage(boolean prefix, String path);

    /**
     * Searches a message in the user's language configuration and sends that message formatted with placeholders.
     *
     * @param prefix       Should a prefix be added in front of the message or not?
     * @param path         The path to the message in the language file.
     * @param placeholders The placeholders and their values (placeholder on odd place, value on even place behind placeholder)
     */
    void sendLangMessage(boolean prefix, String path, Object... placeholders);

    /**
     * Sends a message to the User with the given prefix + colors will be replaced.
     *
     * @param prefix  The prefix for the message. Mostly used for plugin prefixes.
     * @param message The message which has to be sent.
     */
    void sendMessage(String prefix, String message);

    /**
     * Sends a BaseComponent message to the user, colors will be formatted.
     * @param component The component to be sent.
     */
    void sendMessage(BaseComponent component);

    /**
     * Sends a BaseComponent message to the user, colors will be formatted.
     * @param components The components to be sent.
     */
    void sendMessage(BaseComponent[] components);

    /**
     * Synchronously kicks the User with a certain reason.
     *
     * @param reason The reason of the kick.
     */
    void kick(String reason);

    /**
     * Kicks user with message from language file.
     *
     * @param path         Path in language file.
     * @param placeholders Placeholders to be replaced.
     */
    void langKick(String path, Object... placeholders);

    /**
     * Kicks the User with a certain reason.
     *
     * @param reason The reason of the kick.
     */
    void forceKick(String reason);

    /**
     * @return The user's name.
     */
    String getName();

    /**
     * @return The user's UUID.
     */
    UUID getUUID();

    /**
     * Sends the standard no permission message to the User.
     */
    void sendNoPermMessage();

    /**
     * Sets the Socialspy of the User on or off.
     *
     * @param socialspy The status of the Socialspy, true for on, false for off.
     */
    void setSocialspy(Boolean socialspy);

    /**
     * @return Returns if the User is in Socialspy mode or not.
     */
    Boolean isSocialSpy();

    /**
     * @return The Player who's behind the User.
     */
    ProxiedPlayer getParent();

    /**
     * @return The user his language config.
     */
    IConfiguration getLanguageConfig();

    /**
     * @return True if console, false if player.
     */
    boolean isConsole();

    /**
     * @return the name of the server the user is in, returns BUNGEE in case of Console.
     */
    String getServerName();

    /**
     * @return true if user is muted, false if not.
     */
    boolean isMuted();

    /**
     * @return PunishmentInfo of current mute (if muted), null if not muted.
     */
    PunishmentInfo getMuteInfo();

    /**
     * Sets the user mute data in local cache.
     *
     * @param info The info to be stored.
     */
    void setMute(PunishmentInfo info);

    /**
     * Changes staffchat state.
     * @param staffchat true if staffchat should be enabled, false if it should be disabled.
     */
    void setInStaffChat(boolean staffchat);

    /**
     * @return true if staffchat is enabled, false if not.
     */
    boolean isInStaffChat();

    /**
     * @return the version the user is playing on
     */
    Version getVersion();
}