import com.google.gson.annotations.SerializedName

data class AnecdoteResponse(@SerializedName("content") val content: String)
