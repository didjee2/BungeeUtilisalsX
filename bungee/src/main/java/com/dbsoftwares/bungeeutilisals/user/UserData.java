package com.dbsoftwares.bungeeutilisals.user;

import com.dbsoftwares.bungeeutilisals.api.user.interfaces.DatabaseUser;

import java.util.List;

public class UserData implements DatabaseUser {

    @Override
    public Boolean exists(String user) {
        return null;
    }

    @Override
    public List<String> getPlayersOnIP(String IP) {
        return null;
    }

    @Override
    public String getIP(String user) {
        return null;
    }

    @Override
    public void setIP(String user, String IP) {

    }
}