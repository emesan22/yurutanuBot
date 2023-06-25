package click.emesan.bot.yurutanuBot

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.events.session.ShutdownEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.json.JSONObject
import java.awt.Color
import java.io.File
import kotlin.system.exitProcess

class BotListener : ListenerAdapter() {
    private val logger: Logger = LogManager.getLogger(BotClient::class.java)
    private val points: HashMap<String, Int> = HashMap()
    private val dataFile: File = File("src/main/resources/point.json")

    init {
        loadData()
    }

    private fun loadData() {
        if (dataFile.exists()) {
            val jsonString = dataFile.readText()
            val json = JSONObject(jsonString)
            val pointsJson = json.getJSONObject("points")

            points.clear()
            for (userId in pointsJson.keySet()) {
                val point = pointsJson.getInt(userId)
                points[userId] = point
            }
        }
    }

    private fun saveData() {
        val json = JSONObject()
        val pointsJson = JSONObject()

        for ((userId, point) in points) {
            pointsJson.put(userId, point)
        }

        json.put("points", pointsJson)

        dataFile.writeText(json.toString())
    }

    //BOTが起動したら起動と出力
    override fun onReady(event: ReadyEvent) {
        logger.info("き ど う")
    }

    override fun onShutdown(event: ShutdownEvent) {
        saveData()
        exitProcess(0)
    }

    //入退室通知
    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val embed = EmbedBuilder()
            .setAuthor("${event.member.effectiveName}がサーバーに参加しました!", null, event.member.effectiveAvatarUrl)
            .setColor(Color.GREEN)
            .build()
        event.guild.getTextChannelById("1122304483154726932")?.sendMessageEmbeds(embed)!!.queue()
    }

    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
        val message = "<:__:1122315507236868176> ${event.member?.user?.name}が退出しました"
        event.guild.getTextChannelById("1122304483154726932")?.sendMessage(message)?.queue()
    }

    //メッセージ反応
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (!event.author.isBot) {
            tubuyakiReaction(event)
            replayNu(event)
        }
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if (event.channel.id == "1119928574825205820" && event.reaction.emoji == Emoji.fromUnicode("💬")) {
            event.reaction.retrieveUsers().queue { users ->
                val count = users.size
                if (count >= 2) tubuyakiStartThread(event)
            }
        }
    }

    private fun tubuyakiReaction(event: MessageReceivedEvent) {
        if (event.channel.id == "1119928574825205820") {
            event.message.addReaction(Emoji.fromUnicode("💬")).queue()
            event.message.addReaction(Emoji.fromUnicode("❤️")).queue()
        }
    }

    private fun tubuyakiStartThread(event: MessageReactionAddEvent) {
        val reaction = event.reaction
        event.channel.removeReactionById(reaction.messageId, Emoji.fromUnicode("💬")).queue()
        event.channel.retrieveMessageById(event.messageId).queue { message ->
            val user = message.author.name
            event.guild.getTextChannelById(event.channel.id)?.createThreadChannel("${user}のつぶやきスレッド", event.messageId)
                ?.queue()
        }
    }

    private fun replayNu(event: MessageReceivedEvent){
        if (event.startWithMessage("ぬ")) {
            val authorId = event.message.author.id
            points[authorId] = points.getOrDefault(authorId, 0) + 1
            event.sendMessage(arrayOf("ぬぬ~", "ぬ!", "ぬ?", "ぬ!ぬぬ", "ぬ~ぬ~"))
        }
        if (event.startWithMessage(arrayOf(":nu:", ":snu:"))) {
            event.sendMessage("<:nu:1101830335718752261>")
        }
        if (event.startWithMessage("こん") && !event.startWithMessage("こんばんは")) {
            event.sendMessage("こんにちは~")
            val authorId = event.message.author.id
            points[authorId] = points.getOrDefault(authorId, 0) + 1
        }
        if (event.startWithMessage("おは")) {
            event.sendMessage("おはよう!")
        }
        if (event.startWithMessage("こんばんは")) {
            event.sendMessage("こんばんは~")
        }
        if (event.startWithMessage("おやすみ")) {
            event.sendMessage("おやすみ~ Good night!")
        }
    }

    //メッセージを送信するための物
    private fun MessageReceivedEvent.sendMessage(message: String) {
        channel.sendMessage(message).queue()
        return
    }

    private fun MessageReceivedEvent.sendMessage(message: Array<String>) {
        val indexN = (message.indices).random()
        channel.sendMessage(message[indexN]).queue()
        return
    }

    private fun MessageReceivedEvent.startWithMessage(messagePrefix: String): Boolean {
        return message.contentDisplay.startsWith(messagePrefix)
    }

    private fun MessageReceivedEvent.startWithMessage(listMessagePrefix: Array<String>): Boolean {
        val indexN = (listMessagePrefix.indices).random()
        return message.contentDisplay.startsWith(listMessagePrefix[indexN])
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

            //鯖整備用
            "setanofgauh" -> {

            }
            //テスト

            "testconection" -> {
                testModmail(event)
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

    private fun handleRankingCommand(event: SlashCommandInteractionEvent) {
        val count = event.getOption("count")?.asLong ?: 10
        val rankingEmbed = EmbedBuilder()
            .setTitle("ランキング!")
            .setColor(Color.GREEN)

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

    private fun handleCreateMethod(event: SlashCommandInteractionEvent){

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

    // テスト
    private fun testModmail(event: SlashCommandInteractionEvent) {
        val content = TextInput.create("content", "何をされましたか?\n証拠はスレッドで出してください。", TextInputStyle.PARAGRAPH)
            .setPlaceholder("〇〇が私の家に盗みました...")
            .setMinLength(3)
            .setMaxLength(2000)
            .build()

        val modol = Modal.create("arasi","荒し報告")
            .addComponents(ActionRow.of(content))
            .build()

        event.replyModal(modol).queue()
    }

    // startsWithAnyOf 関数の実装
    private fun String.startsWithAnyOf(prefixes: List<String>): Boolean {
        return prefixes.any { this.startsWith(it) }
    }

}