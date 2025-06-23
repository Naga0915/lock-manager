#include <BearSSLHelpers.h>
#include <ESP8266WiFi.h>
#include <WiFiClientSecureBearSSL.h>

const char* ssid = "@@@@@@@@@@";
const char* password = "@@@@@@@@@@@";
const char* host = "@@@@@@@@@@@@";
const int port = @@@@@@@@@@@@@@@;
const char fingerprint[] PROGMEM = "@@@@@@@@@@@@@@";
const unsigned long timeoutMs = 10000;

unsigned long lastReceive;


const char* myId = "0";
const char* myKeys[] = {"0", "1", "2", "3"};
const int numKeys = sizeof(myKeys) / sizeof(myKeys[0]);

BearSSL::WiFiClientSecure client;

void setup() {
  Serial.begin(74880);
  delay(1000);
  Serial.println("=== ESP8266 起動 ===");

  WiFi.begin(ssid, password);
  Serial.print("WiFi接続中");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi接続成功");
  Serial.print("IPアドレス: ");
  Serial.println(WiFi.localIP());

  initClient();
  if (!client.connect(host, port)) {
    Serial.println("接続失敗");
    return;
  }

  Serial.println("サーバに接続成功");
  lastReceive = millis();
  sendInitialInfo();
}

void loop() {
  if (!client.connected()) {
    Serial.println("切断されました。再接続を試みます...");
    reconnectToServer();
    delay(1000);
    return;
  }

  readServerMessage();

  delay(100);
}

void readServerMessage() {
  static String line = ""; 

  // --- タイムアウトチェック（応答なければ再接続） ---
  if (millis() - lastReceive > timeoutMs) {
    Serial.println("サーバー応答がありません。タイムアウトしました。再接続します...");
    reconnectToServer();
    line = "";              // 行バッファリセット
    return;
  }

  // --- 受信データ処理 ---
  while (client.available()) {
    char c = client.read();
    if (c == '\n') {
      line.trim();
      if (line.length() > 0) {
        Serial.print("受信: ");
        Serial.println(line);
        handleMessage(line);
        lastReceive = millis();  // ★ 応答があった時のみ更新！
      }
      line = "";
    } else {
      line += c;
      if (line.length() > 255) {
        Serial.println("受信メッセージが長すぎます。破棄して再接続します。");
        reconnectToServer();
        line = "";
        return;
      }
    }
  }
}

void sendInitialInfo() {
  client.printf("MY_ID_IS %s\n", myId);
  client.print("I_HAVE ");
  for (int i = 0; i < numKeys; ++i) {
    client.print(myKeys[i]);
    if (i != numKeys - 1) client.print(",");
  }
  client.print("\n");
  client.flush();
}

void reconnectToServer() {
  client.stop();
  delay(500);

  Serial.println("サーバに再接続中...");
  initClient();

  if (client.connect(host, port)) {
    Serial.println("再接続成功");
    lastReceive = millis();
    sendInitialInfo();
    delay(100);
  } else {
    Serial.println("再接続失敗");
  }
}

void initClient() {
  client.setFingerprint(fingerprint);
  client.setBufferSizes(1024, 1024);
  client.setTimeout(timeoutMs); // タイムアウト必須！
}

void handleMessage(const String& message) {
  if (message.startsWith("UNLOCK ")) {
    String keyId = message.substring(7);
    bool success = unlockKey(keyId);
    client.printf("UNLOCK %s %s\n", keyId.c_str(), success ? "SUCCESS" : "FAILED");
  } else if (message.startsWith("LOCK ")) {
    String keyId = message.substring(5);
    bool success = lockKey(keyId);
    client.printf("LOCK %s %s\n", keyId.c_str(), success ? "SUCCESS" : "FAILED");
  } else if (message.startsWith("IS_ALIVE ")) {
    String id = message.substring(9);
    if (isAlive(id)) {
      client.printf("IM_ALIVE %s\n", myId);
    }
  } else {
    client.printf("UNKNOWN_COMMAND: %s\n", message.c_str());
  }
}

bool hasKey(const String& keyId) {
  for (int i = 0; i < numKeys; ++i) {
    if (String(myKeys[i]) == keyId) {
      return true;
    }
  }
  return false;
}

bool lockKey(const String& keyId) {
  if (!hasKey(keyId)) {
    Serial.printf("LOCK 失敗: 鍵ID %s は保持していません\n", keyId.c_str());
    return false;
  }
  Serial.printf("鍵ID %s をロックしました（ダミー）\n", keyId.c_str());
  return true;
}

bool unlockKey(const String& keyId) {
  if (!hasKey(keyId)) {
    Serial.printf("UNLOCK 失敗: 鍵ID %s は保持していません\n", keyId.c_str());
    return false;
  }
  Serial.printf("鍵ID %s をアンロックしました（ダミー）\n", keyId.c_str());
  return true;
}

bool isAlive(const String& id) {
  return id == myId;
}