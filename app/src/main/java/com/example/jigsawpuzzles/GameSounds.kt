package com.example.jigsawpuzzles

import android.content.Context
import android.media.MediaPlayer

class GameSounds(val context: Context) {

     fun playClickSound() = MediaPlayer.create(context,R.raw.click_sound).start()

     fun playFitSound() = MediaPlayer.create(context, R.raw.fit_sound).start()

     fun playSuccessSound() = MediaPlayer.create(context, R.raw.success_sound).start()

     fun playSoundEndOfMovement() = MediaPlayer.create(context, R.raw.end_of_movement_sound).start()


}