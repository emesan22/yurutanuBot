package click.emesan.bot.yurutanuBot

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.awt.Color
import java.awt.Rectangle
import javax.swing.JFrame

class BotListener : ListenerAdapter() {
    private val logger: Logger = LogManager.getLogger(BotClient::class.java)
    private var toggleState = "オフライン"

    // GUIで操作予定
    fun yFrame() {
        val frame = JFrame()
        frame.bounds = Rectangle(300, 150)
        frame.setLocationRelativeTo(null)
        frame.title = "ゆるたぬ専用Bot"
        frame.isResizable = false
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isVisible = true
        logger.info("GUI Ready")
        //5月5日今日地震があった。心配なので今後は日記をつけることにした。

    }

    //BOTが起動したら起動と出力
    override fun onReady(event: ReadyEvent) {
        println("き ど う")
        logger.info("BOT is Start!")
        toggleState = "オンライン"
    }

    //メッセージ反応
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (!event.author.isBot) {
            if (event.message.contentDisplay.startsWith("ぬ")){
                 event.channel.sendMessage("ぬ").queue()
            } else if (event.message.contentDisplay.startsWith(":nu:") || event.message.contentDisplay.startsWith(":snu:")) {
                event.channel.sendMessage("<:nu:1101830335718752261>").queue()

            } else if(event.message.contentDisplay.startsWith("こん") && !event.message.contentDisplay.startsWith("こんばんは")) {
                event.channel.sendMessage(
                    "こんにちは~"
                ).queue()

            } else if (event.message.contentDisplay.startsWith("おは")) {
                event.channel.sendMessage("おはよう!").queue()

            } else if (event.message.contentDisplay.startsWith("こんばんは")) {
                event.channel.sendMessage("こんばんは~").queue()

            } else if (event.message.contentDisplay.startsWith("おやすみ")) {
                event.channel.sendMessage("おやすみ~ Good night!").queue()

            }
        }
    }

    //コマンド処理
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "help" -> {
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

            "author" -> {
                event.reply("made by emesan").queue()
            }

            "say" -> {
                val option = event.getOption("content")!!

                event.reply(option.asString).queue()

                println("Botは${option.asString}と言わせられました")
                logger.info("Bot made me say ${option.asString}")
            }

            "roll" -> {
                val max = event.getOption("d0")?.asInt!!
                val range = (1..max)

                event.reply("1d${max} -> ${range.random()}").queue()
            }

            //ここからモデレーターコマンド
            "announce" -> {
                val to = event.getOption("to")!!.asRole.asMention
                val content = event.getOption("content")!!.asString.replace("\\n", "\n")

                event.reply("送信しました").setEphemeral(true).queue()
                event.channel.sendMessage("|| $to ||\n$content").queue()
            }

            "announce-embed" -> {
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