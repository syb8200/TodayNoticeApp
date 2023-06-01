package com.practice.fc_2_chapter3

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.gson.Gson
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.serverHostEditText)
        val confirmButton = findViewById<Button>(R.id.confirmButton)
        val informationTextView = findViewById<TextView>(R.id.informationTextView)

        // OKHttpClient를 사용하면 아래(Socket 직접 통신)와 같은 작업들이 필요 없게 됨
        // client를 밖으로 꺼낸 이유 : client는 하나만 생성하고, 거기에 각각 다른 요청이 들어감으로써 재사용이 가능
        val client = OkHttpClient()
        var serverHost = ""

        editText.addTextChangedListener {
            serverHost = it.toString()
        }

        confirmButton.setOnClickListener {
            // OkHttpClient는 client에게 request를 요청하게 된다. (요청은 Builder 형태로)
            val request: Request = Request.Builder()
                .url("http://$serverHost:8080")
                .build()

            /**
             *    OKHttp 로 통신하기
             */
            // callback은 인터페이스 이기 때문에, object 구현체로 구현
            // socket으로 직접 했을 때는 직접적으로 Thread 만들어서 처리했었어야 했는데,
            // OkHttp는 자동으로 새로운 스레드 만들어서 처리해준다.
            val callback = object: Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    // onFailure는 요청 자체가 실패하거나, 통신 과정에서 오류 났을 떄 발생
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "수신에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }

                    Log.e("Client", e.toString())
                }

                override fun onResponse(call: okhttp3.Call, response: Response) {
                    // 요청 자체는 성공했는데, 서버 쪽에서 결과를 실패로 내릴 수도 있음 -> isSuccessful 사용
                    if (response.isSuccessful) {
                        // response.body의 반환값 == ResponseBody?
                        // .string() == body객체를 string으로 읽겠다. (toString() 아님)
                        val response = response.body?.string()

                        // 안드로이드에서 기본적으로 JSONObject를 지원함
                        // 하지만 Gson라이브러리를 사용하면 더 편리하게 파싱할 수 있음(찾을 수 있음)
                        // Gson : Json형태의 데이터 포멧을 그대로 Kotlin 파일의 데이터 클래스로 바꿔주는 라이브러리
                        val message = Gson().fromJson(response, Message::class.java)

                        runOnUiThread {
                            informationTextView.isVisible = true
                            informationTextView.text = message.message

                            editText.isVisible = false
                            confirmButton.isVisible = false
                        }


                    } else {
                        runOnUiThread {
                            // 그냥 this하면 컴파일러 오류 -> why? context를 가리키는 this가 callback의 this인지, MainActivity의 this인지 모르기 때문
                            Toast.makeText(this@MainActivity, "수신에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            // 기존 : client.newCall(request).execute()
            // -> execute() : 직접 실행(동기 함수) -> 요청하는 동안 블락이 걸리는데, 딜레이가 생기면 ui가 멈추기 때문에 메인 스레드에서 호출 못함
            // 때문에 response callback을 사용하게 되었음 (해당 callback을 enqueue로 비동기로 전달)
            client.newCall(request).enqueue(callback)
        }

        /**
         *  Socket으로 직접 통신하기
         */
//        Thread {
//            try {
//                // 에뮬에서 테스트 할 때 10.0.2.2로 실행해야함
//                val socket = Socket("10.0.2.2", 8080)
//                val printer = PrintWriter(socket.getOutputStream())
//                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
//
//                // 서버 순서 : 읽고 -> 쓰고
//                // 클라이언트 순서 : 쓰고 -> 읽고
//                printer.println("GET / HTTP/1.1")
//                printer.println("Host: 127.0.0.1:8080")
//                printer.println("User-Agent: android")
//                printer.println("\r\n")
//                printer.flush()
//
//                var input: String? = "-1"
//                while(input != null) {
//                    input = reader.readLine()
//                    Log.e("Client", "$input")
//                }
//
//                reader.close()
//                printer.close()
//                socket.close()
//            } catch (e: Exception) {
//                Log.e("Client", e.toString())
//            }
//        }.start()

        // 안드로이드에서 데이터 가져올 때 일반적인 방법 (결국엔 HttpConnection이 Socket을 통해서 가져오게 됨)
        // : HttpURLConnection (HttpURLConnection은 내부적으로 okhttp를 사용하고 있음)
    }
}