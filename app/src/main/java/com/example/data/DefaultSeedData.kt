package com.example.data

import com.example.model.*

object DefaultUsers {
    val citizenUser = UserAccount(
        id = "USR-CITIZEN-01",
        name = "Kavitha Raman",
        phone = "9876543210",
        role = UserRole.CITIZEN,
        district = "Chennai",
        isVerified = true,
        accessibilityNeeds = "Elderly mother at home, requires ground floor access",
        preferredLanguage = "en"
    )

    val rescueTeamUser = UserAccount(
        id = "USR-RESCUE-01",
        name = "Inspector Arulraj (NDRF Batch 4)",
        phone = "9876543211",
        role = UserRole.RESCUE_TEAM,
        district = "Chennai",
        isVerified = true,
        organization = "National Disaster Response Force (NDRF)",
        preferredLanguage = "en"
    )

    val medicalTeamUser = UserAccount(
        id = "USR-MED-01",
        name = "Dr. Priya Sundaram",
        phone = "9876543212",
        role = UserRole.MEDICAL_TEAM,
        district = "Chennai",
        isVerified = true,
        organization = "Govt Stanley Medical College Rapid Response",
        preferredLanguage = "ta"
    )

    val volunteerUser = UserAccount(
        id = "USR-VOL-01",
        name = "Karthik Subramanian",
        phone = "9876543213",
        role = UserRole.VOLUNTEER,
        district = "Chennai",
        isVerified = true,
        organization = "Tamil Nadu Youth Disaster Volunteers",
        preferredLanguage = "en"
    )

    val reliefManagerUser = UserAccount(
        id = "USR-RELIEF-01",
        name = "M. Selvamani",
        phone = "9876543214",
        role = UserRole.RELIEF_MANAGER,
        district = "Chennai",
        isVerified = true,
        organization = "Greater Chennai Corporation Relief Cell",
        preferredLanguage = "en"
    )

    val districtOfficerUser = UserAccount(
        id = "USR-DISTRICT-01",
        name = "Dr. R. Alagarsamy IAS",
        phone = "9876543215",
        role = UserRole.DISTRICT_OFFICER,
        district = "Chennai",
        isVerified = true,
        organization = "District Disaster Management Authority (DDMA)",
        preferredLanguage = "en"
    )

    val superAdminUser = UserAccount(
        id = "USR-ADMIN-01",
        name = "State Emergency Operations Centre (SEOC)",
        phone = "9876543216",
        role = UserRole.SUPER_ADMIN,
        district = "State HQ - Tamil Nadu",
        isVerified = true,
        organization = "Tamil Nadu State Disaster Management Authority (TNSDMA)",
        preferredLanguage = "en"
    )

    val allPresetUsers = listOf(
        citizenUser,
        rescueTeamUser,
        medicalTeamUser,
        volunteerUser,
        reliefManagerUser,
        districtOfficerUser,
        superAdminUser
    )
}

object DefaultSeedData {
    val initialSosRequests = listOf(
        SOSRequest(
            id = "SOS-TN-4102",
            citizenId = "USR-CITIZEN-01",
            citizenName = "Kavitha Raman",
            citizenPhone = "9876543210",
            category = DisasterCategory.FLOODING,
            peopleCount = 4,
            latitude = 12.9815,
            longitude = 80.2180,
            locationAddress = "14, Gandhi Road, Velachery West",
            landmark = "Near Phoenix Marketcity back gate",
            hasUrgentMedical = false,
            hasChildren = true,
            hasElderly = true,
            hasDisability = false,
            notes = "Ground floor submerged up to 4.5 feet water. We are on the first floor terrace. Power out since morning. Drinking water finished.",
            status = RequestStatus.TEAM_DISPATCHED,
            assignedTeamId = "USR-RESCUE-01",
            assignedTeamName = "NDRF Unit 4 - Inflatable Boat Alpha",
            aiPriority = PriorityLevel.HIGH,
            aiSummary = "Priority High: Water height 4.5ft, 2 vulnerable occupants (infant & elderly). Boat team en route.",
            timestamp = System.currentTimeMillis() - 45 * 60 * 1000
        ),
        SOSRequest(
            id = "SOS-TN-4105",
            citizenId = "USR-CITIZEN-02",
            citizenName = "Murugan Pandian",
            citizenPhone = "9444123456",
            category = DisasterCategory.COLLAPSED_BUILDING,
            peopleCount = 3,
            latitude = 13.0422,
            longitude = 80.2337,
            locationAddress = "Plot 82, Lake View 3rd Street, Madipakkam",
            landmark = "Opposite Sri Krishna Temple",
            hasUrgentMedical = true,
            hasChildren = false,
            hasElderly = true,
            hasDisability = true,
            notes = "Compound wall and balcony shed collapsed. 72-year-old grandfather injured with arm bleeding and immobilized.",
            status = RequestStatus.ASSIGNED,
            assignedTeamId = "USR-MED-01",
            assignedTeamName = "Govt Stanley Medical QRT + Fire Services",
            aiPriority = PriorityLevel.CRITICAL,
            aiSummary = "Priority CRITICAL: Structural collapse with active trauma bleeding. Medical evacuation required.",
            timestamp = System.currentTimeMillis() - 25 * 60 * 1000
        ),
        SOSRequest(
            id = "SOS-TN-4109",
            citizenId = "USR-CITIZEN-03",
            citizenName = "Anitha Krishnan",
            citizenPhone = "9840223344",
            category = DisasterCategory.CYCLONE,
            peopleCount = 6,
            latitude = 11.7480,
            longitude = 79.7714,
            locationAddress = "Coastal Fishermen Colony, Cuddalore OT",
            landmark = "Near Old Harbour Light post",
            hasUrgentMedical = false,
            hasChildren = true,
            hasElderly = false,
            hasDisability = false,
            notes = "Roof sheets blown away by gale winds. Strong storm surge approaching. Need urgent shelter transport.",
            status = RequestStatus.UNDER_REVIEW,
            aiPriority = PriorityLevel.HIGH,
            aiSummary = "Priority High: Cyclone wind structural damage in coastal surge zone. Requires evacuation to camp.",
            timestamp = System.currentTimeMillis() - 10 * 60 * 1000
        )
    )

    val initialReports = listOf(
        DisasterReport(
            id = "RPT-101",
            reporterName = "R. Senthil",
            reporterPhone = "9841098765",
            category = DisasterCategory.BLOCKED_ROAD,
            description = "Subway completely flooded and blocked. Tree fallen across 100 Feet Road junction.",
            district = "Chennai",
            location = "Madipakkam - Velachery Main Road Subway",
            latitude = 12.9730,
            longitude = 80.2090,
            affectedPeople = 500,
            verificationStatus = VerificationStatus.VERIFIED,
            priority = PriorityLevel.HIGH,
            assignedOfficer = "Traffic Police & Highways Dept",
            createdAt = System.currentTimeMillis() - 3 * 3600 * 1000
        ),
        DisasterReport(
            id = "RPT-102",
            reporterName = "Meenakshi Sundar",
            reporterPhone = "9790112233",
            category = DisasterCategory.POWER_OUTAGE,
            description = "Distribution transformer exploded after waterlogging. Whole street without power.",
            district = "Chennai",
            location = "Srinagar Colony, Saidapet",
            latitude = 13.0210,
            longitude = 80.2220,
            affectedPeople = 120,
            verificationStatus = VerificationStatus.VERIFIED,
            priority = PriorityLevel.MEDIUM,
            assignedOfficer = "TANGEDCO Emergency Line Unit",
            createdAt = System.currentTimeMillis() - 5 * 3600 * 1000
        ),
        DisasterReport(
            id = "RPT-103",
            reporterName = "Gopinath",
            reporterPhone = "9884556677",
            category = DisasterCategory.FLOODING,
            description = "Adyar river overflowing near Jafferkhanpet causeway. Water entered 15 ground-floor homes.",
            district = "Chennai",
            location = "Jafferkhanpet Causeway",
            latitude = 13.0290,
            longitude = 80.2080,
            affectedPeople = 60,
            verificationStatus = VerificationStatus.PENDING,
            priority = PriorityLevel.CRITICAL,
            createdAt = System.currentTimeMillis() - 40 * 60 * 1000
        )
    )

    val initialReliefCamps = listOf(
        ReliefCamp(
            id = "CAMP-01",
            name = "Velachery Community Hall Disaster Relief Centre",
            address = "No. 12, 100 Feet Bypass Road, Velachery, Chennai",
            district = "Chennai",
            latitude = 12.9800,
            longitude = 80.2210,
            contactPhone = "044-22441100",
            managerName = "M. Selvamani",
            totalCapacity = 450,
            currentOccupants = 280,
            foodAvailable = true,
            waterAvailable = true,
            medicalAvailable = true,
            wheelchairAccessible = true,
            foodStockPacks = 650,
            waterLitres = 2200,
            medicalKits = 45,
            blankets = 300,
            operatingStatus = CampStatus.ACTIVE,
            openingHours = "24 Hours Continuous Service"
        ),
        ReliefCamp(
            id = "CAMP-02",
            name = "Ripon Building Ground Emergency Shelter",
            address = "Sydenhams Road, Periamet, Chennai",
            district = "Chennai",
            latitude = 13.0825,
            longitude = 80.2760,
            contactPhone = "044-25619206",
            managerName = "K. Ramanathan",
            totalCapacity = 800,
            currentOccupants = 520,
            foodAvailable = true,
            waterAvailable = true,
            medicalAvailable = true,
            wheelchairAccessible = true,
            foodStockPacks = 1200,
            waterLitres = 4500,
            medicalKits = 120,
            blankets = 650,
            operatingStatus = CampStatus.ACTIVE,
            openingHours = "24x7 State Central Hub"
        ),
        ReliefCamp(
            id = "CAMP-03",
            name = "Tambaram Railway Govt High School Relief Centre",
            address = "GST Road, West Tambaram, Chennai",
            district = "Chengalpattu",
            latitude = 12.9249,
            longitude = 80.1190,
            contactPhone = "044-22265511",
            managerName = "S. Vijayakumar",
            totalCapacity = 300,
            currentOccupants = 295,
            foodAvailable = true,
            waterAvailable = true,
            medicalAvailable = false,
            wheelchairAccessible = false,
            foodStockPacks = 80,
            waterLitres = 350,
            medicalKits = 4,
            blankets = 40,
            operatingStatus = CampStatus.AT_CAPACITY,
            openingHours = "24 Hours (Low Stock Warning)"
        ),
        ReliefCamp(
            id = "CAMP-04",
            name = "Cuddalore Port Coastal Evacuation Centre",
            address = "Harbour Road, Cuddalore Old Town",
            district = "Cuddalore",
            latitude = 11.7390,
            longitude = 79.7680,
            contactPhone = "04142-220100",
            managerName = "T. Anbarasan",
            totalCapacity = 600,
            currentOccupants = 340,
            foodAvailable = true,
            waterAvailable = true,
            medicalAvailable = true,
            wheelchairAccessible = true,
            foodStockPacks = 850,
            waterLitres = 3100,
            medicalKits = 70,
            blankets = 450,
            operatingStatus = CampStatus.ACTIVE,
            openingHours = "24 Hours Coastal Hub"
        )
    )

    val initialAlerts = listOf(
        DisasterAlert(
            id = "ALT-2026-01",
            title = "EXTREME HEAVY RAINFALL & FLOOD RED ALERT",
            titleTa = "கனமழை மற்றும் வெள்ள அபாய சிவப்பு எச்சரிக்கை",
            severity = AlertSeverity.EMERGENCY,
            description = "IMD warns of extremely heavy rainfall (above 24cm) across coastal Tamil Nadu due to deep depression. Residents in low-lying areas advised to move to relief camps immediately.",
            descriptionTa = "வானிலை மையம் எச்சரிக்கை: காற்றழுத்த தாழ்வு மண்டலம் காரணமாக சென்னை, செங்கல்பட்டு, கடலூரில் 24 செ.மீ மேல் அதி கனமழை பெய்ய வாய்ப்பு. தாழ்வான பகுதி மக்கள் உடனடியாக நிவாரண முகாம்களுக்குச் செல்ல அறிவுறுத்தப்படுகிறார்கள்.",
            issuingAuthority = "IMD & Tamil Nadu State Disaster Management Authority (TNSDMA)",
            affectedDistricts = listOf("Chennai", "Chengalpattu", "Kanchipuram", "Cuddalore", "Villupuram"),
            publishedAt = System.currentTimeMillis() - 2 * 3600 * 1000
        ),
        DisasterAlert(
            id = "ALT-2026-02",
            title = "Severe Cyclone Warning: Gale Winds 85-95 kmph",
            titleTa = "தீவிர புயல் எச்சரிக்கை: மணிக்கு 85-95 கி.மீ வேகத்தில் காற்று",
            severity = AlertSeverity.WARNING,
            description = "Cyclone approaching north Tamil Nadu coast. Fishermen strictly prohibited from venturing into sea. Secure loose tin roofs and keep emergency grab bags ready.",
            descriptionTa = "வட தமிழக கடற்கரையை நோக்கி புயல் நகர்கிறது. மீனவர்கள் கடலுக்குச் செல்ல வேண்டாம். தற்காலிக மேற்கூரைகளை பலப்படுத்தவும், அவசரப் பொருட்களை கையில் வைத்திருக்கவும்.",
            issuingAuthority = "Regional Meteorological Centre, Chennai",
            affectedDistricts = listOf("Chennai", "Cuddalore", "Nagapattinam"),
            publishedAt = System.currentTimeMillis() - 6 * 3600 * 1000
        )
    )

    val initialVolunteerTasks = listOf(
        VolunteerTask(
            id = "TSK-01",
            title = "Food & Water Pack Distribution",
            description = "Distribute 800 packed meals and 1-litre water bottles to stranded residents in Velachery and Madipakkam.",
            category = "FOOD_DISTRIBUTION",
            district = "Chennai",
            location = "Velachery Relief Camp Distribution Point",
            volunteersNeeded = 12,
            assignedVolunteers = listOf("Karthik Subramanian", "Vignesh K", "Sangeetha M"),
            isCompleted = false,
            priority = PriorityLevel.HIGH
        ),
        VolunteerTask(
            id = "TSK-02",
            title = "Elderly Evacuation Support",
            description = "Assist NDRF boat teams in carrying elderly residents and children from boats to ambulances.",
            category = "RESCUE",
            district = "Chennai",
            location = "Saidapet Causeway Junction",
            volunteersNeeded = 8,
            assignedVolunteers = listOf("Karthik Subramanian"),
            isCompleted = false,
            priority = PriorityLevel.CRITICAL
        ),
        VolunteerTask(
            id = "TSK-03",
            title = "Relief Camp Registration & Token Desk",
            description = "Register incoming displaced families, distribute family kit tokens, and record medical checkups.",
            category = "CAMP_MANAGEMENT",
            district = "Chennai",
            location = "Ripon Building Ground Shelter",
            volunteersNeeded = 6,
            assignedVolunteers = emptyList(),
            isCompleted = false,
            priority = PriorityLevel.MEDIUM
        )
    )

    val initialMedicalRequests = listOf(
        MedicalRequest(
            id = "MED-01",
            requesterName = "Govindaraj",
            phone = "9840112233",
            patientCount = 1,
            symptoms = "Severe diabetic foot infection + fever, insulin stock exhausted due to flood isolation.",
            location = "Perumbakkam Slum Clearance Board, Block 14",
            transportRequired = true,
            priority = PriorityLevel.HIGH,
            status = RequestStatus.ASSIGNED
        ),
        MedicalRequest(
            id = "MED-02",
            requesterName = "Revathi S",
            phone = "9790554433",
            patientCount = 1,
            symptoms = "Pregnant woman (8.5 months) experiencing early contraction pains. Road blocked by water.",
            location = "Madippakkam 5th Main Road",
            transportRequired = true,
            priority = PriorityLevel.CRITICAL,
            status = RequestStatus.TEAM_DISPATCHED
        )
    )

    val safetyGuides = listOf(
        SafetyGuide(
            id = "GUIDE-FLOOD",
            titleEn = "Flood Safety Guidelines",
            titleTa = "வெள்ள அபாய பாதுகாப்பு வழிமுறைகள்",
            category = DisasterCategory.FLOODING,
            doListEn = listOf(
                "Switch off main electrical power switch immediately.",
                "Move to higher floors or high-elevation shelter before water enters.",
                "Boil drinking water or use water purification tablets.",
                "Keep emergency documents, medicines, and phone in waterproof bag.",
                "Listen to official announcements on battery radio or SDR app."
            ),
            doListTa = listOf(
                "முதலில் வீட்டின் முதன்மை மின் இணைப்பை (Main Switch) உடனடியாக துண்டிக்கவும்.",
                "நீர் புகும் முன் மேல் மாடிக்கு அல்லது உயரமான நிவாரண முகாமிற்குச் செல்லவும்.",
                "குடிநீரை நன்கு காய்ச்சி பருகவும் அல்லது சுத்திகரிப்பு மாத்திரைகளை பயன்படுத்தவும்.",
                "முக்கிய ஆவணங்கள் மற்றும் மருந்துகளை நீர்ப்புகா பைகளில் வைக்கவும்.",
                "அரசு மற்றும் SDR செயலியில் வரும் அதிகாரப்பூர்வ அறிவிப்புகளை கவனிக்கவும்."
            ),
            dontListEn = listOf(
                "Never walk, swim, or drive through flowing floodwaters (6 inches can sweep you off).",
                "Do not touch fallen electric wires or submerged switches.",
                "Avoid eating food that has come into contact with floodwater.",
                "Do not spread unverified rumors on social media."
            ),
            dontListTa = listOf(
                "ஓடும் வெள்ள நீரில் நடக்கவோ, நீந்தவோ அல்லது வாகனங்களை ஓட்டவோ வேண்டாம்.",
                "அறுந்து கிடக்கும் மின்கம்பிகள் அல்லது நீரில் மூழ்கிய சுவிட்சுகளை தொடாதீர்கள்.",
                "வெள்ள நீரில் நனைந்த உணவுப் பொருட்களை சாப்பிட வேண்டாம்.",
                "வதந்திகளை நம்பவோ அல்லது சமூக வலைதளங்களில் பகிரவோ வேண்டாம்."
            ),
            speechScriptEn = "Flood Emergency Advice: Switch off the main power switch immediately. Move to a higher floor. Do not walk through floodwaters. Dial 112 for critical emergency response.",
            speechScriptTa = "வெள்ள அபாய வழிமுறை: வீட்டின் மின்சார மெயின் சுவிட்சை உடனே அணைக்கவும். மேல் தளத்திற்குச் செல்லவும். வெள்ள நீரில் இறங்க வேண்டாம். அவசர உதவிக்கு 112-ஐ அழைக்கவும்."
        ),
        SafetyGuide(
            id = "GUIDE-CYCLONE",
            titleEn = "Cyclone & Gale Storm Safety",
            titleTa = "புயல் மற்றும் பலத்த காற்று பாதுகாப்பு",
            category = DisasterCategory.CYCLONE,
            doListEn = listOf(
                "Board up glass windows or tape them in 'X' shapes to prevent flying shards.",
                "Secure loose metal roofing sheets and trim hanging tree branches.",
                "Charge power banks, mobile phones, and keep battery torches handy.",
                "Store at least 3 days of dry rations and clean drinking water."
            ),
            doListTa = listOf(
                "கண்ணாடி ஜன்னல்களை மூடவும், உடைந்த கண்ணாடி சிதறாமல் தடுக்க 'X' வடிவில் டேப் ஒட்டவும்.",
                "தகர கூரைகளை பலமாக கட்டவும், மரக்கிளைகளை வெட்டவும்.",
                "மொபைல் போன், பவர் பேங்க், டார்ச் லைட்களை முழுமையாக சார்ஜ் செய்து வைக்கவும்.",
                "குறைந்தது 3 நாட்களுக்கான உலர் உணவு மற்றும் குடிநீரை சேமித்து வைக்கவும்."
            ),
            dontListEn = listOf(
                "Do not go outside during the 'eye of the storm' when winds suddenly calm.",
                "Do not park vehicles under large trees, billboards, or electric poles.",
                "Do not go near sea beaches, harbors, or coastal dykes."
            ),
            dontListTa = listOf(
                "புயலின் கண் பகுதி கடக்கும் போது காற்று நின்றால் வெளியே செல்ல வேண்டாம்.",
                "மரங்கள், விளம்பர பலகைகள் அல்லது மின்கம்பங்கள் கீழ் வாகனங்களை நிறுத்த வேண்டாம்.",
                "கடற்கரை, துறைமுகப் பகுதிகளுக்கு செல்ல வேண்டாம்."
            ),
            speechScriptEn = "Cyclone Safety Instructions: Stay indoors. Fasten loose roofing. Keep power banks charged. Avoid going near windows.",
            speechScriptTa = "புயல் பாதுகாப்பு: வீட்டிற்குள்ளேயே பாதுகாப்பாக இருக்கவும். தளர்வான கூரைகளை கட்டவும். மொபைலை சார்ஜ் செய்து வைக்கவும். ஜன்னல் அருகே செல்ல வேண்டாம்."
        ),
        SafetyGuide(
            id = "GUIDE-FIRE",
            titleEn = "Fire Incident Emergency Actions",
            titleTa = "தீ விபத்து அவசர நடவடிக்கைகள்",
            category = DisasterCategory.FIRE,
            doListEn = listOf(
                "Drop and crawl low under smoke where air is clearer and cooler.",
                "Feel door handles with the back of your hand before opening. If hot, do not open.",
                "Shout and alert everyone; evacuate immediately via stairs, not elevators.",
                "Call 101 or 112 immediately as soon as you reach a safe open area."
            ),
            doListTa = listOf(
                "புகை இருக்கும் போது தவழ்ந்து தரையோடு செல்லவும்; தரைப்பகுதியில் சுத்தமான காற்று இருக்கும்.",
                "கதவைத் திறக்கும் முன் கதவின் பின்புறத்தை கையால் தொட்டுப் பார்க்கவும்; சூடாக இருந்தால் திறக்காதீர்கள்.",
                "அனைவரையும் எச்சரித்து படிக்கட்டுகள் வழியாக வெளியேறவும்; மின்தூக்கியை (Lift) பயன்படுத்த வேண்டாம்.",
                "பாதுகாப்பான இடத்திற்கு வந்தவுடன் 101 அல்லது 112-ஐ உடனடியாக அழைக்கவும்."
            ),
            dontListEn = listOf(
                "Do not use elevators during a fire evacuation.",
                "Never go back inside a burning building for belongings.",
                "Do not throw water on electrical fires or oil fires; use sand or CO2 extinguisher."
            ),
            dontListTa = listOf(
                "தீ விபத்தின் போது லிப்ட்டை பயன்படுத்த வேண்டாம்.",
                "பொருட்களை எடுக்க எரியும் கட்டடத்திற்குள் மீண்டும் செல்ல வேண்டாம்.",
                "மின்சாரம் அல்லது எண்ணெய் தீயில் தண்ணீரை ஊற்ற வேண்டாம்; மணல் அல்லது தீயணைப்பானை பயன்படுத்தவும்."
            ),
            speechScriptEn = "Fire Emergency: Crawl under smoke. Evacuate using stairs, never the elevator. Once outside, stay out and call 101.",
            speechScriptTa = "தீ விபத்து: புகையின் கீழ் தவழ்ந்து செல்லவும். படிக்கட்டு வழியாக வெளியேறவும். வெளியே வந்ததும் 101-ஐ அழைக்கவும்."
        )
    )
}
