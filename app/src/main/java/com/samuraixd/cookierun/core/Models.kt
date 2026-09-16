package com.samuraixd.cookierun.core

data class RunAction(val type:String, val tMs:Long, val durationMs:Long=0)
data class RunRoute(val name:String, val createdAt:Long, val durationMs:Long, val actions:List<RunAction>)
enum class BotMode { IDLE, RECORDING, PLAYING, MATCHING }
enum class GameStage { UNKNOWN, MAINMENU, PURCHASE_ITEM, GAME_START, GAME_RELAY, GAME_COMPLETE, CARD_MINIGAME }
