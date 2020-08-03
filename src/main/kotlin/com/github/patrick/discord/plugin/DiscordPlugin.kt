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

package com.github.patrick.discord.plugin

import com.github.patrick.discord.DEBUG
import com.github.patrick.discord.RELAY_CHANNEL
import com.github.patrick.discord.TOKEN
import com.github.patrick.discord.handler.BukkitChatHandler
import com.github.patrick.discord.init
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class DiscordPlugin : JavaPlugin() {
    override fun onLoad() {
        saveDefaultConfig()
        config?.run {
            TOKEN = requireNotNull(getString("token"))
            RELAY_CHANNEL = requireNotNull(getLong("relay-channel").takeIf {
                it != 0L
            })
            DEBUG = getBoolean("debug")
        }

        Thread {
            init()
        }.start()
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(BukkitChatHandler(), this)
    }
}