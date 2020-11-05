/*
 * Copyright (C) 2020 Orange
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.devicefarmer.minicap

import android.net.LocalServerSocket
import android.net.LocalSocket
import java.io.IOException

/**
 * Minimalist abstraction of a "server" for the proof of concept
 */
class SimpleServer(var listener: Listener) {
    private val SOCKET = "minicap"
    private var serverSocket = LocalServerSocket(SOCKET)

    interface Listener {
        fun onConnection(socket: LocalSocket)
    }

    fun start() {
        try {
            System.out.println("Listening on ${SOCKET}");
            val clientSocket: LocalSocket = serverSocket.accept()
            listener.onConnection(clientSocket)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}
