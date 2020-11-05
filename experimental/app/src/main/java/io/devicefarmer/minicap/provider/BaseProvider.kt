/*
 * Copyright (C) 2020 Orange
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.devicefarmer.minicap.provider

import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.media.Image
import android.media.ImageReader
import android.net.LocalSocket
import android.util.Size
import io.devicefarmer.minicap.RemoteClient
import io.devicefarmer.minicap.SimpleServer
import java.nio.ByteBuffer


/**
 * Base class to provide images of the screen. Those captures can be setup from SurfaceControl as
 * it currently is, but could as well comes from MediaProjection API if useful in a future use case.
 */
abstract class BaseProvider(private val targetSize: Size) : SimpleServer.Listener,
    ImageReader.OnImageAvailableListener {

    private lateinit var client: RemoteClient
    private lateinit var imageReader: ImageReader
    var bitmap: Bitmap? = null

    abstract fun getScreenSize(): Size
    fun getTargetSize(): Size = targetSize

    fun getImageReader(): ImageReader{
        return imageReader
    }

    override fun onConnection(socket: LocalSocket){
        client = RemoteClient(socket).apply {
            screenSize = getScreenSize()
            targetSize = getTargetSize()
            sendBanner()
        }
        imageReader = ImageReader.newInstance(getScreenSize().width, getScreenSize().height, PixelFormat.RGBA_8888, 2)
    }

    override fun onImageAvailable(reader: ImageReader?) {
        reader?.run{
            val image = acquireLatestImage()
            if (image != null) {
                val planes: Array<Image.Plane> = image.planes
                val buffer: ByteBuffer = planes[0].buffer
                val pixelStride: Int = planes[0].pixelStride
                val rowStride: Int = planes[0].rowStride
                val rowPadding: Int = rowStride - pixelStride * width
                // createBitmap can be resources consuming
                bitmap ?: Bitmap.createBitmap(
                        width + rowPadding / pixelStride,
                        height,
                        Bitmap.Config.ARGB_8888
                    ).apply {
                    copyPixelsFromBuffer(buffer)
                    compress(Bitmap.CompressFormat.JPEG, 100, client.imageBuffer)
                }
                client.send()
                image.close()
            }
        }
    }
}
