package com.example.jetpackcompose.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcompose.R
import com.example.jetpackcompose.ui.theme.JetpackcomposeTheme
import com.example.jetpackcompose.utils.networkObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.HttpURLConnection
import java.net.URL


@Serializable
data class LoginRequest(val user_email: String, val user_password: String)
class Activity_Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackcomposeTheme {
                LoginScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Preview
@Composable

fun LoginScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current // Context를 가져옴
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
            onClick = { performLogin(context, LoginRequest(email, password)) },
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
            TextButton(onClick = { SignUp(context) }) {
                Text("회원가입")

            }
            TextButton(onClick = { /* 비밀번호 찾기 액션 */ }) {
                Text("비밀번호 찾기")
            }
        }
    }
}

fun SignUp(context: Context){
    CoroutineScope(Dispatchers.IO).launch {
        CoroutineScope(Dispatchers.Main).launch {
            val intent = Intent(context, Activity_Signup::class.java)
            context.startActivity(intent)
        }
    }
}
fun performLogin(context: Context, loginRequest: LoginRequest) {
    CoroutineScope(Dispatchers.IO).launch {
        val urlString = networkObject.getBaseUrl()+"/login.php"
        val url = URL(urlString)
        val postData = "user_email=${loginRequest.user_email}&user_password=${loginRequest.user_password}"

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/x-www-form-urlencoded; utf-8")
            doOutput = true

            // 데이터 전송
            outputStream.write(postData.toByteArray())
            outputStream.flush()
            outputStream.close()

            val responseCode = responseCode
            val responseMessage = inputStream.bufferedReader().use { it.readText() }
            Log.d("LoginRequest", "Response Code: $responseCode")
            Log.d("LoginRequest", "Response Message: $responseMessage")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseJson = Json.parseToJsonElement(responseMessage).jsonObject
                val status = responseJson["status"]?.jsonPrimitive?.content
                val message = responseJson["message"]?.jsonPrimitive?.content

                if (status == "success") {
                    Log.d("LoginRequest", "Login successful: $message")

                    // MainActivity로 이동
                    CoroutineScope(Dispatchers.Main).launch {
                        val intent = Intent(context, Activity_Main::class.java)
                        context.startActivity(intent)
                    }
                } else {
                    Log.d("LoginRequest", "Login failed: $message")
                }
            } else {
                Log.d("LoginRequest", "HTTP error: $responseCode")
            }
        }
    }
}









@Composable
fun GreetingPreview() {
    JetpackcomposeTheme {
        LoginScreen()
    }
}
