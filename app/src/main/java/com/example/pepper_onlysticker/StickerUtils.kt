package com.example.pepper_onlysticker

object StickerUtils {

    data class Sticker (var stickerId: String, var attachedObject: String)
    var stickers = arrayListOf(
        Sticker("C09A634D9870AEDC","book"),
        //Sticker("B15BC4B081D2A7E0","lemonchair"),
        Sticker("C15DE122BF2973DF","TV Monitor"),
        Sticker("71417EE260BBA6F3","Phone")

    )


}