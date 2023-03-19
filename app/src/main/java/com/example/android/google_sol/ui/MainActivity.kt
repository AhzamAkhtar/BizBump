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
        const val MAIN_SCREEN = 1
        const val BUYING_SCREEN = 2
        const val DIRECTION_SCREEN = 3
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.setScreenState(DIRECTION_SCREEN)
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

    private fun getRouteScreen(){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainUserProfile , FragmentGetDirection()).commit()
    }

}
