package se.asapehrsson.podtest.data

import com.google.gson.annotations.SerializedName

data class Program(
        @SerializedName("description") val description: String? = null,
        @SerializedName("programcategory") val programcategory: Programcategory? = null,
        @SerializedName("broadcastinfo") val broadcastinfo: String? = null,
        @SerializedName("email") val email: String? = null,
        @SerializedName("phone") val phone: String? = null,
        @SerializedName("programurl") val programurl: String? = null,
        @SerializedName("programimage") val programimage: String? = null,
        @SerializedName("programimagetemplate") val programimagetemplate: String? = null,
        @SerializedName("programimagewide") val programimagewide: String? = null,
        @SerializedName("programimagetemplatewide") val programimagetemplatewide: String? = null,
        @SerializedName("socialimage") val socialimage: String? = null,
        @SerializedName("socialimagetemplate") val socialimagetemplate: String? = null,
        @SerializedName("socialmediaplatforms") val socialmediaplatforms: List<Socialmediaplatform>? = null,
        @SerializedName("channel") val channel: Channel? = null,
        @SerializedName("archived") val archived: Boolean? = null,
        @SerializedName("hasondemand") val hasondemand: Boolean? = null,
        @SerializedName("haspod") val haspod: Boolean? = null,
        @SerializedName("responsibleeditor") val responsibleeditor: String? = null,
        @SerializedName("id") val id: Int? = null,
        @SerializedName("name") val name: String? = null,
        @SerializedName("payoff") val payoff: String? = null
)
