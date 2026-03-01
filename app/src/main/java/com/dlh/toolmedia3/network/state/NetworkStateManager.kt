package com.dlh.toolmedia3.network.state

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class NetworkStateManager private constructor(private val context: Context) {

    companion object {
        private var instance: NetworkStateManager? = null

        fun getInstance(context: Context): NetworkStateManager {
            if (instance == null) {
                synchronized(NetworkStateManager::class.java) {
                    if (instance == null) {
                        instance = NetworkStateManager(context.applicationContext)
                    }
                }
            }
            return instance!!
        }
    }

    private val connectivityManager: ConnectivityManager = 
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState> = _networkState

    init {
        initializeNetworkCallback()
        // 初始化时检查当前网络状态
        _networkState.value = getCurrentNetworkState()
    }

    private fun initializeNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                _networkState.postValue(getCurrentNetworkState())
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                _networkState.postValue(getCurrentNetworkState())
            }

            override fun onUnavailable() {
                super.onUnavailable()
                _networkState.postValue(getCurrentNetworkState())
            }
        })
    }

    private fun getCurrentNetworkState(): NetworkState {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            return if (networkCapabilities != null) {
                when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> 
                        NetworkState.Connected(NetworkType.WIFI)
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> 
                        NetworkState.Connected(NetworkType.CELLULAR)
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> 
                        NetworkState.Connected(NetworkType.ETHERNET)
                    else -> NetworkState.Connected(NetworkType.OTHER)
                }
            } else {
                NetworkState.Disconnected
            }
        } else {
            // 旧版本 Android 的网络状态检查
            val networkInfo = connectivityManager.activeNetworkInfo
            return if (networkInfo != null && networkInfo.isConnected) {
                when (networkInfo.type) {
                    ConnectivityManager.TYPE_WIFI -> 
                        NetworkState.Connected(NetworkType.WIFI)
                    ConnectivityManager.TYPE_MOBILE -> 
                        NetworkState.Connected(NetworkType.CELLULAR)
                    ConnectivityManager.TYPE_ETHERNET -> 
                        NetworkState.Connected(NetworkType.ETHERNET)
                    else -> NetworkState.Connected(NetworkType.OTHER)
                }
            } else {
                NetworkState.Disconnected
            }
        }
    }

    /**
     * 检查网络是否可用
     * @return 网络是否可用
     */
    fun isNetworkAvailable(): Boolean {
        return when (val state = _networkState.value) {
            is NetworkState.Connected -> true
            else -> false
        }
    }

    /**
     * 获取当前网络类型
     * @return 当前网络类型
     */
    fun getCurrentNetworkType(): NetworkType {
        return when (val state = _networkState.value) {
            is NetworkState.Connected -> state.type
            else -> NetworkType.NONE
        }
    }
}

/**
 * 网络状态密封类
 */
sealed class NetworkState {
    data class Connected(val type: NetworkType) : NetworkState()
    object Disconnected : NetworkState()
}

/**
 * 网络类型枚举
 */
enum class NetworkType {
    WIFI,
    CELLULAR,
    ETHERNET,
    OTHER,
    NONE
}
