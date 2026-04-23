# Dicoding Event App

A modern Android application designed to browse, manage, and track upcoming and past events. This project serves as a showcase for implementing high-performance, reactive UI and modular architecture in a native Android environment.

## 🚀 Tech Stack

The project is built using the latest industry-standard libraries and tools:

* **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) for a fully declarative and reactive user interface.
* **Navigation:** [Navigation 3 (Nav3)](https://developer.android.com/guide/navigation/navigation-kotlin-dsl) for type-safe and efficient screen transitions.
* **Networking:** [Ktor Client](https://ktor.io/docs/client-dependencies.html) for lightweight and asynchronous API requests.
* **Dependency Injection:** [Koin](https://insert-koin.io/) for pragmatic and developer-friendly DI.
* **Local Database:** [Room Persistence](https://developer.android.com/training/data-storage/room) for caching event data and managing favorites.
* **Image Loading:** [Coil](https://coil-kt.github.io/coil/) for fast and memory-efficient image processing.

## 🏗️ Architecture

This project follows the **MVI (Model-View-Intent)** architectural pattern to ensure a unidirectional data flow and predictable state management.


* **Model:** Represents the single source of truth (State) for the UI.
* **View:** Listens to state changes and renders the UI accordingly.
* **Intent:** Captures user actions and dispatches them to be processed, ensuring clear separation of concerns.

## ✨ Features

* **Event Listing:** Browse through upcoming and past events fetched from the Dicoding API.
* **Detailed View:** Comprehensive information about each event, including schedules and descriptions.
* **Search & Filtering:** Quickly find events based on specific keywords.
* **Local Persistence:** Access previously loaded events offline and save your favorite events via Room database.
* **Responsive Design:** Optimized for various screen sizes and orientations.

## 🛠️ Project Structure

```text
app/
├── data/
│   ├── local/          # Room DB, DAOs, and Entities
│   ├── remote/         # Ktor API Service and DTOs
│   └── repository/     # Data source coordination
├── di/                 # Koin modules configuration
├── ui/
│   ├── components/     # Reusable Compose widgets
│   ├── navigation/     # Nav3 route definitions
│   └── screen/         # UI Screens and MVI ViewModels
└── util/               # Extension functions and constants
```

## ⚙️ Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/ztacole/dicoding-event.git
   ```
2. Open the project in **Android Studio (Ladybug or newer)**.
3. Sync the project with Gradle files.
4. Run the application on an emulator or physical device.
