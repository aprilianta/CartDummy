package com.aprilianta.cartdummy.activity

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.aprilianta.cartdummy.R
import com.aprilianta.cartdummy.adapters.ProductAdapter
import com.aprilianta.cartdummy.adapters.SelectedProductAdapter
import com.aprilianta.cartdummy.fragments.PresentationFragment
import com.aprilianta.cartdummy.fragments.SamplePresentationFragment
import com.aprilianta.cartdummy.helper.PresentationHelper
import com.aprilianta.cartdummy.utils.BluetoothUtil
import com.aprilianta.cartdummy.utils.ESCUtil
import com.aprilianta.cartdummy.utils.SunmiPrintHelper
import com.aprilianta.cartdummy.viewmodels.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottomsheet_cart.*
import kotlinx.android.synthetic.main.fragment_sample_presentation.*

class MainActivity : AppCompatActivity(), PresentationHelper.Listener {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var bottomSheet: BottomSheetBehavior<*>
    var preso: PresentationFragment? = null
    var helper: PresentationHelper? = null
    var inline: View? = null
    var nameProduct: String? = ""
    var priceProduct: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUI()
        initPrinter()
        inline = findViewById(R.id.preso)
        helper = PresentationHelper(this, this)
    }

    private fun setupUI() {
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        rv_product.apply {
            layoutManager =
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    GridLayoutManager(this@MainActivity, 2)
                } else {
                    GridLayoutManager(this@MainActivity, 6)
                }
            adapter = ProductAdapter(mutableListOf(), this@MainActivity, productViewModel)
        }
        rv_selected_product.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = SelectedProductAdapter(mutableListOf(), this@MainActivity, productViewModel)
        }
        bottomSheet = BottomSheetBehavior.from(bottomsheet_detail_order)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        btn_detail.setOnClickListener {
            if (bottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED || bottomSheet.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
        btn_checkout.setOnClickListener {
            Toast.makeText(this@MainActivity, "Show total on LCD", Toast.LENGTH_LONG).show()
            SunmiPrintHelper.getInstance().sendTextsToLcd("Total", "", total_price.text.toString())
        }
        productViewModel.fetchDummyProduct()
        productViewModel.listenToProducts().observe(this, Observer {
            rv_product.adapter?.let { a ->
                if (a is ProductAdapter) {
                    a.updateList(it)
                }
            }
        })
        productViewModel.listenToSelectedProduct().observe(this, Observer {
            rv_selected_product.adapter?.let { a ->
                if (a is SelectedProductAdapter) {
                    a.updateList(it)
                }
            }
            val totalPrice = if (it.isEmpty()) {
                0
            } else {
                it.sumBy { p ->
                    p.price!! * p.selectedQuantity!!
                }
            }
            total_price.text = "Rp " + totalPrice.toString()
        })

        productViewModel.listenNameProduct().observe(this, Observer {
            nameProduct = it
            Log.d("logapril namaproduk", nameProduct)
        })

        productViewModel.listenPriceProduct().observe(this, Observer {
            priceProduct = it
            Log.d("logapril priceproduk", priceProduct)
        })
    }

    private fun initPrinter() {
        if (BluetoothUtil.isBlueToothPrinter) {
            BluetoothUtil.sendData(ESCUtil.init_printer())
        } else {
            SunmiPrintHelper.getInstance().initPrinter()
        }
    }

    override fun showPreso(display: Display?) {
        if (inline!!.visibility == View.VISIBLE) {
            inline!!.visibility = View.GONE
            val f = supportFragmentManager.findFragmentById(R.id.preso)
            f?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
        }
        preso = display?.let { buildPreso(it) }
        preso!!.show(supportFragmentManager, "preso")
    }

    override fun clearPreso(switchToInline: Boolean) {
        if (switchToInline) {
            inline!!.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction()
                .add(R.id.preso, buildPreso(null)!!).commit()
        }
        if (preso != null) {
            preso!!.dismiss()
            preso = null
        }
    }

    fun buildPreso(display: Display?): PresentationFragment? {
        if (nameProduct.isNullOrEmpty() && priceProduct.isNullOrEmpty()) {
            return SamplePresentationFragment.newInstance(
                this, display,
                "Welcome to", "April Store"
            )
        } else {
            return SamplePresentationFragment.newInstance(
                this, display,
                nameProduct, priceProduct
            )
        }
    }

    override fun onResume() {
        super.onResume()
        helper?.onResume()
    }

    override fun onPause() {
        helper!!.onPause()
        super.onPause()
    }
}