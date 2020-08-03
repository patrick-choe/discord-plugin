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

package com.github.patrick.discord

import com.github.patrick.discord.socket.WebSocketClient
import com.google.gson.JsonParser
import org.bukkit.Bukkit
import java.util.Timer

internal lateinit var TOKEN: String
internal lateinit var CLIENT: WebSocketClient

internal lateinit var PARSER: JsonParser
    private set
internal lateinit var TIMER: Timer
    private set

internal lateinit var REST_URL: String
    private set
internal lateinit var WEBSOCKET_URL: String
    private set
internal lateinit var MIME_TYPE: String
    private set
internal lateinit var AUTH_TYPE: String
    private set
internal lateinit var USER_AGENT: String
    private set

internal var RELAY_CHANNEL: Long? = null
internal var DEBUG = false

fun init() {
    @Suppress("SpellCheckingInspection")
    PARSER = JsonParser()
    TIMER = Timer()

    REST_URL = "https://discord.com/api/"
    WEBSOCKET_URL = "wss://gateway.discord.gg/?v=6&encoding=json"
    MIME_TYPE = "application/json"
    AUTH_TYPE = "Bot"
    USER_AGENT = "Minecraft Discord/1.0-SNAPSHOT"

    newClient()
}

internal fun newClient() {
    CLIENT = WebSocketClient()
    connect()
}

internal fun connect() {
    if (!CLIENT.connect()) {
        Bukkit.broadcastMessage("Unable to connect to Discord Server, Retrying in 5 seconds")
        TIMER.schedule(DiscordTask {
            connect()
        }, 5000)
    } else if (DEBUG) {
        println("Successfully connected to $WEBSOCKET_URL")
    }
}