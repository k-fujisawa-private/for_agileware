# coding:utf-8
from kivy.config import Config

Config.set('graphics', 'width', '800')
Config.set('graphics', 'height', '280')

from kivy.app import App
from kivy.uix.widget import Widget

from kivy.properties import StringProperty, ListProperty

import ArduinoControl

class TextWidget(Widget):

    _arduino = ArduinoControl.Arduino('COM8')

    # コンストラクタ
    def __init__(self, **kwargs):
        super(TextWidget, self).__init__(**kwargs)

        self.__C_MODE_FLASH_LED  = 1
        self.__C_MODE_SERVO_CTL  = 2
        self.__C_MODE_OFF        = 0

        self.__servo_angle = 0

        self.RdoModeOnClick()

    #
    def RdoModeOnClick(self):
        self.ids.GrpFlashLED.disabled = (False == self.ids.RdoFlashLED.active)
        self.ids.GrpServoCtl.disabled = (False == self.ids.RdoServoCtl.active)

        if self.ids.RdoFlashLED.active:
            self._arduino.SetModeFlashLED()
            self.ids.SwcConnection.disabled = False
        elif self.ids.RdoServoCtl.active:
            self._arduino.SetModeServoCtl()
            self.ids.SwcConnection.disabled = False
        else:
            self.ids.SwcConnection.disabled = True

    # 接続スイッチ変更イベント
    def SwcConnectionOnActive(self, value):
        if (True == value):
            self.ids.RdoFlashLED.disabled = True
            self.ids.RdoServoCtl.disabled = True
            self._arduino.Open()
        else:
            self.ids.RdoFlashLED.disabled = False
            self.ids.RdoServoCtl.disabled = False
            self._arduino.Close()

    def BtnFlashOnClick(self, cnt):
        self._arduino.Flash(cnt)

    def SdrServoAngleOnTouchMove(self, value):
        self.__servo_angle = int(value)

        self.ids.LblServoAngle.text = str(self.__servo_angle)

    def BtnMoveServoOnClick(self):
        self._arduino.Move(self.__servo_angle)

class MainApp(App):
    def __init__(self, **kwargs):
        super(MainApp, self).__init__(**kwargs)
        self.title = 'Arduino Controller'

    def build(self):
        return TextWidget()

if __name__ == '__main__':
    MainApp().run()
