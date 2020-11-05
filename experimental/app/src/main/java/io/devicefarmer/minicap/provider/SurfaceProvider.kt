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

package io.devicefarmer.minicap.provider

import android.graphics.Rect
import android.net.LocalSocket
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Size
import io.devicefarmer.minicap.utils.DisplayInfo
import io.devicefarmer.minicap.utils.DisplayManagerGlobal
import io.devicefarmer.minicap.utils.SurfaceControl

class SurfaceProvider(targetSize: Size) : BaseProvider(targetSize) {
    constructor() : this(currentScreenSize())

    companion object {
        private fun currentScreenSize(): Size {
            return currentDisplayInfo().run {
                Size(this.size.width, this.size.height)
            }
        }

        private fun currentDisplayInfo(): DisplayInfo {
            return DisplayManagerGlobal.getDisplayInfo(0)
        }
    }

    private val handler: Handler = Handler(Looper.getMainLooper())
    private var display: IBinder? = null
    private val displayInfo: DisplayInfo = currentDisplayInfo()

    override fun getScreenSize(): Size {
        return displayInfo.size
    }

    override fun onConnection(socket: LocalSocket) {
        super.onConnection(socket)
        println("createDisplay")
        //must be done on the main thread
        display = SurfaceControl.createDisplay("minicap", true)

        //initialise the surface to get the display in the ImageReader
        SurfaceControl.openTransaction()
        try {
            SurfaceControl.setDisplaySurface(display, getImageReader().surface)
            SurfaceControl.setDisplayProjection(
                display,
                0,
                Rect(0, 0, getScreenSize().width, getScreenSize().height),
                Rect(0, 0, getTargetSize().width, getTargetSize().height)
            )
            SurfaceControl.setDisplayLayerStack(display, displayInfo.layerStack)
        } finally {
            SurfaceControl.closeTransaction()
        }
        getImageReader().setOnImageAvailableListener(this, handler)
    }
}
