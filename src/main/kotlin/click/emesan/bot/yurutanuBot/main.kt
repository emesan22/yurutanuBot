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
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.requests.GatewayIntent

class BotClient : ListenerAdapter() {
    private lateinit var jda: JDA

    fun main(token: String) { //トークンを使ってBotを起動する部分
        jda = JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
            .setRawEventsEnabled(true)
            .addEventListeners(this)
            .setActivity(Activity.playing("お前らチャンネル登録しろ"))
            .build()

        jda.awaitReady()

        /*
        * TODO:
        *   - make kick command
        *   - make ban command
        */

        // 登録するコマンドを作成
        val thisHelpCommand = Commands.slash("help","このBOTの説明を表示します。")
        val thisAuthorCommand = Commands.slash("author","作者を表示します。")
        val sayCommand = Commands.slash("say", "打った文字をbotに言わせます。")
            .addOption(OptionType.STRING, "msg", "言わせる文字を設定", true)
        val kickCommand = Commands.slash("kick", "メンバーをキックします")
            .addOptions(
                OptionData(OptionType.USER, "user", "kickするメンバー",true),
                OptionData(OptionType.STRING,"reason","キックする理由(DMに送られます。)", true)
            )
        val banCommand = Commands.slash("ban", "メンバーを期限付きでBANします。")
            .addOptions(
                OptionData(OptionType.USER, "user","BANをするメンバー", true),
                OptionData(OptionType.STRING,"reason","BANする理由(DMに送られます。)", true)
            )

        // 指定したサーバーにコマンドを登録
        jda.updateCommands()
            .addCommands(thisHelpCommand, thisAuthorCommand, sayCommand,kickCommand,banCommand)
            .queue()

    }

    override fun onReady(event: ReadyEvent) { //Botがログインしたときの処理
        println("き ど う")
    }

    override fun onMessageReceived(event : MessageReceivedEvent) {
        //Botがメッセージを受信したときの処理
        if(!event.author.isBot){
            if(event.message.contentDisplay.contains("ぬ")) event.channel.sendMessage("ぬ").queue()
            if(event.message.contentDisplay.contains("こん")) event.channel.sendMessage("こんにちは~").queue()
            if(event.message.contentDisplay.contains("おは")) event.channel.sendMessage("おはよう!").queue()
            if(event.message.contentDisplay.contains("こんばんは")) event.channel.sendMessage("こんばんは~").queue()
            if(event.message.contentDisplay.contains("おやすみ")) event.channel.sendMessage("おやすみ~ Good night!").queue()
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
                println("Botは${option.asString}と言わせられました")
            }
            "kick" -> {
                event.reply("実装準備中").setEphemeral(true).queue()
            }
            "ban" -> {
                event.reply("実装準備中").setEphemeral(true).queue()
            }
        }
    }

}

fun main(args:Array<String>) {
    val bot = BotClient()
    val token = System.getenv("Discord_Bot_Token")
    bot.main(token)
}