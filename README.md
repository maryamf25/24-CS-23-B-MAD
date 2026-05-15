<<<<<<< HEAD
# 🍽️ DineOut - Your Personal Food Diary


**DineOut** is a beautifully designed, native Android application built with Kotlin that helps you keep track of your culinary adventures. Whether it's a restaurant you want to try in the future or your absolute favorite local spot, DineOut acts as your personalized food diary.

---

## 🌟 Key Features

* **Track Restaurant Visits:** Keep a comprehensive list of all the restaurants you've visited along with the date, meal time, and occasion.
* **Wishlist Management:** Add places you want to visit in the future to your dedicated Wishlist so you never forget a recommendation.
* **Detailed Logs:** Keep track of:
  * Overall Restaurant Rating (Out of 5 stars)
  * Total amount spent (`Spend Amount`)
  * Value for Money Rating (`Worth Rating`)
  * Your personal notes and ambiance review
* **Individual Dish Tracking:** Rate not just the restaurant, but the specific dishes you had! Log dish names, course types (Appetizer, Main, Dessert), and whether you 'Would Order Again'.
* **Filtering System:** Easily find what you are looking for by filtering through your personal entries.
* **Local Persistent Storage:** Complete offline functionality. The app securely persists your data internally using file-system based JSON serialization so you don't lose any data when logging out or closing the app.

---

## 🛠️ Technology Stack

* **Language:** [Kotlin](https://kotlinlang.org/)
* **Architecture:** MVC / Single-Source-of-Truth Data Management
* **UI Components:** 
  * Material Design 3 (M3)
  * Custom Tab Layouts
  * Edge-to-Edge display
  * Modern Android Splash Screen API manipulation
* **Data Storage:** Custom JSON File handling on Internal App Storage (No external database required)
* **Build System:** Gradle (Kotlin DSL)

---

## 🚀 Getting Started

To run this project locally on your machine, follow these instructions:

### Prerequisites

* **Android Studio:** Jellyfish | 2023.3.1 (or newer recommended)
* **SDK:** Minimum SDK 24 (Android 7.0) | Target SDK 34 (Android 14)
* **JDK:** Java 17

### Installation

1. **Clone the repository** (if linked to GitHub) or download the source code:
   ```bash
   git clone https://github.com/maryamf25/24-CS-23-B-MAD.git
   ```
2. **Open the project in Android Studio:**
   * Launch Android Studio.
   * Click on **File > Open** and select the `DineOut` directory.
3. **Sync Gradle:** Let Android Studio download all required gradle dependencies.
4. **Build and Run:**
   * Connect an Android device via USB (with USB Debugging enabled) or start an AVD (Android Virtual Device).
   * Click the **Run 'app'** button (Shift + F10) in the top toolbar.

---

## 📂 Project Structure

```text
DineOut/
├── app/src/main/
│   ├── java/com/example/dineout/
│   │   ├── activities/     # Contains UI logic (MainActivity, AddEditActivity, etc.)
│   │   ├── adapters/       # RecyclerView adapters for the UI lists
│   │   ├── data/           # Contains DataManager for local JSON persistence
│   │   └── models/         # Data classes (Restaurant, Dish)
│   ├── res/                # XML layouts, drawables, fonts, and themes
│   └── AndroidManifest.xml # App configuration and permissions
└── build.gradle.kts        # App build configurations
```

---

## 📝 Usage Guide

1. **Viewing Data:** On launch, the `MainActivity` populates two tabs: *Visited* and *Wishlist*. It will automatically generate authentic sample data (including top Lahore spots like Haveli Restaurant and Cafe Aylanto) for first-time setup.
2. **Adding an Entry:** Tap the Floating Action Button to log a new food diary entry. Fill out the details, including standard fields and dish-specific remarks.
3. **Editing/Deleting:** Tap on any item in your list to view its `DetailActivity`, where you can further edit your review or seamlessly delete the record.

---

## 👤 Author

* Developed as part of the Mobile Application Development (MAD) Coursework.

---
*Created with ❤️ for food lovers.*
=======
# 📱 Mobile Application Development (MAD) - Assignments Repository

Welcome to our group's official repository for the Mobile Application Development course!
## 👥 Group Details

* **Group ID:** 24
* **Course:** Mobile Application Development (Semester 6)
* **Instructor:** Ma'am Rabeeya Saleem

### 👨‍💻 Team Members

| Sr. No. | Name | Reg Number |
| :---: | :--- | :--- |
| 1 | **Maryam Fatima** | 2023-CS-61 |
| 2 | **Arfa Amir** | 2023-CS-64 |

---

## 📂 Assignments & Projects Index

Below is the index of all our assignments. Click on the respective folder links to view the source code and details for each task:

* [x] **[Assignment 1: Number Multiplier & Discount Calculator](./Assignment-1)**
  * *Description:* A basic Android application that takes user input, multiplies it by 3, and calculates a 20% discount on a given price.
* [x] **[Assignment 2: BMI Calculator](./Assignment-2)**
  * *Description:* An application that takes the user's Name, Age, Height, and Weight to calculate their Body Mass Index (BMI). It utilizes multiple activities and Intents to pass data, dynamically changing the result text color based on the BMI category (Underweight, Normal, Overweight).
* [x] **[Assignment 3: Quiz App](./Assignment-3)**
  * *Description:* A multiple-choice quiz application. It allows users to answer a series of questions, tracks their score, and displays the final score along with the user's name on a result screen.

---

## 🛠️ Tech Stack & Tools Used

We are using the following tools and technologies to build our Android applications:
* **IDE:** Android Studio
* **Language:** Kotlin
* **UI Design:** XML (Empty Views Activity)
* **Version Control:** Git & GitHub

---
>>>>>>> 3c9e865dd9f64d8473826cf4068b741c537da835
