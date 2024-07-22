package com.example.jetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcompose.ui.theme.JetpackcomposeTheme

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

@Composable
fun MyApp(
    modifier: Modifier = Modifier,
    names: List<String> = listOf("Hello World", "Compose","Its ","Strong","and","Light","UI","Tools")//임의로 정의해 둔 리스트
) {
    Column(modifier = modifier.padding(vertical = 20.dp)) {//열을 만들 것
        for (name in names) {//리스트의 사이즈만크 열이 생성될 것.
            Greeting(name = name)//greeting 컴포저블이 만들어지고 그리팅의 매개변수인 name은 myapp에 정의되어 있는 names의 요소들을 하나씩 가져와 name으로 정의하여 넣어줄 것.
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)//여기서 만들어질 열의 패딩을 넣어 줌
    )

    {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {//안에 들어갈 글자의 패딩을 만들어 줌
            Text(text = name)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetpackcomposeTheme {
        MyApp()
    }
}