# Protocols

Here are described the various network layers involved with OBD-II and UDS

DoIP is currently out of scope for this project (given the usage of an ELM327 adapter to
communicate with the car), but layers isolation will be attempted to ease up a later integration

## UDS

| ISO/OSI layer         | Technology name      | Standards                |
|-----------------------|----------------------|--------------------------|
| Level 7: Application  | WWH-OBD on UDS       | ISO 27145-2, ISO 14229-1 |
| Level 6: Presentation | WWH-OBD on UDS       | ISO 27145-2, ISO 14229-1 |
| Level 5: Session      | UDS on CAN           | ISO 14229-2              |
| Level 4: Transport    | ISO-TP               | ISO 15765-2              |
| Level 3: Network      | ISO-TP               | ISO 15765-2              |
| Level 2: Data link    | CAN (through ELM327) |                          |
| Level 1: Physical     | CAN (through ELM327) |                          |

## OBD-II

| ISO/OSI layer         | Technology name      | Standards                |
|-----------------------|----------------------|--------------------------|
| Level 7: Application  | OBD-II               | ISO 27145-2, ISO 14229-1 |
| Level 6: Presentation | OBD-II               | ISO 27145-2, ISO 14229-1 |
| Level 5: Session      | OBD-II               | ISO 14229-2              |
| Level 4: Transport    | ISO-TP               | ISO 15765-2              |
| Level 3: Network      | ISO-TP               | ISO 15765-2              |
| Level 2: Data link    | CAN (through ELM327) |                          |
| Level 1: Physical     | CAN (through ELM327) |                          |

[Source](https://uds.readthedocs.io/en/stable/pages/knowledge_base/osi_model.html#uds-standards)
