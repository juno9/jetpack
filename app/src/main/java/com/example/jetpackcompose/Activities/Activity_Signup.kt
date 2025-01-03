package com.example.jetpackcompose.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcompose.ui.theme.JetpackcomposeTheme
import com.example.jetpackcompose.utils.networkObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.HttpURLConnection
import java.net.URL
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.random.Random


class Activity_Signup : ComponentActivity() {

    // 인증 번호 저장 변수
    private var verificationCode: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackcomposeTheme {
                SignUpScreenWithTheme(
                    onSendVerificationClick = { email -> sendVerificationEmail(email)
                }, onSignUpClick = { email, password -> performSignUp(email, password)
                })
            }
        }
    }

    private fun performSignUp(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {

                val urlString = networkObject.getBaseUrl() + "/signup.php"
                val url = URL(urlString)
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/x-www-form-urlencoded; utf-8")
                    doOutput = true

                    val postData = "email=$email&password=$password"
                    outputStream.write(postData.toByteArray())
                    outputStream.flush()
                    outputStream.close()


                    val responseMessage = inputStream.bufferedReader().use { it.readText() }
                    if (responseMessage.startsWith("{")) { // JSON 형식인지 확인
                        val responseJson = Json.parseToJsonElement(responseMessage).jsonObject
                        val status = responseJson["status"]?.jsonPrimitive?.content
                        val message = responseJson["message"]?.jsonPrimitive?.content
                        if (status == "success") {
                            runOnUiThread {
                                Toast.makeText(this@Activity_Signup, "가입 성공!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@Activity_Signup, Activity_Login::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(this@Activity_Signup, "가입 실패: $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@Activity_Signup, "서버에서 예상치 못한 응답을 받았습니다.", Toast.LENGTH_SHORT).show()
                            Log.e("signup", "Non-JSON response: $responseMessage")
                        }
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@Activity_Signup, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun sendVerificationEmail(email: String) {
        runBlocking {
            if (isEmailAlreadyRegistered(email)) {
                Toast.makeText(this@Activity_Signup, "이미 가입된 이메일입니다.", Toast.LENGTH_SHORT).show()
            } else {
                verificationCode = generateVerificationCode()
                sendEmail(email, verificationCode!!)
                Toast.makeText(this@Activity_Signup, "인증 번호가 전송되었습니다.", Toast.LENGTH_SHORT).show()


            }
        }
    }

    private suspend fun isEmailAlreadyRegistered(email: String): Boolean {
        val urlString = networkObject.getBaseUrl() + "/signupCheck.php"
        val url = URL(urlString)
        val postData = "user_email=$email"

        return withContext(Dispatchers.IO) {
            var isUnavailable = true
            try {
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                    doOutput = true

                    // 데이터 전송
                    outputStream.write(postData.toByteArray())
                    outputStream.flush()
                    outputStream.close()

                    val responseCode = responseCode
                    val responseMessage = inputStream.bufferedReader().use { it.readText() }
                    Log.d("signupCheck", "Response Code: $responseCode")
                    Log.d("signupCheck", "Response Message: $responseMessage")

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val responseJson = Json.parseToJsonElement(responseMessage).jsonObject
                        val status = responseJson["status"]?.jsonPrimitive?.content

                        if (status == "available") {
                            Log.d("signupCheck", "Available email.")
                            isUnavailable = false
                        } else {
                            Log.d("signupCheck", "Email already registered.")
                            isUnavailable = true
                        }
                    } else {
                        Log.d("signupCheck", "HTTP error: $responseCode")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isUnavailable
        }
    }


    private fun generateVerificationCode(): String {
        return Random.nextInt(100000, 999999).toString()
    }

    private fun sendEmail(email: String, code: String) {
        val senderEmail = "a62229437@gmail.com" // 발신자 이메일
        val senderPassword = "qrwt caal czzt eyeg" // 발신자 이메일 비밀번호 또는 앱 비밀번호 (2단계 인증 시 필요)
        val smtpHost = "smtp.gmail.com" // SMTP 서버 주소 (Gmail의 경우)

        // 이메일 전송 로직
        Thread {
            try {
                // SMTP 서버 설정
                val props = Properties().apply {
                    put("mail.smtp.host", smtpHost)
                    put("mail.smtp.port", "587") // Gmail은 587 포트 사용
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true") // TLS 사용
                }

                // 인증
                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(senderEmail, senderPassword)
                    }
                })

                // 메시지 생성
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(senderEmail)) // 발신자
                    addRecipient(Message.RecipientType.TO, InternetAddress(email)) // 수신자
                    subject = "Your Verification Code From KocoaChat"
                    setText("Your verification code is: $code")
                }

                // 메시지 전송
                Transport.send(message)
                println("Email sent successfully to $email")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Failed to send email: ${e.message}")
            }
        }.start() // 이메일 전송은 네트워크 작업이므로 별도의 스레드에서 실행
    }

    private fun isPasswordValid(password: String): Boolean {
        val lengthRegex = Regex("^.{8,20}$") // 8자리에서 20자리
        val twoOfThreeRegex = Regex(
            "^(?=.*[A-Za-z])(?=.*\\d)|(?=.*[A-Za-z])(?=.*[@\$!%*?&#])|(?=.*\\d)(?=.*[@\$!%*?&#]).{8,20}$"
        )

        return lengthRegex.matches(password) && twoOfThreeRegex.matches(password)
    }

    @Composable
    fun SignUpScreenWithTheme(
        onSendVerificationClick: (String) -> Unit,
        onSignUpClick: (String, String) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val context = LocalContext.current

        var email by remember { mutableStateOf("") }
        var inputCode by remember { mutableStateOf("") }
        var emailFieldEnabled by remember { mutableStateOf(true) }
        var verificationFieldEnabled by remember { mutableStateOf(true) }
        var signUpButtonEnabled by remember { mutableStateOf(false) }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var passwordHint by remember { mutableStateOf("") }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "회원가입",
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 16.dp),
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("이메일") },
                    enabled = emailFieldEnabled,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        if (email.isNotEmpty()) {
                            onSendVerificationClick(email)
                        } else {
                            Toast.makeText(context, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = emailFieldEnabled
                ) {
                    Text("인증")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputCode,
                    onValueChange = { inputCode = it },
                    label = { Text("인증번호") },
                    enabled = verificationFieldEnabled,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        if (inputCode == verificationCode) {
                            emailFieldEnabled = false
                            verificationFieldEnabled = false
                            Toast.makeText(context, "인증 성공!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "인증 번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = verificationFieldEnabled
                ) {
                    Text("확인")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordHint = if (!isPasswordValid(password)) "비밀번호는 숫자, 영문, 특수문자를 포함하여 8자에서 20자로 만들어 주세요" else ""
                    signUpButtonEnabled = password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword && passwordHint.isEmpty()
                },
                label = { Text("비밀번호") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            if (passwordHint.isNotEmpty()) {
                Text(
                    text = passwordHint,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Start).padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    signUpButtonEnabled = password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword && passwordHint.isEmpty()
                },
                label = { Text("비밀번호 확인") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(8.dp))

            val isPasswordMatched = password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword
            Text(
                text = when {
                    password.isEmpty() || confirmPassword.isEmpty() -> ""
                    isPasswordMatched -> "비밀번호가 일치합니다."
                    else -> "비밀번호가 일치하지 않습니다."
                },
                color = if (isPasswordMatched) Color.Green else Color.Red
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!emailFieldEnabled && isPasswordMatched) {
                        onSignUpClick(email, password)
                    }
                },
                enabled = signUpButtonEnabled
            ) {
                Text("가입하기")
            }
        }
    }

    // 비밀번호 유효성 검사 함수


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        JetpackcomposeTheme {
            SignUpScreenWithTheme(
                onSendVerificationClick = { email ->
                    // Mock action
                },
                onSignUpClick = { email, password ->
                    // Mock action
                }
            )
        }
    }

}
