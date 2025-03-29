# KalagatoTask
A task management Android application built with Kotlin, Jetpack Compose, and MVVM architecture.

## Project Structure

The project follows a modular architecture to ensure clean and maintainable code.

### 1. Data Layer (`data/`)
Handles data storage and retrieval.

- `Task.kt` - Data model for tasks.
- `TaskDao.kt` - Data Access Object (DAO) interface for database operations.
- `TaskDatabase.kt` - Room Database configuration.
- `TaskRepository.kt` - Repository pattern for handling data operations.

### 2. UI Theme (`ui/theme/`)
Defines the application's styling and appearance.

- `Color.kt` - App color definitions.
- `Theme.kt` - Compose theme configuration.
- `Type.kt` - Typography settings.

### 3. Utilities (`utils/`)
Helper classes for various functionalities.

- `Preferences.kt` - Manages app preferences.
- `TransitionUtils.kt` - Utility functions for screen transitions.

### 4. View Layer (`view/`)
Contains UI components and screens.

#### Common UI Components (`common/`)
Reusable UI elements.

- `Actions.kt` - Defines common actions.
- `DraggableItem.kt` - Implements draggable UI components.
- `NoData.kt` - Displays a "No Data" placeholder.
- `PullToRefresh.kt` - Pull-to-refresh functionality.
- `RadioGroup.kt` - Custom radio group component.
- `ShimmerEffect.kt` - Shimmer loading effect.

#### Screens (`screens/`)
Screens for different app functionalities.

- `AddTaskScreen.kt` - Screen to add new tasks.
- `SettingsScreen.kt` - Application settings.
- `TaskDetailsScreen.kt` - Displays task details.
- `TaskListScreen.kt` - Displays the list of tasks.

### 5. ViewModel Layer (`viewmodel/`)
Manages UI-related data and business logic.

- `TaskViewModel.kt` - ViewModel for task management. (I added a 2-second delay before loading the task list to simulate a shimmer loading effect.)

### 6. Main Application Files
- `MainActivity.kt` - Entry point of the application.
- `TaskManagerApp.kt` - Application class.

## Features
- Task creation, updating, and deletion.
- Dark mode support.
- Smooth UI transitions.
- Room Database for offline support.

## Tech Stack
- Kotlin - Main programming language.
- Jetpack Compose - UI framework.
- Room Database - Local database.
- StateFlow & MutableStateFlow - State management.
- MVVM Architecture - Clean code structure.

## Android Studio Version
```text
Android Studio Meerkat | 2024.3.1 Patch 1
Build #AI-243.24978.46.2431.13208083, built on March 13, 2025
Runtime version: 21.0.5+-13047016-b750.29 aarch64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
Toolkit: sun.lwawt.macosx.LWCToolkit
macOS 15.3.2
Kotlin plugin: K2 mode
GC: G1 Young Generation, G1 Concurrent GC, G1 Old Generation
Memory: 2048M
Cores: 8
Metal Rendering is ON
Registry:
  ide.instant.shutdown=false
  debugger.new.tool.window.layout=true
  ide.experimental.ui=true
  ide.images.show.chessboard=true
  terminal.new.ui=true
Non-Bundled Plugins:
  com.andreibacalu.plugin.stop_build (1.1)
  com.rhyme.flutter.plugin.jumptoassets (1.4)
  Quick Notes (3.3)
  com.ankit.mahadik.json.dart.class (2.18)
  com.github.izhangzhihao.intellijgooglesearch (0.0.2)
  Dart (243.23654.44)
  wu.seal.tool.jsontokotlin (3.7.6)
  GsonOrXmlFormat (2.0)
  com.jetbrains.kmm (0.8.5(243)-7)
  com.developerphil.adbidea (1.6.19)
  io.flutter (83.0.4)
  com.ruiyu.ruiyu (5.2.4)
```

## License
This project is licensed under the MIT License.
