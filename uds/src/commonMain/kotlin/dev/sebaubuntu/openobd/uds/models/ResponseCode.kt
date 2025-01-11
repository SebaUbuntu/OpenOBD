/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.uds.models

enum class ResponseCode(val udsCode: UByte) {
    /**
     * General reject.
     */
    GENERAL_REJECT(0x10u),

    /**
     * Service not supported.
     */
    SERVICE_NOT_SUPPORTED(0x11u),

    /**
     * Subfunction not supported.
     */
    SUBFUNCTION_NOT_SUPPORTED(0x12u),

    /**
     * Incorrect message length or invalid format.
     */
    INVALID_FORMAT(0x13u),

    /**
     * Response too long.
     */
    RESPONSE_TOO_LONG(0x14u),

    /**
     * Busy, repeat request.
     */
    BUSY_REPEAT_REQUEST(0x21u),

    /**
     * Conditions not correct.
     */
    CONDITIONS_NOT_CORRECT(0x22u),

    /**
     * Request sequence error.
     */
    REQUEST_SEQUENCE_ERROR(0x24u),

    /**
     * No response from subnet component.
     */
    NO_RESPONSE_FROM_SUBNET_COMPONENT(0x25u),

    /**
     * Failure prevents execution of requested action.
     */
    FAILURE_PREVENT_EXECUTION_OF_REQUESTED_ACTION(0x26u),

    /**
     * Request out of range.
     */
    REQUEST_OUT_OF_RANGE(0x31u),

    /**
     * Security access denied.
     */
    SECURITY_ACCESS_DENIED(0x33u),

    /**
     * Authentication failed.
     */
    AUTHENTICATION_FAILED(0x34u),

    /**
     * Invalid key.
     */
    INVALID_KEY(0x35u),

    /**
     * Exceeded number of attempts.
     */
    EXCEEDED_NUMBER_OF_ATTEMPTS(0x36u),

    /**
     * Required time delay not expired.
     */
    REQUIRED_TIME_DELAY_NOT_EXPIRED(0x37u),

    /**
     * Secure data transmission required.
     */
    SECURE_DATA_TRANSMISSION_REQUIRED(0x38u),

    /**
     * Secure data transmission not allowed.
     */
    SECURE_DATA_TRANSMISSION_NOT_ALLOWED(0x39u),

    /**
     * Secure data verification failed.
     */
    SECURE_DATA_VERIFICATION_FAILED(0x3Au),

    /**
     * Certificate validation failed, invalid time period.
     */
    CERTIFICATE_VALIDATION_FAILED_INVALID_TIME_PERIOD(0x50u),

    /**
     * Certificate validation failed, invalid signature.
     */
    CERTIFICATE_VALIDATION_FAILED_INVALID_SIGNATURE(0x51u),

    /**
     * Certificate validation failed, invalid chain of trust.
     */
    CERTIFICATE_VALIDATION_FAILED_INVALID_CHAIN_OF_TRUST(0x52u),

    /**
     * Certificate validation failed, invalid type.
     */
    CERTIFICATE_VALIDATION_FAILED_INVALID_TYPE(0x53u),

    /**
     * Certificate validation failed, invalid format.
     */
    CERTIFICATE_VALIDATION_FAILED_INVALID_FORMAT(0x54u),

    /**
     * Certificate validation failed, invalid content.
     */
    CERTIFICATE_VALIDATION_FAILED_INVALID_CONTENT(0x55u),

    /**
     * Certificate validation failed, invalid scope.
     */
    CERTIFICATE_VALIDATION_FAILED_INVALID_SCOPE(0x56u),

    /**
     * Certificate validation failed, invalid certificate.
     */
    CERTIFICATE_VALIDATION_FAILED_INVALID_CERTIFICATE(0x57u),

    /**
     * Ownership verification failed.
     */
    OWNERSHIP_VERIFICATION_FAILED(0x58u),

    /**
     * Challenge calculation failed.
     */
    CHALLENGE_CALCULATION_FAILED(0x59u),

    /**
     * Setting access right failed.
     */
    SETTING_ACCESS_RIGHT_FAILED(0x5Au),

    /**
     * Session key creation/derivation failed.
     */
    SESSION_KEY_CREATION_DERIVATION_FAILED(0x5Bu),

    /**
     * Configuration data usage failed.
     */
    CONFIGURATION_DATA_USAGE_FAILED(0x5Cu),

    /**
     * Deauthentication failed.
     */
    DEAUTHENTICATION_FAILED(0x5Du),

    /**
     * Upload download not accepted.
     */
    UPLOAD_DOWNLOAD_NOT_ACCEPTED(0x70u),

    /**
     * Transfer data suspended.
     */
    TRANSFER_DATA_SUSPENDED(0x71u),

    /**
     * General programming failure.
     */
    GENERAL_PROGRAMMING_FAILURE(0x72u),

    /**
     * Wrong block sequence number.
     */
    WRONG_BLOCK_SEQUENCE_NUMBER(0x73u),

    /**
     * Request correctly received, response pending.
     */
    REQUEST_CORRECTLY_RECEIVED_RESPONSE_PENDING(0x78u),

    /**
     * Subfunction not supported in active session.
     */
    SUBFUNCTION_NOT_SUPPORTED_IN_ACTIVE_SESSION(0x7Eu),

    /**
     * Service not supported in active session.
     */
    SERVICE_NOT_SUPPORTED_IN_ACTIVE_SESSION(0x7Fu),

    /**
     * RPM too high.
     */
    RPM_TOO_HIGH(0x81u),

    /**
     * RPM too low.
     */
    RPM_TOO_LOW(0x82u),

    /**
     * Engine is running.
     */
    ENGINE_IS_RUNNING(0x83u),

    /**
     * Engine is not running.
     */
    ENGINE_IS_NOT_RUNNING(0x84u),

    /**
     * Engine run time too low.
     */
    ENGINE_RUN_TIME_TOO_LOW(0x85u),

    /**
     * Temperature too high.
     */
    TEMPERATURE_TOO_HIGH(0x86u),

    /**
     * Temperature too low.
     */
    TEMPERATURE_TOO_LOW(0x87u),

    /**
     * Vehicle speed too high.
     */
    VEHICLE_SPEED_TOO_HIGH(0x88u),

    /**
     * Vehicle speed too low.
     */
    VEHICLE_SPEED_TOO_LOW(0x89u),

    /**
     * Throttle/pedal too high.
     */
    THROTTLE_PEDAL_TOO_HIGH(0x8Au),

    /**
     * Throttle/pedal too low.
     */
    THROTTLE_PEDAL_TOO_LOW(0x8Bu),

    /**
     * Transmission range not in neutral.
     */
    TRANSMISSION_RANGE_NOT_IN_NEUTRAL(0x8Cu),

    /**
     * Transmission range not in gear.
     */
    TRANSMISSION_RANGE_NOT_IN_GEAR(0x8Du),

    /**
     * Brake switch not closed.
     */
    BRAKE_SWITCH_NOT_CLOSED(0x8Fu),

    /**
     * Shifter lever not in park.
     */
    SHIFTER_LEVER_NOT_IN_PARK(0x90u),

    /**
     * Torque converter clutch locked.
     */
    TORQUE_CONVERTER_CLUTCH_LOCKED(0x91u),

    /**
     * Voltage too high.
     */
    VOLTAGE_TOO_HIGH(0x92u),

    /**
     * Voltage too low.
     */
    VOLTAGE_TOO_LOW(0x93u),

    /**
     * Resource temporary unavailable.
     */
    RESOURCE_TEMPORARY_UNAVAILABLE(0x94u);

    companion object {
        fun fromUdsCode(udsCode: UByte) = entries.firstOrNull {
            it.udsCode == udsCode
        }
    }
}
