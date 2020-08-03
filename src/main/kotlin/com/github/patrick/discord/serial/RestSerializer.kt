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

import com.github.patrick.discord.AUTH_TYPE
import com.github.patrick.discord.DEBUG
import com.github.patrick.discord.MIME_TYPE
import com.github.patrick.discord.REST_URL
import com.github.patrick.discord.TOKEN
import com.github.patrick.discord.USER_AGENT
import java.net.URL
import javax.net.ssl.HttpsURLConnection

fun String.rest(method: String, data: String) {
    (URL("$REST_URL$this").openConnection() as HttpsURLConnection).run {
        setRequestProperty("Content-Type", MIME_TYPE)
        addRequestProperty("Authorization", "$AUTH_TYPE $TOKEN")
        addRequestProperty("User-Agent", USER_AGENT)
        doOutput = true
        requestMethod = method
        outputStream.write(data.toByteArray())
        if (DEBUG) {
            println("send: $data")
        }
        inputStream?.use {
            if (DEBUG) {
                println("message: ${String(it.readBytes())}")
            }
        }
    }
}