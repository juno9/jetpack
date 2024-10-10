package com.example.jetpackcompose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpackcompose.ui.theme.JetpackcomposeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class LoginRequest(val email: String, val password: String)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackcomposeTheme {
                MyApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Preview
@Composable
fun MyApp(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 앱 아이콘 이미지
        val appIcon: Painter = painterResource(id = R.drawable.appicon)
        Image(
            painter = appIcon,
            contentDescription = "App Icon",
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 5.dp),
            contentScale = ContentScale.Crop
        )

        // 이메일 입력란
        var email by remember { mutableStateOf("") }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("이메일") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 비밀번호 입력란
        var password by remember { mutableStateOf("") }
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 로그인 버튼
        Button(
            onClick = { performLogin(LoginRequest(email, password)) },
            modifier = Modifier
                .width(300.dp)
                .height(48.dp)
        ) {
            Text("로그인")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 카카오 로그인 버튼
        Button(
            onClick = { /* 카카오 로그인 액션 */ },
            modifier = Modifier
                .width(300.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFFFE812)) // 카카오 색상
        ) {
            Text("카카오 로그인")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 네이버 로그인 버튼
        Button(
            onClick = { /* 네이버 로그인 액션 */ },
            modifier = Modifier
                .width(300.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF03C75A)) // 네이버 색상
        ) {
            Text("네이버 로그인")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 회원가입 및 비밀번호 찾기 텍스트
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = {


            }
            )
            {
                Text("회원가입")
            }
            TextButton(onClick = { /* 비밀번호 찾기 액션 */ }) {
                Text("비밀번호 찾기")
            }
        }
    }
}

fun performLogin(loginRequest: LoginRequest) {
    // 코루틴을 통해 네트워크 통신 비동기 처리
    CoroutineScope(Dispatchers.IO).launch {
        val url = URL("http://192.168.110.93//login.php") // 서버 URL을 여기에 넣으세요
        val jsonBody = Json.encodeToString(LoginRequest.serializer(), loginRequest) // 직렬화 부분 수정
        Log.d("LoginRequest", "Sending JSON data: $jsonBody")

        // HTTP 통신 설정
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; utf-8")
            doOutput = true

            // JSON 데이터 전송
            val outputStream: OutputStream = outputStream
            outputStream.write(jsonBody.toByteArray())
            outputStream.flush()
            outputStream.close()

            val responseCode = responseCode
            val responseMessage = inputStream.bufferedReader().use { it.readText() } // 응답 데이터 읽기
            Log.d("LoginRequest", "Response Code: $responseCode")
            Log.d("LoginRequest", "Response Message: $responseMessage")
            // 응답 처리 (예시)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 로그인 성공
                Log.d("LoginRequest", "응답 받음, message: "+responseMessage)
            } else {
                // 로그인 실패
                Log.d("LoginRequest", "응답 못받음: $responseCode")
            }
        }
    }
}




@Composable
fun GreetingPreview() {
    JetpackcomposeTheme {
        MyApp()
    }
}
