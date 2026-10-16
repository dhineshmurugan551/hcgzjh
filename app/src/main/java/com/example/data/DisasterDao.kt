package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DisasterDao {

    // Users
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    // SOS Requests
    @Query("SELECT * FROM sos_requests ORDER BY timestamp DESC")
    fun getAllSosRequests(): Flow<List<SosEntity>>

    @Query("SELECT * FROM sos_requests WHERE citizenId = :citizenId ORDER BY timestamp DESC")
    fun getSosRequestsByCitizen(citizenId: String): Flow<List<SosEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSosRequest(sos: SosEntity)

    @Update
    suspend fun updateSosRequest(sos: SosEntity)

    // Disaster Reports
    @Query("SELECT * FROM disaster_reports ORDER BY createdAt DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Update
    suspend fun updateReport(report: ReportEntity)

    // Relief Camps
    @Query("SELECT * FROM relief_camps ORDER BY name ASC")
    fun getAllReliefCamps(): Flow<List<ReliefCampEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReliefCamp(camp: ReliefCampEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReliefCamps(camps: List<ReliefCampEntity>)

    @Update
    suspend fun updateReliefCamp(camp: ReliefCampEntity)

    // Alerts
    @Query("SELECT * FROM disaster_alerts WHERE isActive = 1 ORDER BY publishedAt DESC")
    fun getActiveAlerts(): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAlerts(alerts: List<AlertEntity>)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)
}
