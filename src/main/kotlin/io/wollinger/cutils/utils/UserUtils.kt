package io.wollinger.cutils.utils

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User

object UserUtils {
    fun getServerAvatarURL(member: Member): String? {
        val url = member.avatarUrl
        return if(url != null) "<$url?size=1024>" else null
    }

    fun getAvatarURL(user: User) = "<${user.effectiveAvatarUrl}?size=1024>"
}