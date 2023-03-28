package com.example.android.google_sol.ui
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.android.google_sol.R
import com.example.android.google_sol.util.SellerViewModal
import com.example.android.google_sol.databinding.MainScreenActivityBinding



class MainActivity : AppCompatActivity() {
    private val binding by lazy { MainScreenActivityBinding.inflate(layoutInflater) }
    private val viewModel : SellerViewModal by viewModels()

    companion object{
        const val LOGIN_SCREEN = 6
        const val MAIN_SCREEN = 1
        const val BUYING_SCREEN = 2
        const val DIRECTION_SCREEN = 3
        const val CHECKOUT_SCREEN = 4
        const val ORDER_PLACED = 5
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.setScreenState(LOGIN_SCREEN)
        setupObserver()

    }

    private fun setupObserver() {
        viewModel.screenState.observe(this){
            when(it){
                MAIN_SCREEN -> {
                    mainScreen()
                }
                BUYING_SCREEN -> {
                    buyingScreen()
                }
                DIRECTION_SCREEN -> {
                    getRouteScreen()
                }
                CHECKOUT_SCREEN -> {
                    getCheckoutScreen()
                }
                ORDER_PLACED -> {
                    oderPlacedScreen()
                }
                LOGIN_SCREEN -> {
                    userLoginScreen()
                }
            }
        }
    }




    override fun onBackPressed() {
        if(handledBackPressed())
            return
        super.onBackPressed()
    }

    private fun handledBackPressed() : Boolean {
        when(viewModel.screenState.value){
            BUYING_SCREEN -> {
                viewModel.setScreenState(MAIN_SCREEN)
                return true
            }
        }
        return false
    }


    private fun buyingScreen() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainUserProfile,FragmentBuyingScreen()).commit()
    }

    private fun mainScreen() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainUserProfile,FragmentMainScreen()).commit()
    }

    private fun getCheckoutScreen() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainUserProfile,FragmentCheckoutScreen()).commit()
    }

    private fun getRouteScreen(){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainUserProfile , FragmentGetDirection()).commit()
    }

    private fun oderPlacedScreen() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainUserProfile , FragmentOrderPlaced()).commit()
    }

    private fun userLoginScreen() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainUserProfile , FragmentUserLogin()).commit()
    }



}
