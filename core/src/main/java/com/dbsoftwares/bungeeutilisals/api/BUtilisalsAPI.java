package com.dbsoftwares.bungeeutilisals.api;

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarColor;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarStyle;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BossBar;
import com.dbsoftwares.bungeeutilisals.api.bossbar.IBossBar;
import com.dbsoftwares.bungeeutilisals.api.event.event.IEventLoader;
import com.dbsoftwares.bungeeutilisals.api.execution.SimpleExecutor;
import com.dbsoftwares.bungeeutilisals.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisals.api.language.LanguageManager;
import com.dbsoftwares.bungeeutilisals.api.manager.IChatManager;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.user.ConsoleUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.DatabaseUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.UserCollection;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BossBar;
import com.dbsoftwares.bungeeutilisals.api.language.LanguageManager;
import com.dbsoftwares.bungeeutilisals.event.EventLoader;
import com.dbsoftwares.bungeeutilisals.manager.ChatManager;
import com.dbsoftwares.bungeeutilisals.punishments.PunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.user.UserData;
import com.dbsoftwares.bungeeutilisals.user.UserList;
import com.dbsoftwares.bungeeutilisals.event.EventLoader;
import com.dbsoftwares.bungeeutilisals.manager.ChatManager;
import com.dbsoftwares.bungeeutilisals.user.UserData;
import com.dbsoftwares.bungeeutilisals.user.UserList;
import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class BUtilisalsAPI implements BUAPI {

    private final BungeeUtilisals instance;
    private ConsoleUser console;
    private UserList users;
    private ChatManager chatManager;
    private EventLoader eventLoader;
    private LanguageManager languageManager;
    private UserData userdata;
    private SimpleExecutor simpleExecutor;
    private PunishmentExecutor punishmentExecutor;

    public BUtilisalsAPI(BungeeUtilisals instance) {
        APIHandler.registerProvider(this);

        this.instance = instance;
        this.console = new ConsoleUser();
        this.users = new UserList();
        this.chatManager = new ChatManager();
        this.eventLoader = new EventLoader();
        this.languageManager = new LanguageManager(instance);
        this.userdata = new UserData();
        this.simpleExecutor = new SimpleExecutor();
        this.punishmentExecutor = new PunishmentExecutor();
    }

    @Override
    public Collection<Announcer> getAnnouncers() {
        return Announcer.getAnnouncers().values();
    }

    @Override
    public Plugin getPlugin() {
        return instance;
    }

    @Override
    public ILanguageManager getLanguageManager() {
        return languageManager;
    }

    @Override
    public IEventLoader getEventLoader() {
        return eventLoader;
    }

    @Override
    public Optional<User> getUser(String name) {
        return users.fromName(name);
    }

    @Override
    public Optional<User> getUser(ProxiedPlayer player) {
        return users.fromPlayer(player);
    }

    @Override
    public UserCollection getUsers() {
        return users;
    }

    @Override
    public UserCollection getUsers(String permission) {
        UserList list = new UserList();
        users.stream().filter(user -> user.getParent().hasPermission(permission)).forEach(list::add);
        return list;
    }

    @Override
    public UserCollection newUserCollection() {
        return new UserList();
    }

    @Override
    public DatabaseUser getUserData() {
        return userdata;
    }

    @Override
    public IChatManager getChatManager() {
        return chatManager;
    }

    @Override
    public SimpleExecutor getSimpleExecutor() {
        return simpleExecutor;
    }

    @Override
    public IConfiguration getConfig(FileLocation location) {
        return location.getConfiguration();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return instance.getDatabaseManagement().getConnection();
    }

    @Override
    public IPunishmentExecutor getPunishmentExecutor() {
        return punishmentExecutor;
    }

    @Override
    public ConsoleUser getConsole() {
        return console;
    }

    @Override
    public void broadcast(String message) {
        users.forEach(user -> user.sendMessage(message));
        getConsole().sendMessage(message);
    }

    @Override
    public void broadcast(String message, String permission) {
        users.stream().filter(user -> user.getParent().hasPermission(permission)).forEach(user -> user.sendMessage(message));
        getConsole().sendMessage(message);
    }

    @Override
    public void announce(String prefix, String message) {
        users.forEach(user -> user.sendMessage(prefix, message));
        getConsole().sendMessage(prefix, message);
    }

    @Override
    public void announce(String prefix, String message, String permission) {
        users.stream().filter(user -> user.getParent().hasPermission(permission)).forEach(user -> user.sendMessage(prefix, message));
        getConsole().sendMessage(prefix, message);
    }

    @Override
    public void langBroadcast(String message, Object... placeholders) {
        users.forEach(user -> user.sendLangMessage(message, placeholders));
        getConsole().sendLangMessage(message, placeholders);
    }

    @Override
    public void langBroadcast(String message, String permission, Object... placeholders) {
        users.stream().filter(user -> user.getParent().hasPermission(permission)).forEach(user -> user.sendLangMessage(message, placeholders));
        getConsole().sendLangMessage(message, placeholders);
    }

    @Override
    public IBossBar createBossBar() {
        return new BossBar();
    }

    @Override
    @Deprecated
    public IBossBar createBossBar(BarColor color, BarStyle style, float progress, String message) {
        return new BossBar(color, style, progress, message);
    }

    @Override
    @Deprecated
    public IBossBar createBossBar(UUID uuid, BarColor color, BarStyle style, float progress, String message) {
        return new BossBar(uuid, color, style, progress, message);
    }

    @Override
    public IBossBar createBossBar(BarColor color, BarStyle style, float progress, BaseComponent[] message) {
        return new BossBar(color, style, progress, message);
    }

    @Override
    public IBossBar createBossBar(UUID uuid, BarColor color, BarStyle style, float progress, BaseComponent[] message) {
        return new BossBar(uuid, color, style, progress, message);
    }
}