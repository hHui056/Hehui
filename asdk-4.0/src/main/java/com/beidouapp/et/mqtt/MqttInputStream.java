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

import java.io.*;

/**
 * An <code>MqttInputStream</code> lets applications read instances of
 * <code>MqttWireMessage</code>.
 */
public class MqttInputStream extends InputStream {
    private DataInputStream in;

    public MqttInputStream(InputStream in) {
        this.in = new DataInputStream(in);
    }

    public int read() throws IOException {
        return in.read();
    }

    public int available() throws IOException {
        return in.available();
    }

    public void close() throws IOException {
        in.close();
    }

    /**
     * Reads an <code>MqttWireMessage</code> from the stream.
     */
    public MqttWireMessage readMqttWireMessage() throws IOException, RuntimeException {
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        byte first = in.readByte();
        // clientState.notifyReceivedBytes(1);

        byte type = (byte) ((first >>> 4) & 0x0F);
        if ((type < MqttWireMessage.MESSAGE_TYPE_CONNECT) || (type > MqttWireMessage.MESSAGE_TYPE_DISCONNECT)) {
            // Invalid MQTT message type...
            throw new RuntimeException("不支持的消息类型" + type);
        }
        long remLen = MqttWireMessage.readMBI(in).getValue();
        bais.write(first);
        // bit silly, we decode it then encode it
        bais.write(MqttWireMessage.encodeMBI(remLen));
        byte[] packet = new byte[(int) (bais.size() + remLen)];
        readFully(packet, bais.size(), packet.length - bais.size());

        byte[] header = bais.toByteArray();
        System.arraycopy(header, 0, packet, 0, header.length);
        MqttWireMessage message = MqttWireMessage.createWireMessage(packet);
        return message;

    }

    private void readFully(byte b[], int off, int len) throws IOException {
        if (len < 0)
            throw new IndexOutOfBoundsException();
        int n = 0;
        while (n < len) {
            int count = in.read(b, off + n, len - n);

            if (count < 0)
                throw new EOFException();
            n += count;
        }
    }
}
