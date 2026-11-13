# YechimUZ Android Mobile Application

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin%202.0.21-blue.svg)](https://kotlinlang.org)
[![UI Toolkit](https://img.shields.io/badge/UI-Jetpack%20Compose-purple.svg)](https://developer.android.com/jetpack/compose)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-orange.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)]()

> **YechimUZ** — Muammo egalari (Problem Owners) va Yechim taklif qiluvchi ekspert/dasturchilarni (Solvers) bog‘lovchi intellektual ekotizimning rasmiy Android mobil ilovasi.

---

## 🏛️ Arxitektura va Texnologik To‘plam (Tech Stack)

Ushbu ilova **Google Android Architecture Guidelines** va **Clean Architecture** tamoyillari asosida to‘liq zamonaviy Native Kotlin tilida ishlab chiqilgan.

| Qatlam / Komponent | Texnologiya | Tavsif |
| :--- | :--- | :--- |
| **Dasturlash tili** | Kotlin 2.0.21 | Zamonaviy va xavfsiz mobil dasturlash tili |
| **Foydalanuvchi interfeysi** | Jetpack Compose (BOM 2024.10.01) | Deklarativ reaktiv UI, Material 3 dizayn tizimi |
| **SDK talablari** | `minSdk: 26`, `compileSdk: 36`, `targetSdk: 36` | Android 8.0 dan Android 15+ gacha to‘liq moslik |
| **Build tizimi** | Gradle 8.11.1 + AGP 8.9.1 | Gradle Wrapper (`./gradlew`) orqali har qanday muhitda mustaqil build |
| **Tarmoq (Networking)** | Retrofit 2.11.0 + OkHttp 4.12.0 | REST API mijoz, Gson konverter, avtomatik sessiya CookieJar |
| **Xavfsizlik** | Hardware-backed Android Keystore (AES-256-GCM) | Token va sessiya ma'lumotlarini apparat darajasida shifrlash |
| **Sinxronizatsiya** | 100% Strict Online Mode | Ma'lumotlar to‘g‘ridan-to‘g‘ri Vercel/Neon DB bilan sinxron, oflayn chalkashliklarsiz |
| **Real-time Chat** | 4-second Lifecycle-aware Polling | 1:1 xavfsiz chat, xabarlarni yuborish va o‘qish holati |
| **Hududlar filtri** | 14 ta O‘zbekiston viloyatlari chiplari | Respublika bo‘ylab muammolarni qulay saralash |
| **Google Play Moslik** | UGC Report/Block, Account Deletion | Play Store xavfsizlik va ma'lumotlar maxfiyligi talablariga 100% mos |

---

## 🌐 Markaziy Backend API Bog‘lanishi

Android ilova to‘g‘ridan-to‘g‘ri ma'lumotlar bazasiga (PostgreSQL) TCP orqali ulanmaydi. Barcha amallar HTTPS REST protokoli orqali mavjud ishlab turgan Backend API bilan amalga oshiriladi:

- **API Bazaviy Manzil:** `https://yechimuz-lovat.vercel.app/api/v1`
- **Autentifikatsiya:** `yechimuz_token` sessiya cookie'si va `Authorization: Bearer <token>` sarlavhalari avtomatik inyeksiyasi orqali ta'minlanadi.

---

## 📂 Loyiha Tuzilmasi (Folder Structure)

```text
startap apk/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml          # Ilova ruxsatlari va deklaratsiyalari
│   │       ├── java/uz/yechim/app/
│   │       │   ├── YechimUzApp.kt           # Application klassi
│   │       │   ├── MainActivity.kt          # Asosiy faoliyat, Onlayn tarmoq nazorati
│   │       │   ├── core/
│   │       │   │   ├── network/             # RetrofitClient, SessionCookieJar
│   │       │   │   ├── security/            # KeystoreManager (AES-256-GCM)
│   │       │   │   └── theme/               # YechimUzTheme, Color, Type
│   │       │   ├── data/
│   │       │   │   ├── model/               # DTO modellar (Problem, Solution, Chat, User)
│   │       │   │   └── remote/              # ApiService Retrofit interfeysi
│   │       │   └── presentation/
│   │       │       ├── auth/                # Login va Ro'yxatdan o'tish ekranlari
│   │       │       ├── home/                # Asosiy dashboard, AI tavsiyalar, Statistika
│   │       │       ├── problems/            # 14 viloyat filtri bilan Muammolar bozori
│   │       │       ├── ai/                  # AI orqali muammo/challenge yaratish
│   │       │       ├── solutions/           # Yechimlar matritsasi va g'olibni tanlash
│   │       │       ├── chat/                # 1:1 xavfsiz chat va UGC shikoyat/bloklash
│   │       │       ├── profile/             # Foydalanuvchi profili, Akkauntni o'chirish
│   │       │       └── navigation/          # AppNavGraph, BottomNavigationBar
│   │       └── res/                         # XML resurslar, ranglar, mavzular, backup qoidalari
│   ├── build.gradle.kts                     # App moduli sozlamalari
│   └── proguard-rules.pro                   # R8 / ProGuard qoidalari
├── gradle/wrapper/                          # Gradle 8.11.1 Wrapper binarlari
├── build.gradle.kts                         # Loyiha darajasidagi build skripti
├── settings.gradle.kts                      # Modullar va repozitoriyalar konfiguratsiyasi
├── gradle.properties                        # JVM xotira va AndroidX sozlamalari
├── gradlew & gradlew.bat                    # Gradle ishga tushiruvchi CLI skriptlari
└── README.md                                # Ushbu yo'riqnoma
```

---

## 🚀 Ilovani Boshqa Dasturlash Muhitida (Android Studio) Ishga Tushirish Yo‘riqnomasi

Ushbu loyiha **Portable** formatda yaratilgan bo‘lib, uni istalgan kompyuterda quyidagi oddiy qadamlar bilan ochish va ishga tushirish mumkin:

### 1. Talablar
- **Android Studio:** Ladybug (2024.2.1+) yoki undan yangi versiya tavsiya etiladi.
- **JDK:** Java 17 LTS yoki Java 21 (Android Studio bilan o‘rnatilgan JBR ishlatiladi).
- **Android SDK:** API 26 dan API 36 gacha o‘rnatilgan bo‘lishi lozim.

### 2. Android Studio'da ochish
1. **Android Studio** dasturini ishga tushiring.
2. Boshlang‘ich oynada **Open** tugmasini bosing.
3. `startap apk` papkasini tanlang va **OK** tugmasini bosing.
4. Android Studio loyihani avtomatik aniqlaydi va **Gradle Sync** jarayonini boshlaydi.
5. Internet aloqasi mavjud bo‘lganda kerakli barcha Android kutubxonalari avtomatik yuklanadi.

### 3. Ilovani Run qilish
1. Yuqori paneldagi qurilmalar ro‘yxatidan **Android Emulator** (masalan, Pixel 8 - API 34 yoki API 35) yoki USB orqali ulangan **real Android telefonni** tanlang.
2. Yashil **Run ('app')** tugmasini bosing (yoki `Shift + F10`).
3. Dastur kompilyatsiya qilinadi va qurilmaga o‘rnatilib, darhol ishga tushadi.

### 4. Terminal orqali APK yig‘ish (CLI)
Agarda siz terminaldan foydalanmoqchi bo‘lsangiz:

```powershell
# Debug APK yaratish:
.\gradlew.bat assembleDebug

# Natijaviy APK manzili:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔒 Xavfsizlik va Google Play Siyosati
- **Sessiya Xavfsizligi:** Tokenlar va sessiya kalitlari hech qachon ochiq matn (plain-text) yoki oddiy `SharedPreferences`da saqlanmaydi. Ular `AndroidKeyStore`ning AES-256-GCM apparat darajasidagi kalitlari bilan shifrlanadi.
- **Bulutli Nusxalash Himoyasi:** `data_extraction_rules.xml` va `backup_rules.xml` orqali shaxsiy kalitlar va tokenlarning Google Drive avtomatik backupiga tushib qolishi qat'iyan taqiqlangan.
- **UGC Kontent Himoyasi:** Chat va yechimlarda nomaqbul xabarlar va foydalanuvchilar ustidan "Shikoyat qilish (Report)" va "Foydalanuvchini bloklash (Block)" funksiyalari to‘liq joriy qilingan.
- **Akkauntni O‘chirish:** Profil ekranida Google Play talabiga asosan foydalanuvchi hisobini va unga tegishli barcha ma'lumotlarni o‘chirish imkoniyati yaratilgan.
