package com.example.aop_part2_chapter04

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.aop_part2_chapter04.dao.HistoryDao
import com.example.aop_part2_chapter04.model.History

@Database(entities = [History::class], version = 1)
// AppDatabase를 가져올때 HistoryDao를 사용할 수 있도록 설정
abstract class AppDatabase : RoomDatabase(){
    abstract fun historyDao(): HistoryDao
}