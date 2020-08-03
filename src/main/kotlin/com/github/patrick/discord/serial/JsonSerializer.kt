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

package com.github.patrick.discord.serial

import com.github.patrick.discord.CLIENT
import com.github.patrick.discord.TOKEN
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

fun heartbeat(): String {
    val sequence = CLIENT.sequence
    return JsonObject().apply {
        add("op", 1.json())
        add("d", sequence?.json() ?: JsonNull.INSTANCE)
    }.toString()
}

fun identify(): String {
    return json {
        add("op", 2.json())
        add("d", json {
            add("token", TOKEN.json())
            add("properties", json {
                add("\$os", "windows".json())
                add("\$browser", "minecraft".json())
                add("\$device", "minecraft".json())
            })
            add("presence", json {
                add("game", json {
                    add("name", "Minecraft".json())
                    add("type", 2.json())
                })
                add("status", "online".json())
                add("afk", false.json())
            })
            add("intents", 512.json())
        })
    }.toString()
}

fun createEmbed(content: String, author: String): String {
    return json {
        add("embed", json {
            add("author", json {
                add("name", author.json())
            })
            add("description", content.json())
            add("color", 5592575.json())
            add("footer", json {
                add("text", "from Minecraft".json())
            })
        })
    }.toString()
}

private fun json(unit: JsonObject.() -> Unit): JsonObject {
    return JsonObject().apply {
        unit()
    }
}

private fun String.json(): JsonPrimitive {
    return JsonPrimitive(this)
}

private fun Number.json(): JsonPrimitive {
    return JsonPrimitive(this)
}

private fun Boolean.json(): JsonPrimitive {
    return JsonPrimitive(this)
}