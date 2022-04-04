$<PLATFORMIO_INI>
; * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
; PlatformIO Project Configuration file for GUIslice
; - https://github.com/ImpulseAdventure/GUIslice
;
; - This file will be used to select the MCU, Display
;   and Touch drivers to use with GUIslice
;
; * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

; =========================================================
; Section 1: PlatformIO GENERAL SETTINGS
; =========================================================

[platformio]

; Extra config:
; - PlatformIO config to build from project root
;lib_dir = .

; Default environments to load
default_envs =
$<DEFAULT_ENV>

[env]
; GUIslice compilation more straightforward in "deep" LDF mode
lib_ldf_mode = deep
; To enable inspect/cppcheck w/o LTO1 error, disable LTO
build_unflags =
;	-flto


; ---------------------------------------------------------
[common]
; Define any additional compilation flags here
build_flags =
;	-Wall
;	-Wextra
;	-Wunused-variable

; =========================================================
; Section 2: UPLOAD SETTINGS
; =========================================================

[upload-avrisp]
; Board default is:
; - upload_protocol = arduino
upload_protocol = stk500v2
upload_flags = 
	-Pusb

; =========================================================
; Section 3: DISPLAY DRIVERS
; =========================================================

; ---------------------------------------------------------
; Display: Adafruit/Adafruit_ILI9341
; ---------------------------------------------------------
; NOTE:
; - Need to use lib_ldf_mode=deep otherwise the Adafruit
;   libraries report "no such file" compilation errors on
;   "Adafruit_I2CDevice.h"
[disp-ILI9341]
lib_deps =
	SPI
	Wire
	adafruit/Adafruit BusIO
	adafruit/Adafruit GFX Library
	adafruit/Adafruit ILI9341
build_flags =

[disp-HX8357]
lib_deps =
	SPI
	Wire
	adafruit/Adafruit BusIO
	adafruit/Adafruit GFX Library
	adafruit/Adafruit HX8347
build_flags =

; ---------------------------------------------------------
; Display: prenticedavid/MCUFRIEND_kbv
; ---------------------------------------------------------
[disp-mcufriend]
lib_deps =
	Wire
	adafruit/Adafruit BusIO
	adafruit/Adafruit GFX Library
	prenticedavid/MCUFRIEND_kbv
build_flags =

; ---------------------------------------------------------
; Display: ILI9341_t3
; ---------------------------------------------------------
[disp-ILI9341_t3]
; NOTE (lib_deps):
; - Need to specify the github sources otherwise PIO
;   compilation reports "no such file" on SPI.h
lib_deps =
	https://github.com/PaulStoffregen/Wire
    https://github.com/PaulStoffregen/SPI
    https://github.com/PaulStoffregen/ILI9341_t3
build_flags =

; ---------------------------------------------------------
; Display: bodmer/TFT_eSPI
; ---------------------------------------------------------
[disp-TFT_eSPI-ILI9341]
lib_deps =
	TFT_eSPI
; if you define -DGSLC_SPIFFS_EN=1 you must include these
; other libraries
;  TFT_eFEX
;  JPEGDecoder

; TFT_eSPI display configuration
; - Example from: Setup1_ILI9341-cal-esp32-feather.h
;   Adafruit Feather ESP32 + Adafruit 2.4" Featherwing TFT
;
; - If using TFT_eSPI, configure to match the settings in the
;   User Setup included within TFT_eSPI/User_Setup_Select.h
; - You must use "-DUSER_SETUP_LOADED=1" here to disable the
;   default config files within TFT_eSPI from being used.
build_flags =
	-DUSER_SETUP_LOADED=1
	-DILI9341_DRIVER
	-DTFT_CS=15
	-DTFT_DC=33
	-DTFT_RST=-1
	-DTFT_MOSI=18
	-DTFT_MISO=19
	-DTFT_SCLK=5
	-DLOAD_GLCD
	-DLOAD_GFXFF
	-DSMOOTH_FONT
	-DSPI_FREQUENCY=40000000
	-DSPI_READ_FREQUENCY=20000000
	-DSPI_TOUCH_FREQUENCY=2500000
	-DSUPPORT_TRANSACTIONS

[disp-TFT_eSPI-HX8357D]
lib_deps =
	TFT_eSPI
; if you define -DGSLC_SPIFFS_EN=1 you must include these
; other libraries
;  TFT_eFEX
;  JPEGDecoder

; TFT_eSPI display configuration
; - Example from: Setup1_ILI9341-cal-esp32-feather.h
;   Adafruit Feather ESP32 + Adafruit 2.4" Featherwing TFT
;
; - If using TFT_eSPI, configure to match the settings in the
;   User Setup included within TFT_eSPI/User_Setup_Select.h
; - You must use "-DUSER_SETUP_LOADED=1" here to disable the
;   default config files within TFT_eSPI from being used.
build_flags =
	-DUSER_SETUP_LOADED=1
	-DHX8357D_DRIVER
	-DTFT_CS=15
	-DTFT_DC=33
	-DTFT_MOSI=18
	-DTFT_MISO=19
	-DTFT_SCLK=5
	-DLOAD_GLCD
	-DLOAD_GFXFF
	-DSMOOTH_FONT
	-DSPI_FREQUENCY=40000000
	-DSPI_READ_FREQUENCY=20000000
	-DSPI_TOUCH_FREQUENCY=2500000
	-DSUPPORT_TRANSACTIONS

; ---------------------------------------------------------
; Display: M5stack/m5stack
; ---------------------------------------------------------
[disp-M5Stack]
lib_deps =
	m5stack/M5Stack
build_flags =

; =========================================================
; Section 4: TOUCH DRIVERS
; =========================================================

; ---------------------------------------------------------
; Touch: Adafruit/Adafruit_STMPE610
; ---------------------------------------------------------
[touch-STMPE610]
lib_deps =
	Wire
	adafruit/Adafruit STMPE610
build_flags =

; ---------------------------------------------------------
; Touch: Adafruit/Adafruit_FT6206
; ---------------------------------------------------------
[touch-FT6206]
lib_deps =
	Wire
	adafruit/Adafruit FT6206 Library
build_flags =

; ---------------------------------------------------------
; Touch: Adafruit/Adafruit_Touchscreen (4wire)
; ---------------------------------------------------------
[touch-4wire]
lib_deps =
	adafruit/Adafruit TouchScreen
build_flags =

; ---------------------------------------------------------
; Touch: PaulStoffregen/XPT2046_Touchscreen
; ---------------------------------------------------------
[touch-XPT2046_ps]
lib_deps =
	PaulStoffregen/XPT2046_Touchscreen
build_flags =


; ---------------------------------------------------------
; Touch: None
; ---------------------------------------------------------
[touch-none]
lib_deps =
build_flags =


; =========================================================
; Section 5: MCU Settings
;
; - For additional settings, please refer to:
;   https://docs.platformio.org/en/latest/boards/index.html
;
; =========================================================

; ---------------------------------------------------------
; MCU: "Arduino"
; - Includes:
;   - Atmel AVR
;     - Arduino UNO (board=uno)
;   - Atmel megaAVR
;     - ATmega2560 (board=ATmega2560)
;   - Atmel SAM
;     - Adafruit Grand Central M4 (board=adafruit_grandcentral_m4)
;     - Arduino Due (board=due)
;     - Arduino Zero (board=zero
;   - etc.
; ---------------------------------------------------------
[mcu-Arduino]
platform = atmelavr
board = ATmega2560
framework = arduino
extends = upload-avrisp

; ---------------------------------------------------------
; MCU: ESP32
; - Includes:
;   - Adafruit ESP32 Feather (board=featheresp32)
;   - etc.
; ---------------------------------------------------------
[mcu-ESP32]
platform = espressif32
board = featheresp32
framework = arduino

; ---------------------------------------------------------
; MCU: M5Stack (ESP32)
; - Includes:
;   - M5stack (board=m5stack-core-esp32)
;   - etc.
; ---------------------------------------------------------
[mcu-M5Stack]
platform = espressif32
board = m5stack-core-esp32
framework = arduino

; ---------------------------------------------------------
; MCU: Teensy
; - Includes:
;   - Teensy 3.1/3.2 (board=teensy31)
;   - Teensy 4.0 (board=teensy40)
;   - etc.
; ---------------------------------------------------------
[mcu-Teensy]
platform = teensy
board = teensy31
framework = arduino

; Disable ks0108 as PIO compile for T3 will fail even
; though the ks0108 library is not being used/included.
lib_ignore = ks0108

; =========================================================
; Section 6: ENVIRONMENTS
; =========================================================

; ---------------------------------------------------------
; PlatformIO GUIslice Environments
;
; - The GUIslice configuration have been pulled from the
;   associated GUIslice /configs/ file.
; - If you have an existing GUIslice config file,
;   it is relatively easy to add your own environment
;   here to load the config.
; ---------------------------------------------------------

$<GUIslice_ENV>

; ...
<STOP>
