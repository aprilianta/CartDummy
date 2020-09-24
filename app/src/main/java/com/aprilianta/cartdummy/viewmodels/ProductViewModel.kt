package com.aprilianta.cartdummy.viewmodels

import android.view.Display
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aprilianta.cartdummy.R
import com.aprilianta.cartdummy.fragments.PresentationFragment
import com.aprilianta.cartdummy.fragments.SamplePresentationFragment
import com.aprilianta.cartdummy.helper.PresentationHelper
import com.aprilianta.cartdummy.models.Product
import com.aprilianta.cartdummy.utils.SunmiPrintHelper
import kotlinx.coroutines.withContext

class ProductViewModel : ViewModel() {
    private var products = MutableLiveData<List<Product>>()
    private var selectedProducts = MutableLiveData<List<Product>>()
    private var nameProduct = MutableLiveData<String>()
    private var priceProduct = MutableLiveData<String>()

    init {
        products.postValue(mutableListOf())
        selectedProducts.postValue(mutableListOf())
    }

    fun fetchDummyProduct(){
        //static data / from API
        val dummies = mutableListOf<Product>().apply {
            add(
                Product(
                    1,
                    "Beng beng",
                    3000,
                    R.drawable.beng
                )
            )
            add(
                Product(
                    2,
                    "Indomie",
                    3500,
                    R.drawable.indomie
                )
            )
            add(
                Product(
                    3,
                    "Garam",
                    2000,
                    R.drawable.garam
                )
            )
            add(
                Product(
                    4,
                    "Sprite",
                    6000,
                    R.drawable.sprite
                )
            )
            add(
                Product(
                    5,
                    "Kopi",
                    6000,
                    R.drawable.kopi
                )
            )
        }
        products.postValue(dummies)

    }

    fun addSelectedProduct(product: Product){
        val tempSelectedProducts : MutableList<Product> = if (selectedProducts.value == null){
            mutableListOf()
        }else{
            selectedProducts.value as MutableList<Product>
        }
        val sameProduct : Product? = tempSelectedProducts.find { p ->
            p.id == product.id
        }
        sameProduct?.let {
            it.selectedQuantity = it.selectedQuantity?.plus(1)
            nameProduct.postValue(it.selectedQuantity.toString() + " x " + it.name)
            priceProduct.postValue("Rp "+(it.selectedQuantity!! * it.price!!).toString())
            SunmiPrintHelper.getInstance().sendTextsToLcd(it.selectedQuantity.toString() + " x " + it.name, "", "Rp "+(it.selectedQuantity!! * it.price!!).toString())
        } ?: kotlin.run {
            tempSelectedProducts.add(product)
            nameProduct.postValue("1 x " + product.name)
            priceProduct.postValue("Rp "+ product.price!!).toString()
            SunmiPrintHelper.getInstance().sendTextsToLcd("1 x "+product.name, "", "Rp "+product.price.toString())
        }


        selectedProducts.postValue(tempSelectedProducts)
    }

    fun decrementQuantityProduct(product: Product){
        val tempSelectedProducts : MutableList<Product> = if (selectedProducts.value == null){
            mutableListOf()
        }else{
            selectedProducts.value as MutableList<Product>
        }
        val sameProduct : Product? = tempSelectedProducts.find { p ->
            p.id == product.id
        }
        sameProduct?.let {
            if(it.selectedQuantity?.minus(1) == 0){
                SunmiPrintHelper.getInstance().sendTextsToLcd(it.name + " deleted", "", "")
                nameProduct.postValue(it.name + " deleted")
                priceProduct.postValue("")
                tempSelectedProducts.remove(it)
            }else{
                it.selectedQuantity = it.selectedQuantity!!.minus(1)
                nameProduct.postValue(it.selectedQuantity.toString() + " x " + it.name)
                priceProduct.postValue("Rp "+(it.selectedQuantity!! * it.price!!).toString())
                SunmiPrintHelper.getInstance().sendTextsToLcd(it.selectedQuantity.toString() + " x " + it.name, "", "Rp "+(it.selectedQuantity!! * it.price!!).toString())
            }
        }
        selectedProducts.postValue(tempSelectedProducts)
    }

    fun incrementQuantityProduct(product: Product){
        val tempSelectedProducts : MutableList<Product> = if (selectedProducts.value == null){
            mutableListOf()
        }else{
            selectedProducts.value as MutableList<Product>
        }
        val sameProduct : Product? = tempSelectedProducts.find { p ->
            p.id == product.id
        }
        sameProduct?.let {
            it.selectedQuantity = it.selectedQuantity!!.plus(1)
            nameProduct.postValue(it.selectedQuantity.toString() + " x " + it.name)
            priceProduct.postValue("Rp "+(it.selectedQuantity!! * it.price!!).toString())
            SunmiPrintHelper.getInstance().sendTextsToLcd(it.selectedQuantity.toString() + " x " + it.name, "", "Rp "+(it.selectedQuantity!! * it.price!!).toString())
        }
        selectedProducts.postValue(tempSelectedProducts)
    }

    fun deleteSelectedProduct(product: Product){
        val tempSelectedProducts : MutableList<Product> = if (selectedProducts.value == null){
            mutableListOf()
        }else{
            selectedProducts.value as MutableList<Product>
        }
        val sameProduct : Product? = tempSelectedProducts.find { p ->
            p.id == product.id
        }
        sameProduct?.let {
            SunmiPrintHelper.getInstance().sendTextsToLcd(it.name + " deleted", "", "")
            nameProduct.postValue(it.name + " deleted")
            priceProduct.postValue("")
            tempSelectedProducts.remove(it)
        }
        selectedProducts.postValue(tempSelectedProducts)
    }

    fun listenToProducts() = products
    fun listenToSelectedProduct() = selectedProducts

    fun listenNameProduct() = nameProduct
    fun listenPriceProduct() = priceProduct
}