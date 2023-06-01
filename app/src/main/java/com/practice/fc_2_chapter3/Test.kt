package com.practice.fc_2_chapter3

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket

// JVM에서 작동함
fun main() {
    /**
     *  Socket으로 직접 통신하기
     */
    // 네트워크 관련 동작은 메인 스레드(UI 스레드)에서 작동되지 않음
    // 에뮬에서는 작동되지 않음 (컴퓨터와 핸드폰의 와이파이가 일치해야함)
    Thread {
        val port = 8080
        val server = ServerSocket(port) // 데이터 받을 준비 함(일단 blocking 됨)

        while(true) {
            val socket = server.accept() // 클라이언트 쪽에서 요청을 하면 -> 서버쪽에서 accept를 해줘야 함 -> 그제서야 socket이라는 객체 생성

            // 데이터 주고받으려면 stream이 필요하고, stream은 일방통행이다.(내려가고, 올라가고)
            // socket.getInputStream() // 클라이언트로부터 들어오는 스트림 == 클라이언트의 socket.outputStream
            // socket.getOutputStream() // 클라이언트에게 데이터를 주는 스트림 == 클라이언트의 socket.InputStream

            // 버퍼를 두고 데이터를 읽어야 함 (reader 필요)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream())) // inputStream을 Reader로 바꿔서 거기에 버퍼를 씌웠다.
            val printer = PrintWriter(socket.getOutputStream())

            var input: String? = "-1"
            while(input != null && input != "") {
                input = reader.readLine()
            }

            println("READ DATA $input")

            // http 통신 규격을 활용해서 사용자에게 데이터 보내기
            // http 통신하면 -> header, body 부분이 있다.
            // HEADER
            printer.println("HTTP/1.1 200 OK") // HTTP 1.1 버전을 사용하겠다 + 데이터를 정상적으로 수신/반환 했다. (정상응답)
            printer.println("Content-Type: text/html\r\n")

            // BODY
            printer.println("{\"message\": \"Today is Sunny\"}")
            printer.println("\r\n")
            printer.flush() // 잔여 데이터가 있을 수 있으니, 마저 배출해준다.
            printer.close() // outputStream 끊기

            reader.close()

            socket.close()
        }

    }.start()
}