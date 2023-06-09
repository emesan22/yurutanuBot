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
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class BotClient {
    private lateinit var jda: JDA
    private val logger: Logger = LogManager.getLogger(BotClient::class.java)

    fun main(token: String) { //トークンを使ってBotを起動する部分
        jda = JDABuilder.create(
            token,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.MESSAGE_CONTENT,
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_MEMBERS
        )
            .setRawEventsEnabled(true)
            .addEventListeners(BotListener())
            .setActivity(Activity.playing("お前らチャンネル登録しろ"))
            .build()

        jda.awaitReady()

        val guild = jda.getGuildById("1099656854784712805")!!

        // 登録するコマンドを作成

        /* 説明
        * /キーワード - コマンド名
        * <> - 必修なオプション
        * [] - オプション
        */

        //一般コマンド
        // /help - 説明
        val thisHelpCommand = Commands.slash("help", "このBOTの説明を表示します。")

        // /say <content> - BOTにオプションで書いたものを言わせる。
        val sayCommand = Commands.slash("say", "打った文字をbotに言わせます。")
            .addOption(OptionType.STRING, "content", "言わせる文字を設定", true)

        // /roll <d0> - d0で書いた数字を最大値としてダイスを振る
        val rollCommand = Commands.slash("roll", "ダイスを振ります。")

        // /ranking [limit] - ランキングを表示します。
        val rankingCommand = Commands.slash("ranking", "ランキングを表示します。")
            .addOptions(OptionData(OptionType.INTEGER, "limit", "表示する最大の数を出力します。"))

        //モデレーターコマンド
        // /announce <content> <to> - 通常のチャットでアナウンスをします。 <content>は内容 <to>はメンションをするロールを選びます。
        val announceCommand = Commands.slash("announce", "アナウンスをします。")
            .addOption(OptionType.STRING, "content", "アナウンスをする内容", true)
            .addOption(OptionType.ROLE, "to", "メンションするロール上に付きます", true)
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))

        // /announce-embed <title> <description> <to> [image] - embedでアナウンスをします。
        // <title>はタイトル <description>で内容を <to>はメンションをするロールを選びます。 [image]は画像を出します。
        val embedAnnounceCommand = Commands.slash("announce-embed", "Embedでアナウンスします。")
            .addOptions(
                OptionData(OptionType.STRING, "title", "タイトル", true),
                OptionData(OptionType.STRING, "description", "内容 改行を行う場合\\nと入力してください", true),
                OptionData(OptionType.ROLE, "to", "メンションするロール上に付きます", true),
                OptionData(OptionType.STRING, "image", "画像を挿入します。URLで入力してください", false)
            )
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))

        //実装予定
        val kickCommand = Commands.slash("kick", "メンバーをキックします")
            .addOptions(
                OptionData(OptionType.USER, "user", "kickするメンバー", true),
                OptionData(OptionType.STRING, "reason", "キックする理由(DMに送られます。)", true)
            )
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS))

        //実装予定
        val banCommand = Commands.slash("ban", "メンバーを期限付きでBANします。")
            .addOptions(
                OptionData(OptionType.USER, "user", "BANをするメンバー", true),
                OptionData(OptionType.STRING, "reason", "BANする理由(DMに送られます。)", true)
            )
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS))
        logger.info("すべてのコマンドの定義を終わりました。")

        guild.updateCommands().queue() //コマンドを一時リセット
        logger.info("コマンドをリセットしました。")

        // コマンドを登録
        guild.updateCommands()
            .addCommands(
                thisHelpCommand,
                sayCommand,
                rankingCommand,
                announceCommand,
                embedAnnounceCommand,
                kickCommand,
                banCommand,
                rollCommand
            ).queue()
        logger.info("コマンドをセットしました。")
    }
}

fun main() {
    val token = System.getenv("botToken")
    BotClient().main(token)
}
