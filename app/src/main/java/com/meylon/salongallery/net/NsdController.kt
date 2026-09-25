package com.meylon.salongallery.net

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiManager
import android.util.Log
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

/** A Display device found on the local network. */
data class DiscoveredScreen(
    val key: String,     // NSD service name (unique-ish)
    val name: String,    // friendly name
    val host: String,    // resolved IP
    val port: Int,
)

/**
 * Wraps Android NSD (mDNS/DNS-SD). The Display device [register]s a service;
 * the Remote [startDiscovery] to find and resolve those services.
 */
class NsdController(context: Context) {

    private val appContext = context.applicationContext
    private val nsd = appContext.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val wifi = appContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private var multicastLock: WifiManager.MulticastLock? = null
    private var regListener: NsdManager.RegistrationListener? = null
    private var discListener: NsdManager.DiscoveryListener? = null

    // Resolve only one service at a time (NsdManager rejects concurrent resolves).
    private val resolveQueue = ConcurrentLinkedQueue<NsdServiceInfo>()
    private val resolving = AtomicBoolean(false)

    // ---- Display side ----

    fun register(serviceName: String, port: Int) {
        val info = NsdServiceInfo().apply {
            this.serviceName = serviceName
            this.serviceType = SERVICE_TYPE
            this.port = port
        }
        regListener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(info: NsdServiceInfo) {
                Log.i(TAG, "registered: ${info.serviceName}")
            }
            override fun onRegistrationFailed(info: NsdServiceInfo, err: Int) {
                Log.e(TAG, "register failed: $err")
            }
            override fun onServiceUnregistered(info: NsdServiceInfo) {}
            override fun onUnregistrationFailed(info: NsdServiceInfo, err: Int) {}
        }
        runCatching { nsd.registerService(info, NsdManager.PROTOCOL_DNS_SD, regListener) }
    }

    fun unregister() {
        regListener?.let { runCatching { nsd.unregisterService(it) } }
        regListener = null
    }

    // ---- Remote side ----

    fun startDiscovery(
        onFound: (DiscoveredScreen) -> Unit,
        onLost: (String) -> Unit,
    ) {
        multicastLock = wifi.createMulticastLock("salon-nsd").apply {
            setReferenceCounted(true)
            runCatching { acquire() }
        }
        discListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(type: String) {}
            override fun onDiscoveryStopped(type: String) {}
            override fun onStartDiscoveryFailed(type: String, err: Int) {
                runCatching { nsd.stopServiceDiscovery(this) }
            }
            override fun onStopDiscoveryFailed(type: String, err: Int) {}
            override fun onServiceFound(info: NsdServiceInfo) {
                if (info.serviceType.trimEnd('.') == SERVICE_TYPE.trimEnd('.')) {
                    resolveQueue.add(info)
                    pump(onFound)
                }
            }
            override fun onServiceLost(info: NsdServiceInfo) {
                onLost(info.serviceName)
            }
        }
        runCatching {
            nsd.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discListener)
        }
    }

    private fun pump(onFound: (DiscoveredScreen) -> Unit) {
        if (!resolving.compareAndSet(false, true)) return
        val next = resolveQueue.poll()
        if (next == null) {
            resolving.set(false)
            return
        }
        val listener = object : NsdManager.ResolveListener {
            override fun onResolveFailed(info: NsdServiceInfo, err: Int) {
                resolving.set(false)
                pump(onFound)
            }
            override fun onServiceResolved(info: NsdServiceInfo) {
                val host = info.host?.hostAddress
                if (host != null) {
                    onFound(DiscoveredScreen(info.serviceName, info.serviceName, host, info.port))
                }
                resolving.set(false)
                pump(onFound)
            }
        }
        runCatching { nsd.resolveService(next, listener) }.onFailure {
            resolving.set(false)
            pump(onFound)
        }
    }

    fun stopDiscovery() {
        discListener?.let { runCatching { nsd.stopServiceDiscovery(it) } }
        discListener = null
        multicastLock?.let { runCatching { if (it.isHeld) it.release() } }
        multicastLock = null
        resolveQueue.clear()
        resolving.set(false)
    }

    companion object {
        const val SERVICE_TYPE = "_salongallery._tcp."
        private const val TAG = "NsdController"
    }
}
