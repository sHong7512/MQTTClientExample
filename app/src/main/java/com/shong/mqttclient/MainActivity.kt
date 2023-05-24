package com.shong.mqttclient

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.shong.klog.Klog
import com.shong.klog.models.LogLevel
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage

class MainActivity : AppCompatActivity() {
    companion object{
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
                Klog.f("MQTTService","Connection Lost")
            }

            override fun messageArrived(p0: String?, p1: MqttMessage?) {
                //도착 메세지
                Klog.fl("arr-"+p0.toString(), p1.toString(), LogLevel.I)
            }

            override fun deliveryComplete(p0: IMqttDeliveryToken?) {
                //메세지가 도착 하였을 때
                Klog.fl("MQTTService","Delivery Complete\ntoken: $p0", LogLevel.V)
            }
        })

        // 구독 (sub)
//        mqttClient.subscribe("$TOP_TOPIC")
//        mqttClient.subscribe("$TOP_TOPIC/#")
        mqttClient.subscribe("$TOP_TOPIC/+/aa")

        // 전송 (pub)
        setSendButton(findViewById<Button>(R.id.sendButton1), "$TOP_TOPIC")
        setSendButton(findViewById<Button>(R.id.sendButton2), "$TOP_TOPIC/a")
        setSendButton(findViewById<Button>(R.id.sendButton3), "$TOP_TOPIC/a/aa")
        setSendButton(findViewById<Button>(R.id.sendButton4), "$TOP_TOPIC/a/bb")
        setSendButton(findViewById<Button>(R.id.sendButton5), "$TOP_TOPIC/b")
        setSendButton(findViewById<Button>(R.id.sendButton6), "$TOP_TOPIC/b/aa")
    }

    private fun setSendButton(btn: Button, topic: String){
        btn.setText("send - $topic")
        btn.setOnClickListener {
            if(mqttClient.isConnected){
                val str = "i'm android - $topic"
                mqttClient.publish(topic, MqttMessage(str.toByteArray()))
                Klog.f("Send", str)
            } else {
                Klog.f("Send", "is not connected")
            }
        }
    }
}