package com.dbsoftwares.bungeeutilisals.api.experimental.connection;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.Connection;

import java.net.InetSocketAddress;

public class BungeeConnection implements Connection {

	@Override
	public void disconnect(String arg0) { }

	@Override
	public void disconnect(BaseComponent... arg0) { }

	@Override
	public void disconnect(BaseComponent arg0) { }

	@Override
	public boolean isConnected() {
		return false;
	}

	@Override
	public InetSocketAddress getAddress() {
		return null;
	}

	@Override
	public Unsafe unsafe() {
		return null;
	}

}