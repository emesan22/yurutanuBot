package click.emesan.bot.yurutanuBot

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.requests.GatewayIntent

class BotClient {
    private lateinit var jda: JDA

    companion object {
        private const val GUILD_ID = "1099656854784712805"
    }

    fun main(token: String) { //トークンを使ってBotを起動する部分
        BotListener().yFrame()

        jda = JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
            .setRawEventsEnabled(true)
            .addEventListeners(BotListener())
            .setActivity(Activity.playing("お前らチャンネル登録しろ"))
            .build()

        jda.awaitReady()

        val guild = jda.getGuildById(GUILD_ID)!!

        /*
        * TODO:
        *   - make kick command
        *   - make ban command
        *   - make GUI Panel
        */

        // 登録するコマンドを作成
        //一般コマンド
        val thisHelpCommand = Commands.slash("help", "このBOTの説明を表示します。")
        val thisAuthorCommand = Commands.slash("author", "作者を表示します。")
        val sayCommand = Commands.slash("say", "打った文字をbotに言わせます。")
            .addOption(OptionType.STRING, "content", "言わせる文字を設定", true)
        val rollCommand = Commands.slash("roll", "ダイスを振ります。")
            .addOption(OptionType.INTEGER, "d0", "最大値", true)

        //モデレーターコマンド
        val announceCommand = Commands.slash("announce", "アナウンスをします。")
            .addOption(OptionType.STRING, "content", "アナウンスをする内容", true)
            .addOption(OptionType.ROLE, "to", "メンションするロール上に付きます", true)
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))
        val embedAnnounceCommand = Commands.slash("announce-embed", "Embedでアナウンスします。")
            .addOptions(
                OptionData(OptionType.STRING, "title", "タイトル", true),
                OptionData(OptionType.STRING, "description", "内容 改行を行う場合\\nと入力してください", true),
                OptionData(OptionType.ROLE, "to", "メンションするロール上に付きます", true),
                OptionData(OptionType.STRING, "image", "画像を挿入します。URLで入力してください", false)
            )
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))
        val kickCommand = Commands.slash("kick", "メンバーをキックします")
            .addOptions(
                OptionData(OptionType.USER, "user", "kickするメンバー", true),
                OptionData(OptionType.STRING, "reason", "キックする理由(DMに送られます。)", true)
            )
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS))
        val banCommand = Commands.slash("ban", "メンバーを期限付きでBANします。")
            .addOptions(
                OptionData(OptionType.USER, "user", "BANをするメンバー", true),
                OptionData(OptionType.STRING, "reason", "BANする理由(DMに送られます。)", true)
            )
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS))

        guild.updateCommands().queue() //コマンドを一時リセット

        // コマンドを登録
        guild.updateCommands()
            .addCommands(
                thisHelpCommand,
                thisAuthorCommand,
                sayCommand,
                announceCommand,
                embedAnnounceCommand,
                kickCommand,
                banCommand,
                rollCommand
            )
            .queue()
    }
}

fun main() {
    val bot = BotClient()
    val token = "BOT_TOKEN"
    bot.main(token)
}