package com.samal.v2ray.handler

import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.Executors

object UdpChecker {
    private val executor = Executors.newCachedThreadPool()

    interface UdpCheckCallback {
        fun onResult(isGamingSupported: Boolean, details: String)
    }

    fun checkUdpIntegrity(serverHost: String, callback: UdpCheckCallback) {
        executor.execute {
            val targets = listOf(
                Triple("Google STUN", "stun.l.google.com", 19302),
                Triple("Cloudflare NTP", "time.cloudflare.com", 123)
            )

            var passed = 0
            val detailsBuilder = StringBuilder()

            for ((name, host, port) in targets) {
                try {
                    val addr = InetAddress.getByName(host)
                    val socket = DatagramSocket().apply {
                        soTimeout = 3000
                    }
                    val payload = if (port == 19302) {
                        byteArrayOf(0x00, 0x01, 0x00, 0x00, 0x21, 0x12, 0xa4, 0x42, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00).map { it.toByte() }.toByteArray()
                    } else {
                        ByteArray(48).apply { this[0] = 0x1b.toByte() }
                    }

                    val packet = DatagramPacket(payload, payload.size, addr, port)
                    socket.send(packet)

                    val receiveBuffer = ByteArray(1024)
                    val receivePacket = DatagramPacket(receiveBuffer, receiveBuffer.size)
                    socket.receive(receivePacket)
                    socket.close()

                    detailsBuilder.append("[$name]: مدعوم ✅\n")
                    passed++
                } catch (e: Exception) {
                    detailsBuilder.append("[$name]: غير مدعوم ❌\n")
                }
            }

            val isGaming = (passed > 0)
            val resultText = if (isGaming) "UDP ✅ (شغّال ألعاب)" else "UDP ❌ (غير مدعوم للألعاب)"
            callback.onResult(isGaming, resultText)
        }
    }
}
