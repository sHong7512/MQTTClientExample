package com.shong.mqttclient

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.shong.imageconverter.ImageConverter
import com.shong.klog.Klog
import com.shong.klog.models.LogLevel
import kotlinx.coroutines.*
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage

class MainActivity : AppCompatActivity() {
    companion object {
        const val SERVER_IP: String = "tcp://192.168.2.133:1883"  // 서버 IP
        const val TOP_TOPIC: String = "topic"
    }

    val mqttClient: MqttClient = MqttClient(SERVER_IP, MqttClient.generateClientId(), null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Klog.runFloating(this)
        Klog.addBackPressedFloatingClose(this)

        // 클라이언트 초기화
        mqttClient.connect()

        // 콜백 설정
        mqttClient.setCallback(object : MqttCallback {
            override fun connectionLost(p0: Throwable?) {
                //연결이 끊겼을 경우
                Klog.f("MQTTService", "Connection Lost")
            }

            override fun messageArrived(p0: String?, p1: MqttMessage?) {
                //도착 메세지
                if (p0 == "$TOP_TOPIC/image" && p1 != null) {
                    val bitmap = ImageConverter.byteArrayToBitmap(p1.payload)
                    Klog.fl("arr-$p0", "imageSize : ${p1.payload.size / 1024}kb", LogLevel.I)
                    MainScope().launch {
                        this@MainActivity.findViewById<ImageView>(R.id.imageView).setImageBitmap(bitmap)
                    }
                } else {
                    Klog.fl("arr-$p0", p1.toString(), LogLevel.I)
                }
            }

            override fun deliveryComplete(p0: IMqttDeliveryToken?) {
                //메세지가 도착 하였을 때
                Klog.fl("MQTTService", "Delivery Complete\ntoken: $p0", LogLevel.V)
            }
        })

        // 구독 (sub)
//        mqttClient.subscribe("$TOP_TOPIC")
        mqttClient.subscribe("$TOP_TOPIC/#")
//        mqttClient.subscribe("$TOP_TOPIC/+/aa")
//        mqttClient.subscribe("$TOP_TOPIC/image")

        // 전송 (pub)
        setBasicSendButton(findViewById<Button>(R.id.sendButton1), "$TOP_TOPIC")
        setBasicSendButton(findViewById<Button>(R.id.sendButton2), "$TOP_TOPIC/a")
        setBasicSendButton(findViewById<Button>(R.id.sendButton3), "$TOP_TOPIC/a/aa")
        setBasicSendButton(findViewById<Button>(R.id.sendButton4), "$TOP_TOPIC/a/bb")
        setBasicSendButton(findViewById<Button>(R.id.sendButton5), "$TOP_TOPIC/b")
        setBasicSendButton(findViewById<Button>(R.id.sendButton6), "$TOP_TOPIC/b/aa")

        val imageConverter = ImageConverter(this)
        findViewById<Button>(R.id.albumButton).setOnClickListener {
            imageConverter.setOnUriListener(object : ImageConverter.OnUriListener {
                override fun onUri(uri: Uri) {
                    val imageByteArray =
                        ImageConverter.convertToJpgByteArray(this@MainActivity, uri) ?: return
                    CoroutineScope(Dispatchers.IO).launch {
                        sendByteArray("$TOP_TOPIC/image", imageByteArray)
                    }
                }

                override fun onError(msg: String) {
                    TODO("Not yet implemented")
                }
            })
            imageConverter.getUriForAlbum()
        }
    }

    private fun setBasicSendButton(btn: Button, topic: String) {
        btn.setText("send - $topic")
        btn.setOnClickListener {
            sendStr(topic, "i'm android - $topic")
        }
    }

    private fun sendStr(topic: String, msg: String) {
        if (mqttClient.isConnected) {
            mqttClient.publish(topic, MqttMessage(msg.toByteArray()))
            Klog.f("Send", msg)
        } else {
            Klog.f("Send", "is not connected")
        }
    }

    private fun sendByteArray(topic: String, byteArray: ByteArray) {
        if (mqttClient.isConnected) {
            mqttClient.publish(topic, MqttMessage(byteArray))
            Klog.d("Send_byteArray", "size :: ${byteArray.size}")
        } else {
            Klog.d("Send_byteArray", "is not connected")
        }
    }
}