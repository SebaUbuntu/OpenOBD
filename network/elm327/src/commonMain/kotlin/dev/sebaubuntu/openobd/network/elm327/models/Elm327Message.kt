/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.network.elm327.models

/**
 * ELM327 messages and alerts.
 */
sealed interface Elm327Message {
    sealed interface Alert : Elm327Message {
        /**
         * Prompt character used to indicate that the ELM327 is waiting for input.
         */
        data object Prompt : Alert

        /**
         * This message occurs as a warning that there has been no RS232 or OBD activity for some time
         * (see the Power Control section for details). If allowed, the IC will be initiating a switch
         * to the Low Power (standby) mode of operation. If this was initiated by no RS232 activity,
         * sending something within the next minute will stop the switch to low power. Note that the '!'
         * before ACT ALERT is printed if PP 0F bit 1 is 1.
         */
        data class ActAlert(
            val lowPowerDisabled: Boolean,
        ) : Alert

        /**
         * This appears as a warning that the ELM327 is about to switch to the Low Power (standby) mode
         * of operation in 2 seconds time. This delay is provided to allow an external controller enough
         * time to prepare for the change in state. No inputs or voltages on pins can stop this action
         * once initiated. Note that the '!' before LP ALERT is printed if PP 0F bit 1 is 1.
         */
        data class LowPowerAlert(
            val lowPowerDisabled: Boolean,
        ) : Alert

        /**
         * The ELM327 continually monitors the 5V supply to ensure that it is within acceptable limits.
         * If the voltage should go below the low limit, a 'brownout reset' circuit is activated, and
         * the IC stops all activity. In rare cases, a sudden large change in VDD can also trigger a low
         * voltage reset. When the voltage returns to normal, the ELM327 performs a full reset, and then
         * prints LV RESET. Note that this type of reset is exactly the same as an AT Z or MCLR reset
         * (but it does not print ELM327 v2.3). An LV RESET will also block automatic searches through
         * the CAN protocols, if bit 4 of PP 2A is a '1' (it is by default). This is done because most
         * LV RESETs seem to occur as a result of CAN wiring problems (the transceiver is capable of
         * passing very large currents). Blocking of the CAN protocols is only done until the next reset
         * (AT Z, WS, etc.) or until an AT FE is sent.
         */
        data object LowVoltageReset : Alert

        /**
         * The ELM327 is searching for ECUs responding to the message.
         */
        data object SearchingEcus : Alert

        /**
         * If any OBD operation is interrupted by a received RS232 character, or by a low level on the
         * RTS pin, the ELM327 will print the word STOPPED. If you should see this response, then
         * something that you have done has interrupted the ELM327. Most people see it because they have
         * not waited for pin 15 to go high, or for the prompt character ('>') to be displayed before
         * starting to send the next command. Note that short duration pulses on pin 15 may cause the
         * STOPPED message to be displayed, but may not be of sufficient duration to cause a switch to
         * Low Power operation.
         */
        data object Stopped : Alert

        companion object {
            fun from(value: String) = when (value.filterNot(Char::isWhitespace)) {
                ">" -> Prompt
                "ACTALERT" -> ActAlert(false)
                "!ACTALERT" -> ActAlert(true)
                "LPALERT" -> LowPowerAlert(false)
                "!LPALERT" -> LowPowerAlert(true)
                "LVRESET" -> LowVoltageReset
                "SEARCHING..." -> SearchingEcus
                "STOPPED" -> Stopped
                else -> null
            }
        }
    }

    sealed interface Error : Elm327Message {
        /**
         * This is the standard response for a misunderstood command received on the RS232 input.
         * Usually it is due to a typing mistake, but it can also occur if you try to do something that
         * is not appropriate (e.g. trying to do an AT FI command if you are not set for protocol 5).
         */
        data object UnknownCommand : Error

        /**
         * The ELM327 provides a 512 byte internal RS232 transmit buffer so that OBD messages can be
         * received quickly, stored, and sent to the computer at a more constant rate. Occasionally
         * (particularly with CAN systems) the buffer will fill at a faster rate than it is being
         * emptied by the PC. Eventually it may become full, and no more data can be stored (it is
         * lost). If you are receiving BUFFER FULL messages, and you are using a lower baud data rate,
         * give serious consideration to changing your data rate to something higher. If you still
         * receive BUFFER FULL messages after that, you might consider turning the headers and maybe the
         * spaces off (with AT H0, and AT S0), or using the CAN filtering commands (AT CRA, AT FT, or AT
         * CM and AT CF) to reduce the amount of data being sent.
         */
        data object BufferFull : Error

        /**
         * This occurs when the ELM327 tries to send a message, or to initialize the bus, and detects
         * too much activity to do so (it needs a pause in activity in order to insert the message).
         * Although this could be because the bus was in fact very busy, it is almost always due to a
         * wiring problem that is giving a continuously active input. If this is an initial trial with
         * your new ELM327 circuit, then check all the voltage levels at the offending OBD input, as
         * this error is very likely due to a wiring problem (see our
         * 'AN02 - ELM327 Circuit Construction' for some typical voltages).
         */
        data object BusBusy : Error

        /**
         * A generic problem has occurred. This is most often from an invalid signal being detected on
         * the bus (for example, a pulse that is longer than a valid Break signal), but usually is from
         * a wiring error. Note that some vehicles may generate long pulses as part of their startup
         * process, so you may see this message as part of a normal vehicle startup while
         * 'monitoring all'.
         */
        data object BusError : Error

        /**
         * Bus init error.
         */
        data object BusInitError : Error

        /**
         * A generic problem has occurred. This is most often from an invalid signal being detected on
         * the bus (for example, a pulse that is longer than a valid Break signal), but usually is from
         * a wiring error. Note that some vehicles may generate long pulses as part of their startup
         * process, so you may see this message as part of a normal vehicle startup while
         * 'monitoring all'.
         */
        data object CanError : Error

        /**
         * There was a response from the vehicle, but the information was incorrect or could not be
         * recovered.
         */
        data object DataError : Error

        /**
         * There was an error in the line that this points to, either from an incorrect checksum, or a
         * problem with the format of the message (the ELM327 still shows you what it received). There
         * could have been a noise burst which interfered, possibly a circuit problem, or perhaps you
         * have the CAN Auto Formatting (CAF) on, and you are looking at a system that is not of the ISO
         * 15765-4 format. Try resending the command again – if it was a noise burst, it may be received
         * correctly the second time.
         */
        data object RxDataError : Error

        /**
         * There are a number of internal errors that might be reported as ERR with a two-digit code
         * following. These occur if an internally monitored parameter is found to be out of limits, or
         * if a module is not responding correctly. If you witness one of these, contact Elm Electronics
         * for advice. One error that is not necessarily a result of an internal problem is ERR94. This
         * code represents a 'fatal CAN error', and may be seen if there are CAN network issues (some
         * non-CAN vehicles may use pins 6 and 14 of the connector for other functions, and this may
         * cause problems). If you see an ERR94, it means that the CAN module was not able to reset
         * itself, and needed a complete IC reset to do so. You will need to restore any settings that
         * you had previously made, as they will have been returned to their default values. Beginning
         * with v1.3a of this IC, an ERR94 will also block further automatic searches through the CAN
         * protocols, if bit 5 of PP 2A is a '1' (it is by default). This is done because most ERR94s
         * will be as a result of serious CAN wiring problems. Blocking of the CAN protocols remains in
         * effect until the next power off and on, or until an AT FE is sent.
         */
        data class InternalError(val code: UByte) : Error

        /**
         * When an OBD output is energized, a check is always made to ensure that the signal also
         * appears at the respective input. If there is a problem, and no active input is detected, the
         * IC turns the output off and declares that there was a problem with the FeedBack (FB) of the
         * signal. If this is an initial trial with your ELM327, this is almost certainly a wiring
         * problem. Check your wiring before proceeding.
         */
        data object FeedBackError : Error

        /**
         * The IC waited for the period of time that was set by AT ST, and detected no response from the
         * vehicle. It may be that the vehicle had no data to offer for that particular PID, that the
         * mode requested was not supported, that the vehicle was attending to higher priority issues,
         * or in the case of the CAN systems, the filter may have been set so that the response was
         * ignored, even though one was sent. If you are certain that there should have been a response,
         * try increasing the ST time (to be sure that you have allowed enough time for the ECU to
         * respond), or restoring the CAN filter to its default setting.
         */
        data object NoData : Error

        /**
         * An error was detected in the received CAN data. This most often occurs if monitoring a CAN
         * bus using an incorrect baud rate setting, but it may occur if monitoring and there are
         * messages found that are not being acknowledged, or that contain bit errors. The entire
         * message will be displayed as it was received (if you have filters set, the received message
         * may not agree with the filter setting). Try a different protocol, or a different baud rate.
         */
        data object RxError : Error

        /**
         * If you see this message, it means that the ELM327 has tried all the available protocols, and
         * could not detect a compatible one. This could be because your vehicle uses an unsupported
         * protocol, or could be as simple as forgetting to turn the ignition key on. If you are sure
         * that your vehicle uses an OBDII protocol, then check all of your connections, and the
         * ignition, then try the command again.
         */
        data object UnableToConnect : Error

        companion object {
            fun from(value: String) = when (val value = value.filterNot(Char::isWhitespace)) {
                "?" -> UnknownCommand
                "BUFFERFULL" -> BufferFull
                "BUSBUSY" -> BusBusy
                "BUSERROR" -> BusError
                "BUSINIT...ERROR" -> BusInitError
                "CANERROR" -> CanError
                "DATAERROR" -> DataError
                "<DATAERROR" -> RxDataError
                "FBERROR" -> FeedBackError
                "NODATA" -> NoData
                "<RXERROR" -> RxError
                "UNABLETOCONNECT" -> UnableToConnect
                else -> when {
                    value.startsWith("ERR") -> InternalError(
                        value.removePrefix("ERR").toUByte()
                    )

                    else -> null
                }
            }
        }
    }

    companion object {
        /**
         * Get a [Elm327Message] from a response string.
         *
         * @param value The response string
         * @return The [Elm327Message], or null if no match is found
         */
        fun from(value: String) = Alert.from(value) ?: Error.from(value)
    }
}
