/*******************************************************************************
 * Copyright (c) 2009, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Dave Locke - initial API and implementation and/or initial documentation
 */
package com.beidouapp.et.mqtt;

import com.beidouapp.et.core.EtMessage;
import com.beidouapp.et.util.HexUtil;
import com.beidouapp.et.util.Log;
import com.beidouapp.et.util.LogFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An <code>MqttOutputStream</code> lets applications write instances of
 * <code>MqttWireMessage</code>.
 */
public class MqttOutputStream extends OutputStream {
	private static final String TAG = MqttOutputStream.class.getSimpleName();
	private Log LOG = LogFactory.getLog("java");

	private BufferedOutputStream out;

	public MqttOutputStream(OutputStream out) {
		this.out = new BufferedOutputStream(out);
	}

	public void close() throws IOException {
		out.close();
	}

	public void flush() throws IOException {
		out.flush();
	}

	public void write(byte[] b) throws IOException {
		out.write(b);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}

	public void write(int b) throws IOException {
		out.write(b);
	}

	public void writeMessage(EtMessage msg) throws IOException {
		MqttMessage mqttMsg = new MqttMessage(msg.getPayload());
		MqttWireMessage mqttWireMsg = new MqttPublish(msg.getTopic(), mqttMsg);
		mqttWireMsg.setMessageId(msg.getMsgId());
		write(mqttWireMsg);
	}
	
	/**
	 * Writes an <code>MqttWireMessage</code> to the stream.
	 */
	public void write(MqttWireMessage message) throws IOException {
		final String methodName = "write";
		byte[] bytes = message.getHeader();
		byte[] pl = message.getPayload();
		out.write(bytes, 0, bytes.length);

		LOG.d(TAG, "write header:" + HexUtil.printHexString(bytes));

		int offset = 0;
		int chunckSize = 1024;
		while (offset < pl.length) {
			int length = Math.min(chunckSize, pl.length - offset);
			out.write(pl, offset, length);
			LOG.d(TAG, "write payload:" + HexUtil.printHexString(pl));
			offset += chunckSize;
		}
		out.flush();
	}
}
