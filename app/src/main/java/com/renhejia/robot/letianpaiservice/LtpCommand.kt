package com.renhejia.robot.letianpaiservice

import android.os.Parcel
import android.os.Parcelable

class LtpCommand : Parcelable {
    var command: String? = null
    var data: String? = null

    constructor()

    constructor(cmd: String?, data: String?) {
        this.command = cmd
        this.data = data
    }

    constructor(`in`: Parcel) {
        this.command = `in`.readString()
        this.data = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(command)
        dest.writeString(data)
    }

    fun readFromParcel(dest: Parcel) {
        command = dest.readString()
        data = dest.readString()
    }

    override fun toString(): String {
        return "LtpCommand{" +
                "command='" + command + '\'' +
                ", data='" + data + '\'' +
                '}'
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<LtpCommand> = object : Parcelable.Creator<LtpCommand> {
            override fun createFromParcel(`in`: Parcel): LtpCommand {
                return LtpCommand(`in`)
            }

            override fun newArray(size: Int): Array<LtpCommand?> {
                return arrayOfNulls(size)
            }
        }
    }
}
