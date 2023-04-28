package click.emesan.bot.yurutanuBot

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.requests.GatewayIntent

class BotClient : ListenerAdapter() {
    private lateinit var jda: JDA

    fun main(token: String) { //トークンを使ってBotを起動する部分
        jda = JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
            .setRawEventsEnabled(true)
            .addEventListeners(this)
            .setActivity(Activity.playing("say ぬ"))
            .build()

        jda.awaitReady()

        // 登録するコマンドを作成
        val thisHelpCommand = Commands.slash("help","このBOTの説明を表示します。")
        val thisAuthorCommand = Commands.slash("author","作者を表示します。")
        val sayCommand = Commands.slash("say", "打った文字をbotに言わせます。")
                                            .addOption(OptionType.STRING, "msg", "言わせる文字を設定", true)

        // 指定したサーバーにコマンドを登録
        jda.updateCommands()
            .addCommands(thisHelpCommand, thisAuthorCommand, sayCommand)
            .queue()

    }

    override fun onReady(event: ReadyEvent) { //Botがログインしたときの処理
        println("起動しました")
    }

    override fun onMessageReceived(event : MessageReceivedEvent) {
        //Botがメッセージを受信したときの処理
        if(event.message.contentDisplay == "ぬ" && !event.author.isBot){//メッセージ内容を確認
            event.channel.sendMessage("ぬ").queue()
        }
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "help" -> {
                event.reply("実装準備中").setEphemeral(true).queue()
            }
            "author" -> {
                event.reply("made by emesan").queue()
            }
            "say" -> {
                val option = event.getOption("msg")!!
                event.reply(option.asString).queue()
            }
        }
    }

}

fun main(args:Array<String>) {
    val bot = BotClient()
    bot.main("")
}