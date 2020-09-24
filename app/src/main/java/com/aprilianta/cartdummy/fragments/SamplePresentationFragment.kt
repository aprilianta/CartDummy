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
package com.aprilianta.cartdummy.fragments

import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aprilianta.cartdummy.R
import kotlinx.android.synthetic.main.fragment_sample_presentation.*

class SamplePresentationFragment (override var nameProduct: String?,
                                  override var priceProduct: String?)
    : PresentationFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sample_presentation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_nama_produk.text = nameProduct
        tv_harga_produk.text = priceProduct
    }

    companion object {
        fun newInstance(
            ctxt: Context?,
            display: Display?,
            nameProduct: String?,
            priceProduct: String?
        ): SamplePresentationFragment {
            val frag = priceProduct?.let { nameProduct?.let { nameProduct -> SamplePresentationFragment(nameProduct, priceProduct) } }
            frag?.setDisplay(ctxt, display)
            return frag!!
        }
    }
}