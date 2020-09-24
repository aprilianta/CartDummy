/***
 * Copyright (c) 2013-2018 CommonsWare, LLC
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
package com.aprilianta.cartdummy.fragments

import android.app.Dialog
import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import androidx.fragment.app.DialogFragment

/**
 * Fragment that can display its content in a Presentation. Otherwise,
 * it largely behaves like an ordinary DialogFragment.
 */
abstract class PresentationFragment : DialogFragment() {
    abstract var nameProduct: String?
    abstract var priceProduct: String?
    private var display: Display? = null
    private var preso: Presentation? = null

    /**
     * {@inheritDoc}
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return preso ?: super.onCreateDialog(savedInstanceState)
    }

    /**
     * Call this to provide the Display for the Presentation and to actually
     * set up the Presentation. Otherwise, this fragment will behave like
     * an ordinary DialogFragment.
     *
     * @param ctxt a Context associated with this Display
     * @param display the Display on which to show the Presentation
     */
    fun setDisplay(ctxt: Context?, display: Display?) {
        preso = display?.let { Presentation(ctxt, it, theme) }
        this.display = display
    }

    /**
     * @return the Display supplied via setDisplay
     */
    fun getDisplay(): Display? {
        return display
    }

    /**
     * @return the Context associated with the Presentation (via setDisplay())
     * where available
     */
    override fun getContext(): Context? {
        return if (preso != null) {
            preso!!.context
        } else activity
    }
}