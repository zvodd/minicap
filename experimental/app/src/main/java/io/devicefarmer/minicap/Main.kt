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

import android.os.Looper
import android.util.Size
import io.devicefarmer.minicap.provider.SurfaceProvider

/**
 * Main entry point that can be launched as follow:
 * adb shell CLASSPATH=/data/local/tmp/minicap-debug.apk app_process /system/bin io.devicefarmer.minicap.Main
 *
 * For now, only somehow parses parameter -P <w>x<h>@<w>x<h>/{0|90|180|270}
 */
class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            var provider : SurfaceProvider
            val projection = args.getOrNull(args.indexOf("-P")+1)?.run {
                val targetSize = this.split('@','/')[1].split('x')
                Projection(targetSize[0].toInt(), targetSize[1].toInt())
            }
            //the stf process reads this
            println("PID: ${android.os.Process.myPid()}")
            Looper.prepareMainLooper()
            provider = if(projection == null) {
                SurfaceProvider()
            } else {
                SurfaceProvider(Size(projection.w, projection.h))
            }
            val server = SimpleServer(provider)
            server.start()
            Looper.loop()
        }
    }

    data class Projection(val w: Int, val h: Int)
}
