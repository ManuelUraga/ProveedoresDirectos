package com.femco.oxxo.reciboentiendaproveedores.presentation

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.femco.oxxo.reciboentiendaproveedores.R
import com.femco.oxxo.reciboentiendaproveedores.databinding.ActivityMainBinding
import com.femco.oxxo.reciboentiendaproveedores.utils.MyAlertDialog
import com.femco.oxxo.reciboentiendaproveedores.utils.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController)

        if (PreferencesManager.instance?.firstTime == false) {
            PreferencesManager.instance?.firstTime = true
            navController.navigate(R.id.action_load_catalog_fragment)
        }

        onBackPressedDispatcher.addCallback(this, onBackPressed(navHostFragment, navController))
    }

    private fun onBackPressed(navHostFragment: NavHostFragment, navController: NavController) =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                try {
                    if (navHostFragment.childFragmentManager.backStackEntryCount == 0) {
                        MyAlertDialog(this@MainActivity)
                            .showAlert(
                                message = R.string.main_dialog_exit_message,
                                positiveMessage = R.string.main_dialog_exit_positive,
                                negativeMessage = R.string.main_dialog_exit_negative
                            ) {
                                finishAffinity()
                            }
                    } else {
                        navController.popBackStack()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


    override fun onSupportNavigateUp(): Boolean {
        return navController.popBackStack() || super.onSupportNavigateUp()
    }

}