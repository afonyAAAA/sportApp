package com.boundless.GIGABET.wonders.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.boundless.GIGABET.wonders.R
import com.boundless.GIGABET.wonders.models.NavhostValue
import com.boundless.GIGABET.wonders.navigation.Screens

@Composable
fun MainScreen(navHostController: NavhostValue){

    Image(
        painter = painterResource(id = R.drawable.background),
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        contentDescription = ""
    )

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .height(80.dp)
                .width(100.dp),
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Text(
                    text = stringResource(id = R.string.app_name)
                )
            }
        }

        Button(onClick = {
            navHostController.navHostController.navigate(Screens.Puzzles.route)
        }) {
            Text(text = "Play")
        }

        Button(onClick = { navHostController.navHostController.navigate(Screens.Settings.route) }) {
            Text(text = "Settings")
        }
    }
}