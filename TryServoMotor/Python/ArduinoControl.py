# coding:utf-8
import serial
import time
from enum import IntFlag

class Arduino:
    # コンストラクタ
    def __init__(self, com):
        self._arduino = None
        # 定数設定
        self.__C_ARDUINO_COM_PORT = com
        # 
        self.__C_MODE_OFF       = b'\xF0'
        self.__C_MODE_LED_FLASH = b'\xF1'
        self.__C_MODE_SERVO_CTL = b'\xF2'
        #
        self.__C_RESPONSE_ERROR = b'\xFF'

        self.__mode = self.__C_MODE_LED_FLASH

        #
        self.__C_RETRY_COUNT    = 10
        self.__C_RETRY_INTERVAL = 1


    # デストラクタ
    def __del__(self):
        pass

    def SetModeFlashLED(self):
        self.__mode = self.__C_MODE_LED_FLASH

    def SetModeServoCtl(self):
        self.__mode = self.__C_MODE_SERVO_CTL

    def Open(self):
        if True == self.IsOpened():
            return

        self._arduino = serial.Serial(self.__C_ARDUINO_COM_PORT, 115200, timeout=1)
        self._arduino.setDTR(False)

        time.sleep(2)

        self.__ChangeMode(self.__mode)

    def Close(self):
        if None == self._arduino or True == self._arduino.closed:
            return

        self.__ChangeMode(self.__C_MODE_OFF)

        self._arduino.close()
        self._arduino = None

    def Flash(self, count):
        if False == self.IsOpened():
            return

        self.__ActiveValue(count)

    def Move(self, angle):
        if False == self.IsOpened():
            return

        self.__ActiveValue(angle)

    #
    def IsOpened(self):
        return None != self._arduino and False == self._arduino.closed

    # モード変更書き込み
    def __ChangeMode(self, mode):
        print('REQUEST MODE: {}'.format(int.from_bytes(mode, 'big')))
        self._arduino.write(mode)

        _ret = False

        for i in range(1, self.__C_RETRY_COUNT):
            _res = self._arduino.read()
            print('RESPONSE    : {}'.format(int.from_bytes(_res, 'big')))
            if (mode == _res):
                _ret = True
                break
            time.sleep(self.__C_RETRY_INTERVAL)

        return _ret

    # アクティブコマンド書き込み
    def __ActiveValue(self, value):
        print('REQUEST MODE: {}'.format(value))

        _bval = value.to_bytes(1, 'big')
        self._arduino.write(_bval)

        for i in range(1, self.__C_RETRY_COUNT):
            _res = self._arduino.read()
            print('RESPONSE    : {}'.format(int.from_bytes(_res, 'big')))
            if (_bval == _res or self.__C_RESPONSE_ERROR == _res):
                break
            time.sleep(self.__C_RETRY_INTERVAL)
