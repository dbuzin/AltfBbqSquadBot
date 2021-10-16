import retrofit2.Call
import retrofit2.http.GET

interface AnecdoteApi {
    @GET("/RandJSON.aspx?CType=11")
    fun getRandom(): Call<AnecdoteResponse>
}