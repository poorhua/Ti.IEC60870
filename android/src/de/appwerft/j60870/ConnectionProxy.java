/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package de.appwerft.j60870;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import javax.net.SocketFactory;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ClientConnectionBuilder;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ConnectionEventListener;

// This proxy can be created by calling J60870.createExample({message: "hello world"})
@Kroll.proxy(creatableInModule = J60870Module.class)
public class ConnectionProxy extends KrollProxy {
	// Standard Debugging variables
	private static final String LCAT = "J60870";
	// connection params
	private InetAddress address;
	private int port;
	private Connection connection;
	private static final String INTERROGATION_ACTION_KEY = "i";
	private static final String CLOCK_SYNC_ACTION_KEY = "c";
	private KrollFunction onLoad;
	private KrollFunction onError;
	private KrollFunction onClosed;

	// Constructor
	public ConnectionProxy() {
		super();
	}

	private static class ClientEventListener implements ConnectionEventListener {

		@Override
		public void newASdu(ASdu aSdu) {
			Log.d(LCAT, "\nReceived ASDU:\n" + aSdu);

		}

		@Override
		public void connectionClosed(IOException e) {
			System.out.print("Received connection closed signal. Reason: ");
			if (!e.getMessage().isEmpty()) {
				Log.d(LCAT, e.getMessage());
			} else {
				Log.d(LCAT, "unknown");
			}

		}

	}

	@Override
	public void handleCreationDict(
			@Kroll.argument(optional = true) KrollDict options) {
		super.handleCreationDict(options);
		importFromJSON();
		importOptions(options);
		connect();
		if (options.containsKey("message")) {
			Log.d(LCAT,
					"example created with message: " + options.get("message"));
		}
	}

	private void importOptions(KrollDict opts) {
		if (opts.containsKeyAndNotNull("address")) {
			try {
				InetAddress address = InetAddress.getByName(opts
						.getString("address"));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		if (opts.containsKeyAndNotNull("port")) {
			int port = opts.getInt("port");
		}
	}

	private void importFromJSON() {
		// TODO
	}

	private void connect() {
		ClientConnectionBuilder builder = new ClientConnectionBuilder(address);
		try {
			connection = builder.setPort(port)
					.setSocketFactory(SocketFactory.getDefault()).connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Kroll.method
	public void send() {
		// ASdu asdu = new ASdu();
		connection.send(asdu);
	}

	@Kroll.method
	public void startDataTransfer(
			@Kroll.argument(optional = true) KrollDict opts)
			throws java.io.IOException {
		int timeout = 10000;
		if (opts.containsKeyAndNotNull("timeout"))
			timeout = opts.getInt("timeout");
		if (opts.containsKeyAndNotNull("onload")) {
			Object o = opts.get("onload");
			if (o instanceof KrollFunction)
				onLoad = (KrollFunction) o;
		}
		if (opts.containsKeyAndNotNull("onclosed")) {
			Object o = opts.get("onclosed");
			if (o instanceof KrollFunction)
				onClosed = (KrollFunction) o;
		}
		if (opts.containsKeyAndNotNull("onerror")) {
			Object o = opts.get("onerror");
			if (o instanceof KrollFunction)
				onError = (KrollFunction) o;
		}
		try {
			connection.startDataTransfer(new ConnectionEventListener() {
				@Override
				public void connectionClosed(IOException arg0) {
					if (onClosed != null)
						onClosed.call(getKrollObject(), new KrollDict());
				}

				@Override
				public void newASdu(ASdu asdu) {
					if (onLoad != null) {
						KrollDict kd = new KrollDict();
						kd.put("asdu", asdu.toString());
						onLoad.call(getKrollObject(), kd);
					}
				}

			}, timeout);
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}
}