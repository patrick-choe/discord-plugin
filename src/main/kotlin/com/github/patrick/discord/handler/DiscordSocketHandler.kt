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

package com.github.patrick.discord.handler

import com.github.patrick.discord.CLIENT
import com.github.patrick.discord.DEBUG
import com.github.patrick.discord.PARSER
import com.github.patrick.discord.RELAY_CHANNEL
import com.github.patrick.discord.connect
import com.github.patrick.discord.serial.identify
import com.google.gson.JsonNull
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketFrame
import org.bukkit.Bukkit
import org.bukkit.ChatColor

class DiscordSocketHandler : WebSocketAdapter() {
    override fun onConnected(socket: WebSocket, headers: Map<String, List<String>>) {
        if (DEBUG) {
            println("connected")
        }
    }

    override fun onDisconnected(socket: WebSocket, serverCloseFrame: WebSocketFrame, clientCloseFrame: WebSocketFrame, closedByServer: Boolean) {
        if (DEBUG) {
            println("disconnected - server-side: $closedByServer")
            println("last server message: ${serverCloseFrame.payloadText}")
            println("last client message: ${clientCloseFrame.payloadText}")
        }
        CLIENT.connected = false
        CLIENT.online = false
        connect()
    }

    override fun onTextMessage(socket: WebSocket, message: String) {
        if (DEBUG) {
            println("message: $message")
        }
        PARSER.parse(message).asJsonObject.run {
            val sequence = requireNotNull(get("s"))
            CLIENT.sequence = if (sequence is JsonNull) null else sequence.asInt

            when (requireNotNull(getAsJsonPrimitive("op")?.asInt)) {
                0 -> {
                    when (requireNotNull(getAsJsonPrimitive("t")?.asString)) {
                        "READY" -> {
                            CLIENT.online = true
                            Bukkit.broadcastMessage("Connected to Discord Server, Ready to listen.")
                        }
                        "MESSAGE_CREATE" -> {
                            getAsJsonObject("d")?.run {
                                val channel = requireNotNull(getAsJsonPrimitive("channel_id")?.asString?.toLongOrNull())
                                val author = requireNotNull(
                                        getAsJsonObject("member")?.getAsJsonPrimitive("nick")
                                                ?: (getAsJsonObject("author")?.getAsJsonPrimitive("username"))
                                ).asString
                                val content = requireNotNull(getAsJsonPrimitive("content")?.asString)
                                val broadcast = ChatColor.translateAlternateColorCodes('&', content)
                                if (channel == RELAY_CHANNEL && requireNotNull(getAsJsonArray("embeds")).size() == 0) {
                                    Bukkit.broadcastMessage("${ChatColor.BLUE}[$author]${ChatColor.RESET} $broadcast")
                                }
                            }
                        }
                        else -> {}
                    }
                }
                10 -> {
                    getAsJsonObject("d")?.run {
                        val interval = requireNotNull(getAsJsonPrimitive("heartbeat_interval")?.asLong)
                        CLIENT.refresh(interval)
                        CLIENT.send(identify())
                    }
                }
                11 -> {
                    CLIENT.status = true
                }
                else -> {}
            }
        }
    }
}