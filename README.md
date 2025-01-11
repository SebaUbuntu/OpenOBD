# OpenOBD

Open source ELM327 client application

## Features

- ELM327 connection
    - Bluetooth
    - TCP socket
    - Serial (TODO)
    - USB (TODO)
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
