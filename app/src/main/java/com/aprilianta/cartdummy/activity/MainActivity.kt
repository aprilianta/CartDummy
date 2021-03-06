package com.aprilianta.cartdummy.activity

import android.content.DialogInterface
import android.content.res.Configuration
import android.hardware.display.DisplayManager
import android.hardware.display.DisplayManager.DisplayListener
import android.media.MediaRouter
import android.os.Bundle
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


class MainActivity : AppCompatActivity(), PresentationHelper.Listener {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var bottomSheet: BottomSheetBehavior<*>
    //var preso: PresentationFragment? = null
    //var helper: PresentationHelper? = null
    //var inline: View? = null
    private var mDisplayManager: DisplayManager? = null
    private var displays: Array<Display> = emptyArray()
    private var presentation: DemoPresentation? = null
    var nameProduct: String? = ""
    var priceProduct: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUI()
        initPrinter()
        //inline = findViewById(R.id.preso)
        //helper = PresentationHelper(this, this)
        try {
            mDisplayManager = getSystemService(DISPLAY_SERVICE) as DisplayManager
            displays = mDisplayManager!!.getDisplays(null)
        } catch (e: Exception){
            e.printStackTrace()
        }
        try {
            presentation = DemoPresentation(this, displays[1])
            presentation!!.show()
            presentation!!.setTextNameProduct("Welcome to")
            presentation!!.setTextPriceProduct("April Store")
        } catch (e: Exception){
            e.printStackTrace()
        }
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
            //preso?.setTextNameProduct("Total price :")
            //preso?.setTextPriceProduct(total_price.text.toString())
            try {
                presentation?.setTextNameProduct("Total price :")
                presentation?.setTextPriceProduct(total_price.text.toString())
            } catch (e: Exception){
                e.printStackTrace()
            }

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
            total_price.text = "Rp $totalPrice"
            if (totalPrice == 0) {
                //preso?.setTextNameProduct("Welcome to")
                //preso?.setTextPriceProduct("April Store")
                try {
                    presentation!!.setTextNameProduct("Welcome to")
                    presentation!!.setTextPriceProduct("April Store")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        productViewModel.listenNameProduct().observe(this, Observer {
            nameProduct = it
            //preso?.setTextNameProduct(nameProduct)
            try {
                presentation?.setTextNameProduct(nameProduct)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        productViewModel.listenPriceProduct().observe(this, Observer {
            priceProduct = it
            //preso?.setTextPriceProduct(priceProduct)
            try {
                presentation?.setTextPriceProduct(priceProduct)
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
        /*
        if (inline!!.visibility == View.VISIBLE) {
            inline!!.visibility = View.GONE
            val f = supportFragmentManager.findFragmentById(R.id.preso)
            f?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
        }
        preso = display?.let { buildPreso(it) }
        preso!!.show(supportFragmentManager, "preso")
         */
    }

    override fun clearPreso(switchToInline: Boolean) {
        /*
        if (switchToInline) {
            inline!!.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction()
                .add(R.id.preso, buildPreso(null)!!).commit()
        }
        if (preso != null) {
            preso!!.dismiss()
            preso = null
        }
         */
    }

    private fun buildPreso(display: Display?): PresentationFragment? {
            return SamplePresentationFragment.newInstance(
                this, display,
                "Welcome to", "April Store"
            )
    }

    override fun onResume() {
        super.onResume()
        //helper?.onResume()
    }

    override fun onPause() {
        super.onPause()
        //helper!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        presentation?.dismiss()
    }
}