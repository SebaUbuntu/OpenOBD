# OpenOBD

Open source ELM327 client application

## Supported OS

- Android
- iOS (untested, I don't have Xcode)
- Windows / Linux / macOS (with JVM)

## Features

- ELM327 connection
    - Bluetooth
    - Bluetooth LE (WIP)
    - TCP socket
    - Serial (TODO)
    - USB (TODO)
    - Demo device for testing
- Session information (ELM327 version, OBD protocol, etc.)
- OBD-II services
    - Get current data (01)
    - Get freeze frame data (02)
    - Get stored diagnostic trouble codes (03)
    - Clear diagnostic trouble codes (04) (TODO)
    - Get pending diagnostic trouble codes (07)
    - Get vehicle information (09)
    - Get permanent diagnostic trouble codes (0A)
- UDS services
    - TODO
- Car cluster-like dashboard with gauges
- Connection profile support
    - Custom DTC codes
    - Custom OBD-II PIDs (TODO)
    - Custom UDS DIDs (TODO)
- ELM327 terminal
- In-app log viewer

## Gradle modules

The app is split between several modules, mostly based on features or protocol:

- app: The Compose Multiplatform stuff (UI - VM - repositories + dependency injection)
- backend: I/O socket providers (USB, Bluetooth, etc.)
- core: Common code for all modules
- elm327: Code needed to talk with an ELM327 device
- logging: Logging library
- obd2: OBD-II services
- profiles: Non-standard profiles provider (Custom DTC definitions, etc.)
- storage: Application data storage
- uds: UDS services

Each platform is instead split into separate modules:

- androidApp: Android
- desktopApp: JVM
- iosApp: iOS
