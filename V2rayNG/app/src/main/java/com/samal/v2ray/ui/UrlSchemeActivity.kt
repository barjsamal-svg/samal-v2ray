package com.samal.v2ray.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.samal.v2ray.AppConfig
import com.samal.v2ray.R
import com.samal.v2ray.extension.toast
import com.samal.v2ray.extension.toastError
import com.samal.v2ray.handler.AngConfigManager
import com.samal.v2ray.ui.base.BaseComponentActivity
import com.samal.v2ray.ui.main.MainActivity
import com.samal.v2ray.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLDecoder

class UrlSchemeActivity : BaseComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            intent.apply {
                if (action == Intent.ACTION_SEND) {
                    if ("text/plain" == type) {
                        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                            parseUri(it, null)
                        }
                    }
                } else if (action == Intent.ACTION_VIEW || action == Intent.ACTION_SEND) {
                    val uri: Uri? = if (action == Intent.ACTION_SEND) intent.getParcelableExtra(Intent.EXTRA_STREAM) else intent.data
                    if (uri != null) {
                        val content = readUriContent(uri)
                        if (!content.isNullOrEmpty()) {
                            lifecycleScope.launch(Dispatchers.IO) {
                                val samalRes = com.samal.v2ray.handler.SamalCrypto.decryptConfig(content.trim())
                                if (samalRes.isLocked && !samalRes.success) {
                                    withContext(Dispatchers.Main) {
                                        toastError("❌ هذا الكونفج مقفل تشفيرياً ولا يمكن فكه أو استيراده!")
                                    }
                                } else {
                                    val (count, countSub) = AngConfigManager.importBatchConfig(content.trim(), "", false)
                                    withContext(Dispatchers.Main) {
                                        if (count + countSub > 0) {
                                            toast("✅ تم استيراد كونفج SAMAL بنجاح!")
                                        } else {
                                            toastError("❌ فشل استيراد الكونفج!")
                                        }
                                    }
                                }
                            }
                        }
                    } else if (action == Intent.ACTION_VIEW) {
                        when (data?.host) {
                            "install-config" -> {
                                val shareUrl = data?.getQueryParameter("url").orEmpty()
                                parseUri(shareUrl, data?.fragment)
                            }
                            "install-sub" -> {
                                val shareUrl = data?.getQueryParameter("url").orEmpty()
                                parseUri(shareUrl, data?.fragment)
                            }
                        }
                    }
                }
            }

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } catch (e: Exception) {
            LogUtil.e(AppConfig.TAG, "Error processing URL scheme", e)
        }
    }

    @Composable
    override fun ScreenContent() {
    }

    private fun parseUri(uriString: String?, fragment: String?) {
        if (uriString.isNullOrEmpty()) {
            return
        }
        LogUtil.i(AppConfig.TAG, uriString)

        var decodedUrl = URLDecoder.decode(uriString, "UTF-8")
        val uri = Uri.parse(decodedUrl)
        if (uri != null) {
            if (uri.fragment.isNullOrEmpty() && !fragment.isNullOrEmpty()) {
                decodedUrl += "#${fragment}"
            }
            LogUtil.i(AppConfig.TAG, decodedUrl)
            lifecycleScope.launch(Dispatchers.IO) {
                val (count, countSub) = AngConfigManager.importBatchConfig(decodedUrl, "", false)
                withContext(Dispatchers.Main) {
                    if (count + countSub > 0) {
                        toast(R.string.import_subscription_success)
                    } else {
                        toast(R.string.import_subscription_failure)
                    }
                }
            }
        }
    }

    private fun readUriContent(uri: Uri): String? {
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val reader = java.io.BufferedReader(java.io.InputStreamReader(inputStream))
            val sb = StringBuilder()
            var line: String? = reader.readLine()
            while (line != null) {
                sb.append(line).append('\n')
                line = reader.readLine()
            }
            reader.close()
            inputStream.close()
            return sb.toString()
        } catch (e: Exception) {
            LogUtil.e(AppConfig.TAG, "Failed to read URI content", e)
            return null
        }
    }
}
