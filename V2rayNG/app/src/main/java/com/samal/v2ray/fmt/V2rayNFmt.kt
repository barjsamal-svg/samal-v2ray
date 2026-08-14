package com.samal.v2ray.fmt

import com.samal.v2ray.AppConfig
import com.samal.v2ray.dto.V2rayNShareItem
import com.samal.v2ray.dto.entities.ProfileItem
import com.samal.v2ray.util.JsonUtil
import com.samal.v2ray.util.LogUtil
import com.samal.v2ray.util.Utils

object V2rayNFmt : FmtBase() {
    fun parse(str: String): ProfileItem? {
        try {
            val jsonBase64Payload = str.substringAfterLast('/')
            val jsonPayload = Utils.decode(jsonBase64Payload)
            val v2rayNShareItem = JsonUtil.fromJson(jsonPayload, V2rayNShareItem::class.java)
            return v2rayNShareItem?.toProfileItem()
        } catch (e: Exception) {
            LogUtil.e(AppConfig.TAG, "Failed to parse V2rayN format", e)
        }
        return null
    }
}