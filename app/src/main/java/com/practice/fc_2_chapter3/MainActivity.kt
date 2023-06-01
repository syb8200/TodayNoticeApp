package com.practice.fc_2_chapter3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Thread {
            try {
                // 에뮬에서 테스트 할 때 10.0.2.2로 실행해야함
                val socket = Socket("10.0.2.2", 8080)
                val printer = PrintWriter(socket.getOutputStream())
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                // 서버 순서 : 읽고 -> 쓰고
                // 클라이언트 순서 : 쓰고 -> 읽고
                printer.println("GET / HTTP/1.1")
                printer.println("Host: 127.0.0.1:8080")
                printer.println("User-Agent: android")
                printer.println("\r\n")
                printer.flush()

                var input: String? = "-1"
                while(input != null) {
                    input = reader.readLine()
                    Log.e("Client", "$input")
                }

                reader.close()
                printer.close()
                socket.close()
            } catch (e: Exception) {
                Log.e("Client", e.toString())
            }
        }.start()

        // 안드로이드에서 데이터 가져올 때 일반적인 방법 (결국엔 HttpConnection이 Socket을 통해서 가져오게 됨)
        // : HttpURLConnection (HttpURLConnection은 내부적으로 okhttp를 사용하고 있음)

    }
}