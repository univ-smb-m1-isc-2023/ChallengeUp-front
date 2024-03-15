package fr.usmb.challengeup.entities

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson

data class User(
    val id: Long,
    val username: String?,
    val email: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(username)
        parcel.writeString(email)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun toJSON(): String {
        return Gson().toJson(this)
    }

    fun fromJSON(json: String): User {
        return Gson().fromJson(json, User::class.java)
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
