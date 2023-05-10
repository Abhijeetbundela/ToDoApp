package com.bundela.todoapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.bundela.todoapp.R
import com.bundela.todoapp.databinding.ActivityMainBinding
import com.bundela.todoapp.viewModel.SplashViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
                ?: return
        val graph = navHostFragment.navController.navInflater.inflate(R.navigation.nav_graph)
        val navController = navHostFragment.navController

        navController.graph = graph
        with(navController) {
            appBarConfiguration = AppBarConfiguration(graph)
            setupActionBarWithNavController(this, appBarConfiguration)
        }

    }

//    override fun onSupportNavigateUp(): Boolean {
//        navHostFragment.navController.navigateUp()
//        return super.onSupportNavigateUp()
//    }

}