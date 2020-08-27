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
    return json {
        it["op"] = 1
        it["d"] = sequence
    }
}

fun identify(): String {
    return json {
        it["op"] = 2
        it["d"] = { data ->
            data["token"] = TOKEN
            data["properties"] = { properties ->
                properties["\$os"] = "windows"
                properties["\$browser"] = "minecraft"
                properties["\$device"] = "minecraft"
            }
            data["presence"] = { presence ->
                presence["game"] = { game ->
                    game["name"] = "Minecraft"
                    game["type"] = 2
                }
                presence["status"] = "online"
                presence["afk"] = false
            }
            data["intents"] = 512
        }
    }
}

fun createEmbed(content: String, name: String): String {
    return json {
        it["embed"] = { embed ->
            embed["author"] = { author ->
                author["name"] = name
            }
            embed["description"] = content
            embed["color"] = 5592575
            embed["footer"] = { footer ->
                footer["text"] = "from Minecraft"

            }
        }
    }
}

private fun json(value: (JsonObject) -> Unit): String {
    return JsonObject().apply(value).toString()
}

private operator fun JsonObject.set(property: String, value: String?) {
    add(property, if (value == null) JsonNull.INSTANCE else JsonPrimitive(value))
}

private operator fun JsonObject.set(property: String, value: Number?) {
    add(property, if (value == null) JsonNull.INSTANCE else JsonPrimitive(value))
}

private operator fun JsonObject.set(property: String, value: Boolean?) {
    add(property, if (value == null) JsonNull.INSTANCE else JsonPrimitive(value))
}

private operator fun JsonObject.set(property: String, value: (JsonObject) -> Unit) {
    add(property, JsonObject().apply(value))
}