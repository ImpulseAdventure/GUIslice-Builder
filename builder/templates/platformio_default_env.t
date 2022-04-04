<Arduino-ILI9341-STMPE610>
[env:Arduino-ILI9341-STMPE610]
extends = mcu-Arduino
monitor_port = COM4
monitor_speed = 9600
;If you need a specific version of GUIslice or any other
;library load it in a new folder "Libraries" and 
;uncomment the lib_extra_dirs while also
;commenting out GUIslice in lib_deps
;lib_extra_dirs = ~/Documents/PlatformIO/Libraries
lib_deps =
	${disp-ILI9341.lib_deps}
	${touch-STMPE610.lib_deps}
	GUIslice
build_flags =
	${common.build_flags}
	${disp-ILI9341.build_flags}
	${touch-STMPE610.build_flags}
	-DUSER_CONFIG_LOADED=1
	-DDRV_DISP_ADAGFX
	-DDRV_DISP_ADAGFX_ILI9341
	-DDRV_TOUCH_ADA_STMPE610
	-DADAGFX_PIN_CS=10
	-DADAGFX_PIN_DC=9
	-DADAGFX_PIN_RST=0
	-DADAGFX_SPI_HW=1
	-DADAGFX_PIN_MOSI=11
	-DADAGFX_PIN_MISO=12
	-DADAGFX_PIN_CLK=13
	-DADAGFX_PIN_SDCS=4
	-DGSLC_ROTATE=1
	-DADATOUCH_I2C_HW=0
	-DADATOUCH_SPI_HW=1
	-DADATOUCH_SPI_SW=0
	-DADATOUCH_I2C_ADDR=0x41
	-DADATOUCH_PIN_CS=8
;	-DINIT_MSG_DISABLE	
	-DDEBUG_ERR=1
	-DGSLC_FEATURE_COMPOUND=0
	-DGSLC_FEATURE_XTEXTBOX_EMBED=0
	-DGSLC_FEATURE_INPUT=0
	-DGSLC_SD_EN=0
	-DGSLC_TOUCH_MAX_EVT=1
	-DGSLC_SD_BUFFPIXEL=50
	-DGSLC_CLIP_EN=1
	-DGSLC_BMP_TRANS_EN=1
	-DGSLC_BMP_TRANS_RGB=0xFF,0x00,0xFF
	-DGSLC_USE_FLOAT=0
	-DGSLC_DEV_TOUCH=\"\"
	-DGSLC_USE_PROGMEM=1
	-DGSLC_LOCAL_STR=0
	-DGSLC_LOCAL_STR_LEN=30
	-DADATOUCH_REMAP_YX=0
; You will need to override these values after calibration of your display
	-DADATOUCH_X_MIN=244
	-DADATOUCH_X_MAX=3858
	-DADATOUCH_Y_MIN=141
	-DADATOUCH_Y_MAX=3717
;	-DUSER_CONFIG_INC_FILE
;	-DUSER_CONFIG_INC_FNAME=\"../configs/ard-shld-adafruit_28_res.h\"
<STOP>
<Arduino-ILI9341-FT6206>
[env:Arduino-ILI9341-FT6206]
extends = mcu-Arduino
lib_deps =
	${disp-ILI9341.lib_deps}
	${touch-FT6206.lib_deps}
	GUIslice
build_flags =
	${common.build_flags}
	${disp-ILI9341.build_flags}
	${touch-FT6206.build_flags}
	-DUSER_CONFIG_LOADED=1
	-DDRV_DISP_ADAGFX
	-DDRV_DISP_ADAGFX_ILI9341
	-DRV_TOUCH_ADA_FT6206
; Touch sensitivity for DRV_TOUCH_FT6206 (capacitive touch)	
	-DADATOUCH_SENSITIVITY=40
	-DADAGFX_PIN_CS=10
	-DADAGFX_PIN_DC=9
	-DADAGFX_PIN_RST=0
	-DADAGFX_SPI_HW=1
	-DADAGFX_PIN_MOSI=11
	-DADAGFX_PIN_MISO=12
	-DADAGFX_PIN_CLK=13
	-DADAGFX_PIN_SDCS=4
	-DGSLC_ROTATE=1
	-DADATOUCH_I2C_HW=0
	-DADATOUCH_SPI_HW=1
	-DADATOUCH_SPI_SW=0
	-DADATOUCH_I2C_ADDR=0x41
	-DADATOUCH_PIN_CS=8
;	-DINIT_MSG_DISABLE	
	-DDEBUG_ERR=1
	-DGSLC_FEATURE_COMPOUND=0
	-DGSLC_FEATURE_XTEXTBOX_EMBED=0
	-DGSLC_FEATURE_INPUT=0
	-DGSLC_SD_EN=0
	-DGSLC_TOUCH_MAX_EVT=1
	-DGSLC_SD_BUFFPIXEL=50
	-DGSLC_CLIP_EN=1
	-DGSLC_BMP_TRANS_EN=1
	-DGSLC_BMP_TRANS_RGB=0xFF,0x00,0xFF
	-DGSLC_USE_FLOAT=0
	-DGSLC_DEV_TOUCH=\"\"
	-DGSLC_USE_PROGMEM=1
	-DGSLC_LOCAL_STR=0
	-DGSLC_LOCAL_STR_LEN=30
;	-DUSER_CONFIG_INC_FILE
	-DUSER_CONFIG_INC_FNAME=\"../configs/ard-shld-adafruit_28_cap.h\"
<STOP>
<Arduino-HX8357-FT6206>
[env:Arduino-HX8357-FT6206]
extends = mcu-Arduino
lib_deps =
	${disp-HX8357.lib_deps}
	${touch-FT6206.lib_deps}
	GUIslice
build_flags =
	${common.build_flags}
	${disp-HX8357.build_flags}
	${touch-FT6206.build_flags}
	-DUSER_CONFIG_LOADED=1
	-DDRV_DISP_ADAGFX
	-DRV_DISP_ADAGFX_HX8357
	-DRV_TOUCH_ADA_FT6206
; Touch sensitivity for DRV_TOUCH_FT6206 (capacitive touch)	
	-DADATOUCH_SENSITIVITY=40
	-DADAGFX_PIN_CS=10
	-DADAGFX_PIN_DC=9
	-DADAGFX_PIN_RST=0
	-DADAGFX_SPI_HW=1
	-DADAGFX_PIN_MOSI=11
	-DADAGFX_PIN_MISO=12
	-DADAGFX_PIN_CLK=13
	-DADAGFX_PIN_SDCS=4
	-DGSLC_ROTATE=1
	-DADATOUCH_I2C_HW=0
	-DADATOUCH_SPI_HW=1
	-DADATOUCH_SPI_SW=0
	-DADATOUCH_I2C_ADDR=0x41
	-DADATOUCH_PIN_CS=8
;	-DINIT_MSG_DISABLE	
	-DDEBUG_ERR=1
	-DGSLC_FEATURE_COMPOUND=0
	-DGSLC_FEATURE_XTEXTBOX_EMBED=0
	-DGSLC_FEATURE_INPUT=0
	-DGSLC_SD_EN=0
	-DGSLC_TOUCH_MAX_EVT=1
	-DGSLC_SD_BUFFPIXEL=50
	-DGSLC_CLIP_EN=1
	-DGSLC_BMP_TRANS_EN=1
	-DGSLC_BMP_TRANS_RGB=0xFF,0x00,0xFF
	-DGSLC_USE_FLOAT=0
	-DGSLC_DEV_TOUCH=\"\"
	-DGSLC_USE_PROGMEM=1
	-DGSLC_LOCAL_STR=0
	-DGSLC_LOCAL_STR_LEN=30
;	-DUSER_CONFIG_INC_FILE
	-DUSER_CONFIG_INC_FNAME=\"../configs/ard-shld-adafruit_28_cap.h\"
<STOP>
<Arduino-mcufriend-NoTouch>
[env:Arduino-mcufriend-NoTouch]
extends = mcu-Arduino
lib_deps =
	${disp-mcufriend.lib_deps}
	GUIslice
build_flags =
	${common.build_flags}
	${disp-mcufriend.build_flags}
	-DUSER_CONFIG_LOADED
	-DUSER_CONFIG_INC_FILE
	-DUSER_CONFIG_INC_FNAME=\"../configs/ard-shld-mcufriend.h\"
<STOP>
<Arduino-mcufriend-4wire>
[env:Arduino-mcufriend-4wire]
extends = mcu-Arduino
lib_deps =
	${disp-mcufriend.lib_deps}
	${touch-4wire.lib_deps}
	GUIslice
build_flags =
	${common.build_flags}
	${disp-mcufriend.build_flags}
	${touch-4wire.build_flags}
	-DUSER_CONFIG_LOADED
	-DUSER_CONFIG_INC_FILE
	-DUSER_CONFIG_INC_FNAME=\"../configs/ard-shld-mcufriend_4wire.h\"
; NOTE: you can either use a config file or override defines but not both!
; Provide new pinout & calibration
;	-DADATOUCH_PIN_YP = A1
;	-DADATOUCH_PIN_XM = A2
;	-DADATOUCH_PIN_YM = 7 
;	-DADATOUCH_PIN_XP = 6 
;	-DADATOUCH_X_MIN = 145
;	-DADATOUCH_X_MAX = 905
;	-DADATOUCH_Y_MIN = 937
;	-DADATOUCH_Y_MAX = 165
<STOP>
<Teensy-ILI9341_t3-NoTouch>
[env:Teensy-ILI9341_t3-NoTouch]
extends = mcu-Teensy
lib_deps =
	${disp-ILI9341_t3.lib_deps}
	GUIslice
build_flags =
	${common.build_flags}
	${disp-ILI9341_t3.build_flags}
	-DUSER_CONFIG_LOADED
	-DUSER_CONFIG_INC_FILE
	-DUSER_CONFIG_INC_FNAME=\"../configs/teensy-adagfx-ili9341_t3-NoTouch-audio.h\"
<STOP>
<Teensy-ILI9341_t3-XPT2046>
[env:Teensy-ILI9341_t3-XPT2046]
extends = mcu-Teensy
lib_deps =
	${disp-ILI9341_t3.lib_deps}
	${touch-XPT2046_ps.lib_deps}
	GUIslice
build_flags =
	${common.build_flags}
	${disp-ILI9341_t3.build_flags}
	${touch-XPT2046_ps.build_flags}
	-DUSER_CONFIG_LOADED
	-DUSER_CONFIG_INC_FILE
	-DUSER_CONFIG_INC_FNAME=\"../configs/teensy-adagfx-ili9341_t3-xpt2046-audio.h\"
<STOP>
<ESP32-TFT_eSPI-ILI9341-STMPE610>
[env:ESP32-TFT_eSPI-ILI9341-STMPE610]
extends = mcu-ESP32
monitor_port = COM5
monitor_speed = 115200
;lib_extra_dirs = ~/Documents/PlatformIO/Libraries
lib_deps =
	${disp-TFT_eSPI-ILI9341.lib_deps}
	${touch-STMPE610.lib_deps}
	GUIslice
build_flags =
	${common.build_flags}
	${disp-TFT_eSPI-ILI9341.build_flags}
	${touch-STMPE610.build_flags}
	-DUSER_CONFIG_LOADED=1;
	-DDRV_DISP_TFT_ESPI
	-DDRV_TOUCH_ADA_STMPE610
  -DADAGFX_PIN_SDCS=14
	-DGSLC_ROTATE=1
	-DADATOUCH_I2C_HW=0
	-DADATOUCH_SPI_HW=1
	-DADATOUCH_SPI_SW=0
	-DADATOUCH_I2C_ADDR=0x41
	-DADATOUCH_PIN_CS=13
	-DDEBUG_ERR=1
	-DGSLC_FEATURE_COMPOUND=0
	-DGSLC_FEATURE_XTEXTBOX_EMBED=0
	-DGSLC_FEATURE_INPUT=0
	-DGSLC_SD_EN=0
	-DGSLC_SPIFFS_EN=0
	-DGSLC_TOUCH_MAX_EVT=1
	-DGSLC_SD_BUFFPIXEL=50
	-DGSLC_CLIP_EN=1
	-DGSLC_BMP_TRANS_EN=1
	-DGSLC_BMP_TRANS_RGB=0xFF,0x00,0xFF
	-DGSLC_USE_FLOAT=0
	-DGSLC_DEV_TOUCH=\"\"
	-DGSLC_USE_PROGMEM=1
	-DGSLC_LOCAL_STR=0
	-DGSLC_LOCAL_STR_LEN=30
; You will need to override these values after calibration of your display
	-DADATOUCH_X_MIN=244
	-DADATOUCH_X_MAX=3858
	-DADATOUCH_Y_MIN=141
	-DADATOUCH_Y_MAX=3717
	-DADATOUCH_REMAP_YX=0
;	-DUSER_CONFIG_INC_FILE=1
;	-DUSER_CONFIG_INC_FNAME=\"../configs/esp-tftespi-default-stmpe610.h\"
<STOP>
<ESP32-TFT_eSPI-HX8357D-STMPE610>
[env:ESP32-TFT_eSPI-HX8357D-STMPE610]
extends = mcu-ESP32
;monitor_port = COM8
monitor_speed = 115200
;lib_extra_dirs = ~/Documents/PlatformIO/Libraries
lib_deps =
	${disp-TFT_eSPI-HX8357D.lib_deps}
	${touch-STMPE610.lib_deps}
	GUIslice
build_flags =
	${common.build_flags}
	${disp-TFT_eSPI-HX8357D.build_flags}
	${touch-STMPE610.build_flags}
	-DUSER_CONFIG_LOADED=1
	-DUSER_CONFIG_LOADED=1;
	-DDRV_DISP_TFT_ESPI
  -DADAGFX_PIN_SDCS=14
	-DGSLC_ROTATE=1
	-DADATOUCH_I2C_HW=0
	-DADATOUCH_SPI_HW=1
	-DADATOUCH_SPI_SW=0
	-DADATOUCH_I2C_ADDR=0x41
	-DADATOUCH_PIN_CS=13
	-DDEBUG_ERR=1
	-DGSLC_FEATURE_COMPOUND=0
	-DGSLC_FEATURE_XTEXTBOX_EMBED=0
	-DGSLC_FEATURE_INPUT=0
	-DGSLC_SD_EN=0
	-DGSLC_SPIFFS_EN=0
	-DGSLC_TOUCH_MAX_EVT=1
	-DGSLC_SD_BUFFPIXEL=50
	-DGSLC_CLIP_EN=1
	-DGSLC_BMP_TRANS_EN=1
	-DGSLC_BMP_TRANS_RGB=0xFF,0x00,0xFF
	-DGSLC_USE_FLOAT=0
	-DGSLC_DEV_TOUCH=\"\"
	-DGSLC_USE_PROGMEM=1
	-DGSLC_LOCAL_STR=0
	-DGSLC_LOCAL_STR_LEN=30
; You will need to override these values after calibration of your display
	-DADATOUCH_X_MIN=244
	-DADATOUCH_X_MAX=3858
	-DADATOUCH_Y_MIN=141
	-DADATOUCH_Y_MAX=3717
	-DADATOUCH_REMAP_YX=0
;	-DUSER_CONFIG_INC_FILE=1
;	-DUSER_CONFIG_INC_FNAME=\"../configs/esp-tftespi-default-stmpe610.h\"
<STOP>
<M5Stack-M5Stack-NoTouch>
[env:M5Stack-M5Stack-NoTouch]
extends = mcu-M5Stack
lib_deps =
	${disp-M5Stack.lib_deps}
	GUIslice
build_flags =
	${common.build_flags}
	${disp-M5Stack.build_flags}
	-DUSER_CONFIG_LOADED=1
	-DUSER_CONFIG_INC_FILE=1
	-DUSER_CONFIG_INC_FNAME=\"../configs/esp-shld-m5stack.h\"
<STOP>
<raspberrypi_3b>
; GUIslice library (example user configuration) for:
;   - CPU:     LINUX Raspberry Pi (RPi)
;   - Display: PiTFT
;   - Touch:   SDL
;   - Wiring:  None
[env:raspberrypi_3b]
platform = linux_arm
board = raspberrypi_3b
lib_deps =
	GUIslice
build_flags =
	${common.build_flags}
; - The following defines the display and touch drivers
	-DUSER_CONFIG_LOADED=1
	-DDRV_DISP_SDL2
	-DDRV_TOUCH_SDL
; NOTE: The GSLC_ROTATE feature is not yet supported in SDL mode
	-DGSLC_ROTATE=1
	-DDEBUG_ERR=1   
;	-DINIT_MSG_DISABLE
	-DGSLC_FEATURE_COMPOUND=1
	-DGSLC_FEATURE_XTEXTBOX_EMBED=0
	-DGSLC_FEATURE_INPUT=1
	-DGSLC_SD_EN=0
	-DDRV_TOUCH_IN_DISP
	-DGSLC_TOUCH_MAX_EVT=1
;	-DGSLC_CLIP_EN=1
; Enable for bitmap transparency and definition of color to use
	-DGSLC_BMP_TRANS_EN=1
	-DGSLC_BMP_TRANS_RGB=0xFF,0x00,0xFF
	-DGSLC_USE_FLOAT=1
; Define default device paths for framebuffer & touchscreen
; - The following assumes display driver (eg. fbtft) reads from fb1
; - Raspberry Pi can support hardware acceleration onto fb0
; - To use SDL2.0 with hardware acceleration with such displays,
;   use fb0 as the target and then run fbcp to mirror fb0 to fb1
	-DGSLC_DEV_FB=\"/dev/fb0\"
	-DGSLC_DEV_TOUCH=\"\"
	-DGSLC_DEV_VID_DRV=\"x11\"
; Enable SDL startup workaround? (1 to enable, 0 to disable)
	-DDRV_SDL_FIX_START=0
; Show SDL mouse (1 to show, 0 to hide)
	-DDRV_SDL_MOUSE_SHOW=0
; Enable hardware acceleration
	-DDRV_SDL_RENDER_ACCEL=1
	-DGSLC_USE_PROGMEM=0
	-DGSLC_LOCAL_STR=1
	-DGSLC_LOCAL_STR_LEN=30
; Debug diagnostic modes
;	-DDBG_LOG           
;	-DDBG_TOUCH         
;	-DDBG_FRAME_RATE 
;	-DDBG_DRIVER 
;	-DUSER_CONFIG_INC_FILE=1
;	-DUSER_CONFIG_INC_FNAME=\"../configs/rpi-sdl2-default-sdl.h\"
<STOP>
<raspberrypi_zero>
[env:raspberrypi_zero]
platform = linux_arm
board = raspberrypi_zero
lib_deps =
	GUIslice
build_flags =
	${common.build_flags}
	-DUSER_CONFIG_LOADED=1
	-DUSER_CONFIG_INC_FILE=1
	-DUSER_CONFIG_INC_FNAME=\"../configs/rpi-sdl2-default-sdl.h\"
<STOP>
<CustomConfig>
; ---------------------------------------------------------
; PlatformIO Custom GUIslice Environment
;
; - Instead of using a GUIslice config file directly, an
;   alternative approach is to define all of the GUIslice
;   config settings here in a custom environment.
; - The settings here match the same ones found in the
; 	GUIslice config. To convert between the formats:
;
; 	GUIslice config          platformio config
;   -----------------------  --------------------
;   #define <PARAM>          -D<PARAM>
;   #define <PARAM> <VALUE>  -D<PARAM>=<VALUE>
;
; ---------------------------------------------------------

[env:CustomConfig]
; Define your MCU, Display Driver and Touch Driver here
extends = mcu-Arduino
;lib_extra_dirs = ~/Documents/PlatformIO/Libraries
lib_deps =
	${disp-ILI9341.lib_deps}
	${touch-STMPE610.lib_deps}
	GUIslice
build_flags =
	${common.build_flags}
	${disp-ILI9341.build_flags}
	${touch-STMPE610.build_flags}
; Specify your user config details here
; Example from /configs/ard-shld-adafruit_28_res.h
	-DUSER_CONFIG_LOADED
	-DDRV_DISP_ADAGFX
	-DDRV_DISP_ADAGFX_ILI9341
	-DDRV_TOUCH_ADA_STMPE610
	-DADAGFX_PIN_CS=10
	-DADAGFX_PIN_DC=9
	-DADAGFX_PIN_RST=0
	-DADAGFX_SPI_HW=1
	-DADAGFX_PIN_MOSI=11
	-DADAGFX_PIN_MISO=12
	-DADAGFX_PIN_CLK=13
	-DADAGFX_PIN_SDCS=4
	-DGSLC_ROTATE=1
	-DADATOUCH_I2C_HW=0
	-DADATOUCH_SPI_HW=1
	-DADATOUCH_SPI_SW=0
	-DADATOUCH_I2C_ADDR=0x41
	-DADATOUCH_PIN_CS=8
	-DADATOUCH_X_MIN=244
	-DADATOUCH_X_MAX=3858
	-DADATOUCH_Y_MIN=141
	-DADATOUCH_Y_MAX=3717
	-DADATOUCH_REMAP_YX=0
	-DDEBUG_ERR=1
	-DGSLC_FEATURE_COMPOUND=0
	-DGSLC_FEATURE_XTEXTBOX_EMBED=0
	-DGSLC_FEATURE_INPUT=0
	-DGSLC_SD_EN=0
	-DGSLC_SPIFFS_EN=0
	-DGSLC_TOUCH_MAX_EVT=1
	-DGSLC_SD_BUFFPIXEL=50
	-DGSLC_CLIP_EN=1
	-DGSLC_BMP_TRANS_EN=1
	-DGSLC_BMP_TRANS_RGB=0xFF,0x00,0xFF
	-DGSLC_USE_FLOAT=0
	-DGSLC_DEV_TOUCH=\"\"
	-DGSLC_USE_PROGMEM=1
	-DGSLC_LOCAL_STR=0
	-DGSLC_LOCAL_STR_LEN=30
<STOP>
