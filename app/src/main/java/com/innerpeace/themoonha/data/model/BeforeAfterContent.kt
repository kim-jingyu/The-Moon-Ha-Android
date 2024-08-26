package com.innerpeace.themoonha.data.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Before&After 콘텐츠 데이터 클래스
 * @author 김진규
 * @since 2024.08.25
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.25  	김진규       최초 생성
 * </pre>
 */
data class BeforeAfterContent(
    val beforeUrl: String,
    val beforeIsImage: Boolean,
    val afterUrl: String,
    val afterIsImage: Boolean,
    val title: String,
    val profileImageUrl: String,
    val memberName: String,
    val hashtags: List<String>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: listOf()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flag: Int) {
        parcel.writeString(beforeUrl)
        parcel.writeByte(if (beforeIsImage) 1 else 0)
        parcel.writeString(afterUrl)
        parcel.writeByte(if (afterIsImage) 1 else 0)
        parcel.writeString(title)
        parcel.writeString(profileImageUrl)
        parcel.writeString(memberName)
        parcel.writeStringList(hashtags)
    }

    companion object CREATOR : Parcelable.Creator<BeforeAfterContent> {
        override fun createFromParcel(parcel: Parcel): BeforeAfterContent {
            return BeforeAfterContent(parcel)
        }

        override fun newArray(size: Int): Array<BeforeAfterContent?> {
            return arrayOfNulls(size)
        }
    }
}