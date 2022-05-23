package cr.ac.ubicacioncurso.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "location_table")
data class Location(
    @PrimaryKey(autoGenerate = true)
    val locationID: Long?,
    val latitude: Double,
    val longitude: Double
)