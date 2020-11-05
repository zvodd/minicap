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

package io.devicefarmer.minicap.utils

import android.graphics.Rect
import android.os.IBinder
import android.view.Surface

/**
 * Provide access to the SurfaceControl which is not part of the Android SDK using reflection.
 * This SurfaceControl relies on a jni bindings that manages the SurfaceComposerClient that is
 * in use in minicap-shared library.
 */
object SurfaceControl {
    private var CLASS: Class<*>? = null

    init {
        try {
            CLASS = Class.forName("android.view.SurfaceControl")
        } catch (e: ClassNotFoundException) {
            throw AssertionError(e)
        }
    }

    fun openTransaction() {
        try {
            CLASS!!.getMethod("openTransaction").invoke(null)
        } catch (e: Exception) {
            throw AssertionError(e)
        }
    }

    fun closeTransaction() {
        try {
            CLASS!!.getMethod("closeTransaction").invoke(null)
        } catch (e: Exception) {
            throw AssertionError(e)
        }
    }

    fun setDisplayProjection(
        displayToken: IBinder?,
        orientation: Int,
        layerStackRect: Rect?,
        displayRect: Rect?
    ) {
        try {
            CLASS!!.getMethod(
                "setDisplayProjection",
                IBinder::class.java,
                Int::class.javaPrimitiveType,
                Rect::class.java,
                Rect::class.java
            ).invoke(null, displayToken, orientation, layerStackRect, displayRect)
        } catch (e: Exception) {
            throw AssertionError(e)
        }
    }

    fun setDisplayLayerStack(displayToken: IBinder?, layerStack: Int) {
        try {
            CLASS!!.getMethod(
                "setDisplayLayerStack",
                IBinder::class.java,
                Int::class.javaPrimitiveType
            ).invoke(null, displayToken, layerStack)
        } catch (e: Exception) {
            throw AssertionError(e)
        }
    }

    fun setDisplaySurface(displayToken: IBinder?, surface: Surface?) {
        try {
            CLASS!!.getMethod("setDisplaySurface", IBinder::class.java, Surface::class.java)
                .invoke(null, displayToken, surface)
        } catch (e: Exception) {
            throw AssertionError(e)
        }
    }

    fun createDisplay(name: String?, secure: Boolean): IBinder {
        try {
            return CLASS!!.getMethod(
                "createDisplay",
                String::class.java,
                Boolean::class.javaPrimitiveType
            ).invoke(null, name, secure) as IBinder
        } catch (e: Exception) {
            throw AssertionError(e)
        }
    }
}
