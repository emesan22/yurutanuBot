package click.emesan.bot.yurutanuBot

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.awt.Color
import kotlin.system.exitProcess

class BotListener : ListenerAdapter() {
    private val logger: Logger = LogManager.getLogger(BotClient::class.java)

    //BOTが起動したら起動と出力
    override fun onReady(event: ReadyEvent) {
        logger.info("き ど う")

        var input: String?
        while (true) {
            input = readlnOrNull()
            if (input.equals("stop", ignoreCase = true)) {
                event.jda.shutdown()
                logger.info("て い し")
                exitProcess(0)
            }
        }
    }

    //メッセージ反応
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val content = event.message.contentDisplay
        val channel = event.channel

        if (!event.author.isBot) when {
            content.startsWith("ぬ") -> channel.sendMessage("ぬ").queue()
            content.startsWithAnyOf(listOf(":nu:", ":snu:")) -> channel.sendMessage("<:nu:1101830335718752261>").queue()
            content.startsWith("こん") && !content.startsWith("こんばんは") -> channel.sendMessage("こんにちは~").queue()
            content.startsWith("おは") -> channel.sendMessage("おはよう!").queue()
            content.startsWith("こんばんは") -> channel.sendMessage("こんばんは~").queue()
            content.startsWith("おやすみ") -> channel.sendMessage("おやすみ~ Good night!").queue()
        }
    }

    //コマンド処理
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val reply = { content: String -> event.reply(content) }

        when (event.name) {
            //コマンドの説明はmain.ktのコマンドの定義の部分を見てください
            //一般コマンド
            "help" -> handleHelpCommand(event)
            "author" -> handleAuthorCommand(event)
            "say" -> handleSayCommand(event)
            "roll" -> handleRollCommand(event)

            //ここからモデレーターコマンド
            "announce" -> handleAnnounceCommand(event)
            "announce-embed" -> handleAnnounceEmbedCommand(event)

            "kick" -> {
                reply("実装準備中").setEphemeral(true).queue()
            }

            "ban" -> {
                reply("実装準備中").setEphemeral(true).queue()
            }
        }
    }

    private fun handleHelpCommand(event: SlashCommandInteractionEvent) {
        // ヘルプコマンドの処理
        event.replyEmbeds(
            EmbedBuilder()
                .addField(
                    "一般機能",
                    "最初の文字が「ぬ」と書いた場合BOTはぬと返されます。また絵文字<:nu:1101830335718752261>だと<:nu:1101830335718752261>と返されます\n最初の文字が「おは」や「こん」「こんばんは」と書いた場合BOTは挨拶が返されます。",
                    false
                )
                .addField(
                    "一般コマンド",
                    "/help - 今表示しているもの\n/say - オプションで書いたものをBOTに言わせます。\n/roll - d0が最大値で振ります。\n今後も開発していきます。",
                    false
                )
                .addField(
                    "モデレーターコマンド",
                    "/announce <内容> <メンション>\n - 通常のメッセージでアナウンスします。\n/announce-embed <タイトル> <内容> <メンション> [画像(URL)]\n - 埋め込みメッセージでアナウンスします。",
                    false
                )
                .setTitle("ヘルプ")
                .setDescription("このBOTの説明をします。")
                .setFooter("Made by emesan ヱメサン#0001")
                .setColor(Color.GREEN)
                .build()
        ).queue()
    }

    private fun handleAuthorCommand(event: SlashCommandInteractionEvent) {
        // authorコマンドの処理
        event.reply("made by emesan").queue()
    }

    private fun handleSayCommand(event: SlashCommandInteractionEvent) {
        // sayコマンドの処理
        val option = event.getOption("content")!!

        event.reply(option.asString).queue()

        logger.info("Botは${option.asString}と言わせられました")
    }

    private fun handleRollCommand(event: SlashCommandInteractionEvent) {
        // rollコマンドの処理
        val max = event.getOption("d0")!!.asInt
        val range = (1..max)

        event.reply("1d${max} -> ${range.random()}").queue()
    }

    //ここからモデレーターコマンド
    private fun handleAnnounceCommand(event: SlashCommandInteractionEvent) {
        // announceコマンドの処理
        val to = event.getOption("to")!!.asRole.asMention
        val content = event.getOption("content")!!.asString.replace("\\n", "\n")

        event.reply("送信しました").setEphemeral(true).queue()
        event.channel.sendMessage("|| $to ||\n$content").queue()

        logger.info("アナウンスを通常の方式で送信しました。以下が内容です。")
        logger.info("メンション先:$to 内容:$content")
    }

    private fun handleAnnounceEmbedCommand(event: SlashCommandInteractionEvent) {
        // announce-embedコマンドの処理
        val to = event.getOption("to")!!.asRole.asMention
        val title = event.getOption("title")!!.asString.replace("\\n", "\n")
        val description = event.getOption("description")!!.asString.replace("\\n", "\n")
        var imageURL = "https://example.com/"

        if (event.getOption("image") != null) {
            imageURL = event.getOption("image")!!.asString
        }

        event.channel.sendMessage("|| $to ||").setEmbeds(
            EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setColor(Color.BLACK)
                .setImage(imageURL)
                .setFooter("Made by emesan ヱメサン#0001 - ゆるたぬサーバー")
                .build()
        ).queue()
        event.reply("送信しました").setEphemeral(true).queue()
        logger.info("アナウンスをembed方式で送信しました。以下が内容です。")
        logger.info("メンション先:$to タイトル:$title \n内容:$description")
    }

    /*
    private fun handleKickCommand(event: SlashCommandInteractionEvent) {
    // kickコマンドの処理
    // ...
    }

    private fun handleBanCommand(event: SlashCommandInteractionEvent) {
    // banコマンドの処理
    // ...
    }
    */

    // startsWithAnyOf 関数の実装
    private fun String.startsWithAnyOf(prefixes: List<String>): Boolean {
        return prefixes.any { this.startsWith(it) }
    }

}