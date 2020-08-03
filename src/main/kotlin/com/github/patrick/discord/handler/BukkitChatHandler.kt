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
import com.github.patrick.discord.RELAY_CHANNEL
import com.github.patrick.discord.serial.createEmbed
import com.github.patrick.discord.serial.rest
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class BukkitChatHandler : Listener {
    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        Thread {
            val channel = RELAY_CHANNEL?: return@Thread
            if (CLIENT.connected) {
                "channels/$channel/messages".rest("POST", createEmbed(event.message, event.player.name))
            }
        }.start()
    }
}