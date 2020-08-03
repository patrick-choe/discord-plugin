/*
 * Copyright (C) 2020 PatrickKR
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact me on <mailpatrickkr@gmail.com>
 */

package com.github.patrick.discord.socket

import com.github.patrick.discord.CLIENT
import com.github.patrick.discord.DEBUG
import com.github.patrick.discord.DiscordTask
import com.github.patrick.discord.TIMER
import com.github.patrick.discord.WEBSOCKET_URL
import com.github.patrick.discord.handler.DiscordSocketHandler
import com.github.patrick.discord.serial.heartbeat
import com.github.patrick.discord.newClient
import com.github.patrick.discord.socket.ssl.WebSocketTrustManager
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketFactory
import javax.net.ssl.SSLContext

class WebSocketClient {
    private var socket: WebSocket? = null
    internal var connected = false
    internal var online = false
    internal var status = true
    internal var sequence: Int? = null
    private var refreshTask: DiscordTask? = null

    init {
        try {
            socket = WebSocketFactory().apply {
                sslContext = SSLContext.getInstance("TLS").apply {
                    init(null, arrayOf(WebSocketTrustManager()), null)
                }
                verifyHostname = false
            }.createSocket(WEBSOCKET_URL).apply {
                addListener(DiscordSocketHandler())
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }

    fun connect(): Boolean {
        return try {
            socket?.connect()
            connected = true
            true
        } catch (throwable: Throwable) {
            connected = false
            throwable.printStackTrace()
            false
        }
    }

    private fun disconnect() {
        try {
            socket?.disconnect()
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }

    fun send(message: String) {
        if (DEBUG) {
            println("send: $message")
        }
        try {
            socket?.sendText(message)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }

    fun refresh(interval: Long) {
        if (!CLIENT.status) {
            TIMER.cancel()
            refreshTask = null
            CLIENT.disconnect()
            newClient()
            return
        }
        CLIENT.status = false
        refreshTask = DiscordTask {
            send(heartbeat())
        }
        TIMER.scheduleAtFixedRate(refreshTask, interval, interval)
    }
}