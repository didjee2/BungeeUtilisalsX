package com.dbsoftwares.bungeeutilisals.bungee.executors;

import com.dbsoftwares.bungeeutilisals.api.event.events.UserChatEvent;
import com.dbsoftwares.bungeeutilisals.api.manager.IChatManager;
import com.dbsoftwares.bungeeutilisals.api.settings.SettingManager;
import com.dbsoftwares.bungeeutilisals.api.settings.SettingType;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.settings.chat.SwearSettings;
import com.dbsoftwares.bungeeutilisals.bungee.settings.chat.UTFSettings;

public class UserChatExecutor {

    IChatManager manager;

    public UserChatExecutor(IChatManager manager) {
        this.manager = manager;
    }

    public void onUnicodeReplace(UserChatEvent event) {
        String message = event.getMessage();
        UTFSettings settings = SettingManager.getSettings(SettingType.UTFSYMBOLS, UTFSettings.class);

        message = manager.replaceSymbols(message);

        if (settings.getFancychat() && event.getUser().getParent().hasPermission(settings.getFancyChatPerm())) {
            message = BungeeUtilisals.getApi().getChatManager().fancyFont(message);
        }

        event.setMessage(message);
    }

    public void onSwearChat(UserChatEvent event) {
        String message = event.getMessage();
        SwearSettings settings = SettingManager.getSettings(SettingType.ANTISWEAR, SwearSettings.class);

        if (manager.checkForSwear(event.getUser(), message)) {
            if (settings.getCancel()) {
                // TODO: send swear message.

                event.setCancelled(true);
            } else {
                // TODO: send swear message.

                event.setMessage(manager.replaceSwearWords(event.getUser(), message, settings.getReplacement()));
            }
        }
    }
}