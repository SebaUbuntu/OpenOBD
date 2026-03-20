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
    - Get current and freeze frame data (0x01, 0x02)
    - Get vehicle information (0x09)
    - Read stored, pending and permanent diagnostic trouble codes (0x03, 0x07, 0x0A)
    - Clear stored diagnostic trouble codes (0x04) (TODO)
- UDS services (TODO)
    - 0x10: DiagnosticSessionControl
    - 0x11: ECUReset
    - 0x14: ClearDiagnosticInformation
    - 0x19: ReadDTCInformation
    - 0x22: ReadDataByIdentifier
    - 0x23: ReadMemoryByAddress
    - 0x24: ReadScalingDataByIdentifier
    - 0x27: SecurityAccess
    - 0x28: CommunicationControl
    - 0x29: Authentication
    - 0x2A: ReadDataByPeriodicIdentifier
    - 0x2C: DynamicallyDefineDataIdentifier
    - 0x2E: WriteDataByIdentifier
    - 0x2F: InputOutputControlByIdentifier
    - 0x31: RoutineControl
    - 0x34: RequestDownload
    - 0x35: RequestUpload
    - 0x36: TransferData
    - 0x37: RequestTransferExit
    - 0x38: RequestFileTransfer
    - 0x3D: WriteMemoryByAddress
    - 0x3F: TesterPresent
    - 0x84: SecuredDataTransmission
    - 0x85: ControlDTCSetting
    - 0x86: ResponseOnEvent
    - 0x87: LinkControl
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
