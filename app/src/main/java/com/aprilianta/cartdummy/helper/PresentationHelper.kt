/***
 * Copyright (c) 2013 CommonsWare, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aprilianta.cartdummy.helper

import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.DisplayManager.DisplayListener
import android.os.Build
import android.view.Display

/**
 * A class to assist in managing a Presentation.
 *
 * To work properly, you need to forward the onResume() and onPause() events
 * from your activity to the helper.
 *
 * The helper starts in an enabled state. Call disable() to disable it (and
 * stop showing presentations) and enable() to re-enable it.
 */
class PresentationHelper(ctxt: Context, listener: Listener?) :
    DisplayListener {
    /**
     * A callback to let you know when it is time to show a Presentation
     * or stop showing that Presentation.
     */
    interface Listener {

        fun showPreso(display: Display?)
        fun clearPreso(switchToInline: Boolean)
    }

    private var listener: Listener? = null
    private var mgr: DisplayManager? = null
    private var current: Display? = null
    private var isFirstRun = true
    private var isEnabled = true

    /**
     * Call this from onResume() of your activity, so we can determine what
     * changes need to be made to the Presentation, if any
     */
    fun onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            handleRoute()
            mgr!!.registerDisplayListener(this, null)
        }
    }

    /**
     * Call this from onPause() of your activity, so we can determine what
     * changes need to be made to the Presentation, if any
     */
    fun onPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            listener!!.clearPreso(false)
            current = null
            mgr!!.unregisterDisplayListener(this)
        }
    }

    /**
     * Call this to re-enable the helper after having previously called disable()
     */
    fun enable() {
        isEnabled = true
        handleRoute()
    }

    /**
     * Call this to stop showing presentations, even if there is an associated
     * Display for displaying them
     */
    fun disable() {
        isEnabled = false
        if (current != null) {
            listener!!.clearPreso(true)
            current = null
        }
    }

    /**
     * @return whether or not this helper is enabled, based on enable() and
     * disable() calls
     */
    fun isEnabled(): Boolean {
        return isEnabled
    }

    private fun handleRoute() {
        if (isEnabled()) {
            val displays = mgr!!.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)
            if (displays.size == 0) {
                if (current != null || isFirstRun) {
                    listener!!.clearPreso(true)
                    current = null
                }
            } else {
                val display = displays[0]
                if (display != null && display.isValid) {
                    if (current == null) {
                        listener!!.showPreso(display)
                        current = display
                    } else if (current!!.displayId != display.displayId) {
                        listener!!.clearPreso(true)
                        listener!!.showPreso(display)
                        current = display
                    } else {
                        // no-op: should already be set
                    }
                } else if (current != null) {
                    listener!!.clearPreso(true)
                    current = null
                }
            }
            isFirstRun = false
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun onDisplayAdded(displayId: Int) {
        handleRoute()
    }

    /**
     * {@inheritDoc}
     */
    override fun onDisplayChanged(displayId: Int) {
        handleRoute()
    }

    /**
     * {@inheritDoc}
     */
    override fun onDisplayRemoved(displayId: Int) {
        handleRoute()
    }

    /**
     * Basic constructor.
     *
     * @param ctxt a Context, typically the activity that is planning on showing
     * the Presentation
     * @param listener the callback for show/hide events
     */
    init {
        this.listener = listener
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mgr = ctxt.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        }
    }
}