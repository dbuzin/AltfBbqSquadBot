import retrofit2.Retrofit

class Anecdote(retrofit: Retrofit) {
    private val responseError = "Сейчас все шутники заняты"
    var api: AnecdoteApi = retrofit.create(AnecdoteApi::class.java)

    fun getRandomAnecdote(): String? {
        val response = api.getRandom().execute()
        return if (response.isSuccessful)
            response.body()?.content
        else responseError
    }
}