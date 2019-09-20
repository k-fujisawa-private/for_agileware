#include <Servo.h>

//--------------------------------------------------
//----- S: 定数

// LED点滅制御ON
const byte C_MODE_LEDFLASH  = 0xF1;
// サーボモーター制御ON
const byte C_MODE_SERVOCTL  = 0xF2;
// 制御モードOFF
const byte C_MODE_OFF       = 0xF0;
// 応答値
const byte C_RESPONSE_ERROR = 0xFF;

// モードフラグ
enum E_ACTIVE_MODE {
  OFF,
  LEDFLASH,
  SERVOCTL,
};

// LEDランプGPIO端子
const int C_GPIO_LEDLAMP  = 13;
// サーボモーターGPIO端子
const int C_GPIO_SERVO    = 2;

//----- E: 定数
//--------------------------------------------------

//--------------------------------------------------
//----- S: 変数

int _IsActive = false;
E_ACTIVE_MODE _Mode = OFF;

Servo _motor;

//----- E: 変数
//--------------------------------------------------

//==================================================
// 初期設定
//==================================================
void setup() {
  // put your setup code here, to run once:
  pinMode(C_GPIO_LEDLAMP, OUTPUT);

  _motor.attach(C_GPIO_SERVO);
  _motor.write(0);

  Serial.begin(115200);

  _IsActive = false;
}

//==================================================
// ループ関数
//==================================================
void loop() {
  // put your main code here, to run repeatedly:
  if (Serial.available() > 0) {

    delay(10);
    int _Byte = Serial.read();

    byte _res = C_RESPONSE_ERROR;

    // 非実行モード
    if (false == _IsActive) {
      // モード判定
      if (C_MODE_LEDFLASH == _Byte) {
        _IsActive = true;
        _Mode = LEDFLASH;

        _res = _Byte;
      }
      else if (C_MODE_SERVOCTL == _Byte) {
        _IsActive = true;
        _Mode = SERVOCTL;

        _res = _Byte;
      }
      else {
        // 有効なモード値じゃない時は非実行モード継続
        _IsActive = false;
        _Mode = OFF;

        _res = C_MODE_OFF;
      }
    }
    // 実行モード
    else {
      if (C_MODE_OFF == _Byte) {
        _IsActive = false;
        _Mode = OFF;

        _res = C_MODE_OFF;
      }
      else {
        switch (_Mode)
        {
          case LEDFLASH:
            _res = FlashLED(_Byte);
            break;
          case SERVOCTL:
            _res = ServoAngle(_Byte);
            break;
          default:
            break;
        }
      }
    }

    Serial.write(_res);
  }
}

//==================================================
// LEDランプ点滅
//==================================================
byte FlashLED(byte cnt)
{
  for (byte i = 0; i < cnt; i++)
  {
    digitalWrite(C_GPIO_LEDLAMP, HIGH);
    delay(200);
    digitalWrite(C_GPIO_LEDLAMP, LOW);
    delay(200);
  }

  return cnt;
}

//==================================================
// サーボモーター制御
//==================================================
byte ServoAngle(byte angle)
{
  if (180 < angle) return C_RESPONSE_ERROR;

  _motor.write((int)angle);

  return angle;
}
