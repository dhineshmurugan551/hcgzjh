package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DefaultSeedData
import com.example.data.DefaultUsers
import com.example.data.DisasterRepository
import com.example.model.*
import com.example.tts.DisasterVoiceAssistant
import com.example.ui.components.EmergencyTopBar
import com.example.ui.screens.*
import com.example.ui.theme.DisasterRescueTheme
import kotlinx.coroutines.launch

enum class AppScreen {
    CITIZEN_HOME,
    SOS_FORM,
    DISASTER_REPORT,
    LIVE_MAP,
    TRACK_REQUESTS,
    RELIEF_CAMPS,
    MEDICAL_ASSISTANCE,
    SAFETY_GUIDES,
    RESCUE_DASHBOARD,
    RELIEF_MANAGER_DASHBOARD,
    VOLUNTEER_DASHBOARD,
    ADMIN_DASHBOARD,
    USER_PROFILE
}

class MainActivity : ComponentActivity() {
    private lateinit var repository: DisasterRepository
    private lateinit var voiceAssistant: DisasterVoiceAssistant

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = DisasterRepository(applicationContext)
        voiceAssistant = DisasterVoiceAssistant(applicationContext)

        setContent {
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }

            val currentUser by repository.currentUser.collectAsStateWithLifecycle()
            val isHighContrast by repository.isHighContrast.collectAsStateWithLifecycle()
            val currentLanguage by repository.currentLanguage.collectAsStateWithLifecycle()

            val sosRequests by repository.allSosRequests.collectAsStateWithLifecycle(emptyList())
            val reports by repository.allReports.collectAsStateWithLifecycle(emptyList())
            val reliefCamps by repository.reliefCamps.collectAsStateWithLifecycle(emptyList())
            val activeAlerts by repository.activeAlerts.collectAsStateWithLifecycle(emptyList())
            val volunteerTasks by repository.volunteerTasks.collectAsStateWithLifecycle()
            val medicalRequests by repository.medicalRequests.collectAsStateWithLifecycle()
            val auditLogs by repository.auditLogs.collectAsStateWithLifecycle(emptyList())

            var currentScreen by remember { mutableStateOf(AppScreen.CITIZEN_HOME) }
            val screenHistory = remember { mutableStateListOf<AppScreen>() }
            var showRoleDialog by remember { mutableStateOf(false) }

            fun navigateTo(screen: AppScreen) {
                if (currentScreen != screen) {
                    screenHistory.add(currentScreen)
                    currentScreen = screen
                }
            }

            fun navigateBack() {
                if (screenHistory.isNotEmpty()) {
                    currentScreen = screenHistory.removeAt(screenHistory.lastIndex)
                } else {
                    currentScreen = when (currentUser.role) {
                        UserRole.CITIZEN -> AppScreen.CITIZEN_HOME
                        UserRole.RESCUE_TEAM, UserRole.MEDICAL_TEAM -> AppScreen.RESCUE_DASHBOARD
                        UserRole.RELIEF_MANAGER -> AppScreen.RELIEF_MANAGER_DASHBOARD
                        UserRole.VOLUNTEER -> AppScreen.VOLUNTEER_DASHBOARD
                        UserRole.DISTRICT_OFFICER, UserRole.SUPER_ADMIN -> AppScreen.ADMIN_DASHBOARD
                    }
                }
            }

            BackHandler(enabled = currentScreen != AppScreen.CITIZEN_HOME && currentScreen != AppScreen.ADMIN_DASHBOARD) {
                navigateBack()
            }

            // Sync initial screen when user role changes
            LaunchedEffect(currentUser.role) {
                currentScreen = when (currentUser.role) {
                    UserRole.CITIZEN -> AppScreen.CITIZEN_HOME
                    UserRole.RESCUE_TEAM, UserRole.MEDICAL_TEAM -> AppScreen.RESCUE_DASHBOARD
                    UserRole.RELIEF_MANAGER -> AppScreen.RELIEF_MANAGER_DASHBOARD
                    UserRole.VOLUNTEER -> AppScreen.VOLUNTEER_DASHBOARD
                    UserRole.DISTRICT_OFFICER, UserRole.SUPER_ADMIN -> AppScreen.ADMIN_DASHBOARD
                }
            }

            DisasterRescueTheme(highContrast = isHighContrast) {
                Scaffold(
                    modifier = Modifier.fillMaxSize().testTag("app_root_scaffold"),
                    topBar = {
                        EmergencyTopBar(
                            currentRole = currentUser.role,
                            currentLanguage = currentLanguage,
                            isHighContrast = isHighContrast,
                            onLanguageToggle = {
                                val next = if (currentLanguage == "en") "ta" else "en"
                                repository.setLanguage(next)
                            },
                            onHighContrastToggle = {
                                repository.toggleHighContrast()
                            },
                            onRoleSwitchClick = {
                                showRoleDialog = true
                            },
                            onEmergencyCallClick = {
                                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                                startActivity(dialIntent)
                            },
                            onProfileClick = {
                                navigateTo(AppScreen.USER_PROFILE)
                            }
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            AppScreen.CITIZEN_HOME -> {
                                CitizenHomeScreen(
                                    user = currentUser,
                                    sosRequests = sosRequests,
                                    alerts = activeAlerts,
                                    lang = currentLanguage,
                                    onSosClick = { navigateTo(AppScreen.SOS_FORM) },
                                    onReportClick = { navigateTo(AppScreen.DISASTER_REPORT) },
                                    onMapClick = { navigateTo(AppScreen.LIVE_MAP) },
                                    onReliefCampsClick = { navigateTo(AppScreen.RELIEF_CAMPS) },
                                    onMedicalHelpClick = { navigateTo(AppScreen.MEDICAL_ASSISTANCE) },
                                    onTrackRequestsClick = { navigateTo(AppScreen.TRACK_REQUESTS) },
                                    onSafetyGuidesClick = { navigateTo(AppScreen.SAFETY_GUIDES) }
                                )
                            }

                            AppScreen.SOS_FORM -> {
                                SosRequestScreen(
                                    user = currentUser,
                                    lang = currentLanguage,
                                    onSubmit = { category, people, addr, landmark, med, child, eld, dis, notes, lat, lon ->
                                        scope.launch {
                                            val (sos, duplicates) = repository.submitSosRequest(
                                                citizenName = currentUser.name,
                                                citizenPhone = currentUser.phone,
                                                category = category,
                                                peopleCount = people,
                                                latitude = lat,
                                                longitude = lon,
                                                locationAddress = addr,
                                                landmark = landmark,
                                                hasUrgentMedical = med,
                                                hasChildren = child,
                                                hasElderly = eld,
                                                hasDisability = dis,
                                                notes = notes
                                            )
                                            val msg = if (duplicates.isNotEmpty()) {
                                                "Distress signal transmitted (${sos.id})! AI detected ${duplicates.size} nearby requests in sector."
                                            } else {
                                                "Distress signal transmitted (${sos.id})! Priority: ${sos.aiPriority.labelEn}. Responders alerted."
                                            }
                                            snackbarHostState.showSnackbar(msg)
                                            currentScreen = AppScreen.TRACK_REQUESTS
                                        }
                                    },
                                    onBack = { navigateBack() }
                                )
                            }

                            AppScreen.DISASTER_REPORT -> {
                                DisasterReportScreen(
                                    reports = reports,
                                    lang = currentLanguage,
                                    onSubmitReport = { cat, desc, dist, loc, aff, lat, lon ->
                                        scope.launch {
                                            val rpt = repository.submitDisasterReport(cat, desc, dist, loc, lat, lon, aff)
                                            snackbarHostState.showSnackbar("Report ${rpt.id} logged successfully! Forwarded for verification.")
                                        }
                                    },
                                    onBack = { navigateBack() }
                                )
                            }

                            AppScreen.LIVE_MAP -> {
                                LiveDisasterMapScreen(
                                    sosRequests = sosRequests,
                                    reports = reports,
                                    camps = reliefCamps,
                                    lang = currentLanguage,
                                    onBack = { navigateBack() }
                                )
                            }

                            AppScreen.TRACK_REQUESTS -> {
                                TrackRequestsScreen(
                                    requests = sosRequests,
                                    lang = currentLanguage,
                                    onBack = { navigateBack() },
                                    onCancelRequest = { id ->
                                        scope.launch {
                                            repository.updateSosStatus(id, RequestStatus.CANCELLED, rejectionReason = "Cancelled by user")
                                            snackbarHostState.showSnackbar("Request $id cancelled.")
                                        }
                                    }
                                )
                            }

                            AppScreen.RELIEF_CAMPS -> {
                                ReliefCampsScreen(
                                    camps = reliefCamps,
                                    lang = currentLanguage,
                                    onBack = { navigateBack() }
                                )
                            }

                            AppScreen.MEDICAL_ASSISTANCE -> {
                                MedicalAssistanceScreen(
                                    medicalRequests = medicalRequests,
                                    lang = currentLanguage,
                                    onSubmitMedical = { name, phone, count, symptoms, loc, transport ->
                                        val req = MedicalRequest(
                                            id = "MED-${(100..999).random()}",
                                            requesterName = name,
                                            phone = phone,
                                            patientCount = count,
                                            symptoms = symptoms,
                                            location = loc,
                                            transportRequired = transport
                                        )
                                        repository.submitMedicalRequest(req)
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Medical assistance request logged! Unit assigned.")
                                        }
                                    },
                                    onBack = { navigateBack() }
                                )
                            }

                            AppScreen.SAFETY_GUIDES -> {
                                SafetyGuidelinesScreen(
                                    guides = DefaultSeedData.safetyGuides,
                                    voiceAssistant = voiceAssistant,
                                    lang = currentLanguage,
                                    onBack = { navigateBack() }
                                )
                            }

                            AppScreen.RESCUE_DASHBOARD -> {
                                RescueTeamScreen(
                                    user = currentUser,
                                    sosRequests = sosRequests,
                                    lang = currentLanguage,
                                    onUpdateStatus = { reqId, newStatus, reason ->
                                        scope.launch {
                                            repository.updateSosStatus(reqId, newStatus, currentUser.name, reason)
                                            snackbarHostState.showSnackbar("Incident $reqId status set to ${newStatus.labelEn}")
                                        }
                                    }
                                )
                            }

                            AppScreen.RELIEF_MANAGER_DASHBOARD -> {
                                ReliefManagerScreen(
                                    user = currentUser,
                                    camps = reliefCamps,
                                    lang = currentLanguage,
                                    onUpdateInventory = { campId, occupants, food, water, meds, blankets, status ->
                                        scope.launch {
                                            repository.updateCampInventory(campId, occupants, food, water, meds, blankets, status)
                                            snackbarHostState.showSnackbar("Relief camp inventory updated and synchronized.")
                                        }
                                    }
                                )
                            }

                            AppScreen.VOLUNTEER_DASHBOARD -> {
                                VolunteerScreen(
                                    user = currentUser,
                                    tasks = volunteerTasks,
                                    lang = currentLanguage,
                                    onToggleTask = { taskId ->
                                        repository.toggleVolunteerTaskAssignment(taskId, currentUser.name)
                                    },
                                    onCompleteTask = { taskId ->
                                        repository.completeVolunteerTask(taskId)
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Task marked completed! Thank you for your service.")
                                        }
                                    }
                                )
                            }

                            AppScreen.ADMIN_DASHBOARD -> {
                                AdminDashboardScreen(
                                    user = currentUser,
                                    sosRequests = sosRequests,
                                    reports = reports,
                                    camps = reliefCamps,
                                    alerts = activeAlerts,
                                    auditLogs = auditLogs,
                                    lang = currentLanguage,
                                    onVerifyReport = { reportId, status, reason ->
                                        scope.launch {
                                            repository.verifyReport(reportId, status, reason, currentUser.name)
                                            snackbarHostState.showSnackbar("Report $reportId verification updated: ${status.labelEn}")
                                        }
                                    },
                                    onPublishAlert = { title, titleTa, sev, desc, descTa, auth, dists ->
                                        scope.launch {
                                            repository.publishAlert(title, titleTa, sev, desc, descTa, auth, dists)
                                            snackbarHostState.showSnackbar("Public emergency alert broadcasted across districts!")
                                        }
                                    }
                                )
                            }

                            AppScreen.USER_PROFILE -> {
                                UserProfileScreen(
                                    user = currentUser,
                                    lang = currentLanguage,
                                    isHighContrast = isHighContrast,
                                    onToggleHighContrast = { repository.toggleHighContrast() },
                                    onLanguageChange = { repository.setLanguage(it) },
                                    onBack = { navigateBack() },
                                    onLogout = {
                                        repository.switchUser(DefaultUsers.citizenUser)
                                        currentScreen = AppScreen.CITIZEN_HOME
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Logged out. Reset to Citizen view.")
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Role switcher dialog
                    if (showRoleDialog) {
                        RoleSwitcherDialog(
                            currentRole = currentUser.role,
                            lang = currentLanguage,
                            onRoleSelected = { selectedUser ->
                                repository.switchUser(selectedUser)
                                showRoleDialog = false
                                scope.launch {
                                    snackbarHostState.showSnackbar("Switched role to: ${selectedUser.role.labelEn} (${selectedUser.name})")
                                }
                            },
                            onDismiss = { showRoleDialog = false }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceAssistant.shutdown()
    }
}
