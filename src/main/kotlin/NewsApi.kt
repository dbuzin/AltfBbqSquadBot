import com.kwabenaberko.newsapilib.NewsApiClient
import com.kwabenaberko.newsapilib.models.Article
import com.kwabenaberko.newsapilib.models.request.EverythingRequest
import com.kwabenaberko.newsapilib.models.response.ArticleResponse
import kotlin.random.Random

class NewsApi {
    private val apiKey = "77fb51cfe64f408aaf19c0e84d6a399a"
    private val apiClient = NewsApiClient(apiKey)
    private val articles = ArrayList<Article>()
    private var haveNews = true

    fun loadNews() {
        val builder = EverythingRequest.Builder()
            .language("ru")
            .q("ukraine")
            .build()
        apiClient.getEverything(builder, object : NewsApiClient.ArticlesResponseCallback {
            override fun onSuccess(p0: ArticleResponse?) {
                haveNews = true
                p0?.articles?.forEach {
                    articles.add(it)
                }
            }

            override fun onFailure(p0: Throwable?) {
                p0?.printStackTrace()
                haveNews = false
            }
        })
    }

    fun clearNews() {
        articles.clear()
    }

    fun haveNews(): Boolean {
        return haveNews
    }

    fun getArticle(): Article {
        return articles[Random.nextInt(0, articles.size)]
    }
}