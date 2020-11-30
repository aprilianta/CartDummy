package com.aprilianta.cartdummy.activity

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import com.aprilianta.cartdummy.R
import kotlinx.android.synthetic.main.fragment_sample_presentation.*

class DemoPresentation(outerContext: Context?, display: Display?) : Presentation(outerContext, display) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_sample_presentation)
    }

    fun setTextNameProduct(nameProduct: String?) {
        tv_nama_produk.text = nameProduct
    }

    fun setTextPriceProduct(priceProduct: String?) {
        tv_harga_produk.text = priceProduct
    }

    override fun onStop() {
        super.onStop()
    }
}