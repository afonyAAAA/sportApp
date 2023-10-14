package com.lucky.glo.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.lucky.glo.navigation.Screens
import com.lucky.glo.viewModels.MainViewModel

@Composable
fun StartScreen(navHostController: NavHostController, mainViewModel: MainViewModel){

    val sp = LocalContext.current.getSharedPreferences("pref", Context.MODE_PRIVATE)

    var counter by rememberSaveable { mutableIntStateOf(0) }

    val scope = rememberCoroutineScope()

    val factsCasino : List<String> = listOf(
        "The largest casino win was recorded in 2003 when a man named Archie Karas won over \$39 million at the MGM Grand Casino in Las Vegas, while playing slot machines.",
        "The world's first public casino opened in Venice, Italy, in 1638. It was called \"Il Ridotto\" and was a popular place for public entertainment at the time.",
        "Next, you will learn a lot more interesting things about the casino!",
    )

    var nextFact by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically)
    ) {
        AnimatedVisibility(
            visible = counter < 2,
        ) {
            Text(
                modifier = Modifier
                    .padding(13.dp)
                    .fillMaxWidth(),
                letterSpacing = 10.sp,
                lineHeight = 70.sp,
                text = "Did you know?",
                fontSize = 60.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(175.dp, 200.dp)
        ){
            Column {
                AnimatedVisibility(
                    visible = nextFact,
                    exit =  slideOutHorizontally(targetOffsetX = {it}) + fadeOut(),
                    enter = slideInHorizontally() + fadeIn(),
                ) {
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = if(counter <= 2) factsCasino[counter] else "",
                            modifier = Modifier.padding(13.dp),
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }

            }
        }

        Button(
            onClick = {
                if(counter + 1 > 2){
                    navHostController.navigate(
                        Screens.Main.route,
                        navOptions = NavOptions.Builder()
                            .setPopUpTo(Screens.Main.route, false)
                            .build()
                    )

                    sp.edit().putBoolean("first_launch", false).apply()

                    mainViewModel.checkFirstLaunch()
                }else{
                    scope.launch(Dispatchers.IO) {
                        if(counter in 0..1){
                            nextFact = false
                            delay(1500)
                            counter++
                            nextFact = true
                        }
                    }
                }
            },
            border = BorderStroke(3.dp, MaterialTheme.colorScheme.onPrimary),
            modifier = Modifier
                .heightIn(60.dp)
                .widthIn(200.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Next",
                fontSize = 20.sp,
                color = Color.White
            )
        }

    }
}