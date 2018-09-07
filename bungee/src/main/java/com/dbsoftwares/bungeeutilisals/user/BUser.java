package com.dbsoftwares.bungeeutilisals.user;

/*
 * Created by DBSoftwares on 04 september 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserUnloadEvent;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.user.UserCooldowns;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.IExperimentalUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.Version;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.configuration.api.IConfiguration;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Date;
import java.util.Objects;
import java.util.UUID;

@Setter
public class BUser implements User {

    private String name;
    private UUID uuid;
    private Boolean socialspy;
    private ExperimentalUser experimental;
    private UserCooldowns cooldowns;
    private UserStorage storage;
    private PunishmentInfo mute;

    @Getter
    private boolean inStaffChat;

    @Override
    public void load(String name, UUID uuid, String IP) {
        Dao dao = BungeeUtilisals.getInstance().getDatabaseManagement().getDao();

        this.name = name;
        this.uuid = uuid;
        this.experimental = new ExperimentalUser(this);
        this.storage = new UserStorage();
        this.cooldowns = new UserCooldowns();

        if (dao.getUserDao().exists(uuid)) {
            storage = dao.getUserDao().getUserData(uuid);

            storage.setLanguage(BUCore.getApi().getLanguageManager().getLanguageIntegration().getLanguage(uuid));
        } else {
            final Language defLanguage = BUCore.getApi().getLanguageManager().getDefaultLanguage();
            final Date date = new Date(System.currentTimeMillis());

            dao.getUserDao().createUser(
                    uuid,
                    name,
                    IP,
                    defLanguage
            );

            storage = new UserStorage(uuid, name, IP, defLanguage, date, date);
        }

        if (!storage.getUserName().equalsIgnoreCase(name)) { // Stored name != user current name | Name changed?
            storage.setUserName(name);
            dao.getUserDao().setName(uuid, name);
        }

        if (FileLocation.PUNISHMENTS.getConfiguration().getBoolean("enabled")) {
            if (dao.getPunishmentDao().isPunishmentPresent(PunishmentType.MUTE, uuid, null, true)) {
                mute = dao.getPunishmentDao().getPunishment(PunishmentType.MUTE, uuid, null);
            } else if (dao.getPunishmentDao().isPunishmentPresent(PunishmentType.TEMPMUTE, uuid, null, true)) {
                mute = dao.getPunishmentDao().getPunishment(PunishmentType.TEMPMUTE, uuid, null);
            } else if (dao.getPunishmentDao().isPunishmentPresent(PunishmentType.IPMUTE, null, getIP(), true)) {
                mute = dao.getPunishmentDao().getPunishment(PunishmentType.IPMUTE, null, getIP());
            } else if (dao.getPunishmentDao().isPunishmentPresent(PunishmentType.IPTEMPMUTE, null, getIP(), true)) {
                mute = dao.getPunishmentDao().getPunishment(PunishmentType.IPTEMPMUTE, null, getIP());
            }
        }

        UserLoadEvent userLoadEvent = new UserLoadEvent(this);
        BungeeUtilisals.getApi().getEventLoader().launchEvent(userLoadEvent);
    }

    @Override
    public void unload() {
        save();
        cooldowns.remove();

        UserUnloadEvent event = new UserUnloadEvent(this);
        BungeeUtilisals.getApi().getEventLoader().launchEventAsync(event);
    }

    @Override
    public void save() {
        BungeeUtilisals.getInstance().getDatabaseManagement().getDao().getUserDao()
                .updateUser(uuid, getName(), getIP(), getLanguage(), new Date(System.currentTimeMillis()));
    }

    @Override
    public UserStorage getStorage() {
        return storage;
    }

    @Override
    public UserCooldowns getCooldowns() {
        return cooldowns;
    }

    @Override
    public String getIP() {
        return Utils.getIP(getParent().getAddress());
    }

    @Override
    public Language getLanguage() {
        return storage.getLanguage();
    }

    @Override
    public void setLanguage(Language language) {
        storage.setLanguage(language);
    }

    @Override
    public CommandSender sender() {
        return getParent();
    }

    @Override
    public void sendRawMessage(String message) {
        sendMessage(new TextComponent(PlaceHolderAPI.formatMessage(this, message)));
    }

    @Override
    public void sendRawColorMessage(String message) {
        sendMessage(Utils.format(this, message));
    }

    @Override
    public void sendMessage(String message) {
        sendMessage(getLanguageConfig().getString("prefix"), PlaceHolderAPI.formatMessage(this, message));
    }

    @Override
    public void sendLangMessage(String path) {
        sendLangMessage(true, path);
    }

    @Override
    public void sendLangMessage(String path, Object... placeholders) {
        sendLangMessage(true, path, placeholders);
    }

    @Override
    public void sendLangMessage(boolean prefix, String path) {
        if (getLanguageConfig().isList(path)) {
            for (String message : getLanguageConfig().getStringList(path)) {
                if (prefix) {
                    sendMessage(message);
                } else {
                    sendRawColorMessage(message);
                }
            }
        } else {
            if (prefix) {
                sendMessage(getLanguageConfig().getString(path));
            } else {
                sendRawColorMessage(getLanguageConfig().getString(path));
            }
        }
    }

    @Override
    public void sendLangMessage(boolean prefix, String path, Object... placeholders) {
        if (getLanguageConfig().isList(path)) {
            for (String message : getLanguageConfig().getStringList(path)) {
                for (int i = 0; i < placeholders.length - 1; i += 2) {
                    message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
                }

                if (prefix) {
                    sendMessage(message);
                } else {
                    sendRawColorMessage(message);
                }
            }
        } else {
            String message = getLanguageConfig().getString(path);
            for (int i = 0; i < placeholders.length - 1; i += 2) {
                message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
            }

            if (prefix) {
                sendMessage(message);
            } else {
                sendRawColorMessage(message);
            }
        }
    }

    @Override
    public void sendMessage(String prefix, String message) {
        sendMessage(Utils.format(prefix + PlaceHolderAPI.formatMessage(this, message)));
    }

    @Override
    public void sendMessage(BaseComponent component) {
        getParent().sendMessage(component);
    }

    @Override
    public void sendMessage(BaseComponent[] components) {
        getParent().sendMessage(components);
    }

    @Override
    public void kick(String reason) {
        BUCore.getApi().getSimpleExecutor().asyncExecute(() -> getParent().disconnect(Utils.format(reason)));
    }

    @Override
    public void langKick(String path, Object... placeholders) {
        if (getLanguageConfig().isList(path)) {
            StringBuilder builder = new StringBuilder();

            for (String message : getLanguageConfig().getStringList(path)) {
                for (int i = 0; i < placeholders.length - 1; i += 2) {
                    message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
                }

                builder.append(message).append("\n");
            }
            kick(builder.toString());
        } else {
            String message = getLanguageConfig().getString(path);
            for (int i = 0; i < placeholders.length - 1; i += 2) {
                message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
            }

            kick(message);
        }
    }

    @Override
    public void forceKick(String reason) {
        getParent().disconnect(Utils.format(reason));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void sendNoPermMessage() {
        sendLangMessage("no-permission");
    }

    @Override
    public void setSocialspy(Boolean socialspy) {
        this.socialspy = socialspy;
    }

    @Override
    public Boolean isSocialSpy() {
        return socialspy;
    }

    @Override
    public ProxiedPlayer getParent() {
        return ProxyServer.getInstance().getPlayer(name);
    }

    @Override
    public IConfiguration getLanguageConfig() {
        return BUCore.getApi().getLanguageManager().getLanguageConfiguration(BUCore.getApi().getPlugin(), this);
    }

    @Override
    public IExperimentalUser experimental() {
        return experimental;
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public String getServerName() {
        return getParent().getServer().getInfo().getName();
    }

    @Override
    public boolean isMuted() {
        return mute != null;
    }

    @Override
    public PunishmentInfo getMuteInfo() {
        return mute;
    }

    @Override
    public Version getVersion() {
        return Version.getVersion(getParent().getPendingConnection().getVersion());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        BUser user = (BUser) o;
        return user.name.equalsIgnoreCase(name) && user.uuid.equals(uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, uuid);
    }
}