import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.extensions.filters.Filter
import java.io.File
import java.util.*
import kotlin.collections.HashSet
import kotlin.random.Random

class BbqBot {

    private val initTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    private val timeScaler = 3600 * 1000L
    private val newsApi = NewsApi()
    private val membersFile = File("members.txt")
    private val chatMembers = HashSet<Long>()
    private val helpAnswer1 = "Я знаю юзеров этого чата, так что могу вставить свое слово. Если кто скажет Калич, мы дружно ответим: "
    private val helpAnswer2 = "Напишите /hohol_test , чтобы узнать кто настоящий хохол. Если хотите узнать кому сегодня достанется оболонь — просто спросите /obolon"
    private val obolonAnswer1 = "Посмотрим кто тут давно не пил Оболонь..."
    private val obolonAnswer2 = "Сегодня Оболонь получит — @"
    private var beerUserChosen = false
    private var beerUser = ""

    init {
        newsApi.loadNews()
        try {
            membersFile.createNewFile()
            val temp = membersFile.readLines()
            temp.forEach {
                chatMembers.add(it.toLong())
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var bot: Bot = bot {
        token = "2049054909:AAEaHh_N_VIZJIhnBAm73SMxiVNok3XwWGs"
        //logLevel = LogLevel.Network.Body
        dispatch {
            message(Filter.Text){
                saveUser(update)
                val answer = textContainsName(message.text)
                if(answer != "nothing" && answer!= "hohlo_news")
                    bot.sendMessage(chatId = ChatId.fromId(update.message!!.chat.id), text = answer)
                else if (answer == "hohlo_news") {
                    if (newsApi.haveNews()) {
                        val news = newsApi.getArticle()
                        val newsAnswer = "Вот что есть!\n${news.title}\n${news.description}\n${news.url}"
                        bot.sendMessage(chatId = ChatId.fromId(update.message!!.chat.id), text = newsAnswer)
                    }
                    else
                        bot.sendMessage(chatId = ChatId.fromId(update.message!!.chat.id), text = "СБУ не дает мне искать новости!")
                }

            }
            command("start_altf") {
                bot.sendMessage(chatId = ChatId.fromId(update.message!!.chat.id), text = "Здарова, уроды! Напишите /help , чтобы узнать что я могу")
            }
            command("help") {
                bot.sendMessage(chatId = ChatId.fromId(update.message!!.chat.id), text = helpAnswer1 + "Калич лох, иди работай!")
                bot.sendMessage(chatId = ChatId.fromId(update.message!!.chat.id), text = helpAnswer2)
            }
            command("obolon") {
                if (!beerUserChosen) {
                    bot.sendMessage(chatId = ChatId.fromId(update.message!!.chat.id), text = obolonAnswer1)
                    val userId = getBeerUser()
                    val name = bot.getChatMember(ChatId.fromId(update.message!!.chat.id), userId).get().user.username
                    bot.sendMessage(chatId = ChatId.fromId(update.message!!.chat.id), text = obolonAnswer2 + name)
                    beerUser = "@$name"
                    beerUserChosen = true
                    beerUserTimer()
                }
                else
                    bot.sendMessage(chatId = ChatId.fromId(update.message!!.chat.id), text = "Сегодня оболонь уже получили! Счастливчик — $beerUser")
            }
            command("hohol_test") {

            }
        }
    }

    private fun textContainsName(input: String?): String {
        return when {
            input?.contains("Буров ", ignoreCase = true) == true || input?.contains("Буров,", ignoreCase = true) == true -> "Буров воняет!"
            input?.contains("Чечен ", ignoreCase = true) == true || input?.contains("Чечен,", ignoreCase = true) == true -> "Извинись быстро!"
            input?.contains("Соболь ", ignoreCase = true) == true || input?.contains("Соболь,", ignoreCase = true) == true -> "Суп Фо Со в чуфальне!"
            input?.contains("Аллах ", ignoreCase = true) == true || input?.contains("Аллах,", ignoreCase = true) == true -> "Иншала брат!"
            input?.contains("Калич ", ignoreCase = true) == true || input?.contains("Калич,", ignoreCase = true) == true -> "Калич лох, иди работай!"
            input?.contains("Еж ", ignoreCase = true) == true || input?.contains("Еж,", ignoreCase = true) == true -> "Еж — настоящий украинский патриот"
            input?.contains("Бузин ", ignoreCase = true) == true || input?.contains("Бузин,", ignoreCase = true) == true -> "Бузин самый классный юзер в этом чатике!"
            input?.contains("Мираж ", ignoreCase = true) == true || input?.contains("Мираж,", ignoreCase = true) == true -> "Мираж, по тебе плачет вебкам!"
            input?.contains("У Хохлов", ignoreCase = true) == true -> "hohlo_news"
            else -> "nothing"
        }
    }
    private fun beerUserTimer() {
        val day = (24 * timeScaler) - (initTime * timeScaler)
        val task: TimerTask = object: TimerTask() {
            override fun run() {
                if (beerUserChosen){
                    beerUserChosen = false
                    beerUser = ""
                }
                newsApi.clearNews()
                newsApi.loadNews()
            }
        }
        val timer = Timer(true)
        timer.schedule(task, 1000, day)
    }

    private fun getBeerUser(): Long {
        return chatMembers.elementAt(Random.nextInt(0, chatMembers.size))
    }

    private fun saveUser(update: Update) {
        update.message?.from?.id?.let { chatMembers.add(it) }
        val current = membersFile.readLines()
        chatMembers.forEach {
            if (!current.contains(it.toString()))
                membersFile.appendText("${it}\n")
        }
    }

    fun start() {
        bot.startPolling()
    }
}