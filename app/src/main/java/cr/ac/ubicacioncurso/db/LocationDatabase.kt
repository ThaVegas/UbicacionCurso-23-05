package cr.ac.ubicacioncurso.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cr.ac.ubicacioncurso.dao.LocationDao
import cr.ac.ubicacioncurso.entity.Location

@Database(entities = [Location::class], version = 1, exportSchema = false)
abstract class LocationDatabase : RoomDatabase(){

    abstract val locationDao : LocationDao

    companion object{

        private var INSTANCE : LocationDatabase? = null
        fun getInstance(context: Context) : LocationDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            LocationDatabase::class.java,
                            "datebase"
                        )
                            .allowMainThreadQueries()
                            .build()
                    INSTANCE = instance
                }
                    return instance
            }
        }
    }
}