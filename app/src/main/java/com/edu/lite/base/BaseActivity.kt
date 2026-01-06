package com.edu.lite.base

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.edu.lite.App
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.ThemeHelper
import com.edu.lite.base.connectivity.ConnectivityProvider
import com.edu.lite.base.local.SharedPrefManager
import com.edu.lite.base.network.ErrorCodes
import com.edu.lite.base.network.NetworkError
import com.edu.lite.ui.auth.WelcomeActivity
import com.edu.lite.ui.dash_board.profile.download.ProfileDownloadFragment
import com.edu.lite.utils.AlertManager
import com.edu.lite.utils.Resource
import com.edu.lite.utils.event.NoInternetSheet
import com.edu.lite.utils.hideKeyboard
import java.util.Locale
import javax.inject.Inject

abstract class BaseActivity<Binding : ViewDataBinding> : AppCompatActivity(),
    ConnectivityProvider.ConnectivityStateListener {

    lateinit var progressDialogAvl: ProgressDialogAvl
    open val onRetry: (() -> Unit)? = null

    lateinit var binding: Binding
    val app: App
        get() = application as App

    private lateinit var connectivityProvider: ConnectivityProvider
    private var noInternetSheet: NoInternetSheet? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ThemeHelper.getThemeResId(this))
        super.onCreate(savedInstanceState)
        val layout: Int = getLayoutResource()
        binding = DataBindingUtil.setContentView(this, layout)
        binding.setVariable(BR.vm, getViewModel())
        connectivityProvider = ConnectivityProvider.createProvider(this)
        connectivityProvider.addListener(this)
        progressDialogAvl = ProgressDialogAvl(this)
        setStatusBarColor(R.color.white)
        setStatusBarDarkText()
        onCreateView()

        val deviceLanguage = Locale.getDefault().language
        val savedLanguage = sharedPrefManager.getLanguage()
        if (savedLanguage != deviceLanguage && savedLanguage != "") {
            setLocale(savedLanguage)
        }
        val vm = getViewModel()
        binding.setVariable(BR.vm, vm)
        vm.onUnAuth.observe(this) {
            showUnauthorised()
        }
    }


    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun showUnauthorised() {
        sharedPrefManager.clearAllExceptLanguage()
        // startActivity(LoginActivity.newIntent(this))
        // finishAffinity()
    }

    private fun setStatusBarColor(colorResId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, colorResId)
        }
    }

    private fun setStatusBarDarkText() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    protected abstract fun getLayoutResource(): Int
    protected abstract fun getViewModel(): BaseViewModel
    protected abstract fun onCreateView()

    fun showToast(msg: String? = "Something went wrong !!") {
        Toast.makeText(this, msg ?: "Showed null value !!", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }


    fun hideLoading() {
        progressDialogAvl.isLoading(false)
    }

    fun showLoading() {
        progressDialogAvl.isLoading(true)
    }

    fun onError(error: Throwable, showErrorView: Boolean) {
        if (error is NetworkError) {

            when (error.errorCode) {
                ErrorCodes.SESSION_EXPIRED -> {
                    showToast(getString(R.string.session_expired))
                    app.onLogout()
                }

                else -> AlertManager.showNegativeAlert(
                    this, error.message, getString(R.string.alert)
                )
            }
        } else {
            AlertManager.showNegativeAlert(
                this, getString(R.string.please_try_again), getString(R.string.alert)
            )
        }
    }

    override fun onDestroy() {
        connectivityProvider.removeListener(this)
        super.onDestroy()
    }

    override fun onStateChange(state: ConnectivityProvider.NetworkState) {
        if (noInternetSheet == null) {
            noInternetSheet = NoInternetSheet()
            noInternetSheet?.isCancelable = false
        }
        if (state.hasInternet()) {
            ProfileDownloadFragment.data = 1
            if (noInternetSheet?.isVisible == true) noInternetSheet?.dismiss()

        } else {
            if (noInternetSheet?.isVisible == false) {
                ProfileDownloadFragment.data = 2
                WelcomeActivity.observeActivity.postValue(
                    Resource.success(
                        "checkInternet",
                        true
                    )
                )
            }
            // noInternetSheet?.show(supportFragmentManager, noInternetSheet?.tag)
        }
    }

    private fun ConnectivityProvider.NetworkState.hasInternet(): Boolean {
        return (this as? ConnectivityProvider.NetworkState.ConnectedState)?.hasInternet == true
    }
}