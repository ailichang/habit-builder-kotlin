package com.habitbuilder.settings.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.habitbuilder.R

@Composable
fun FaqItemComposable(question:String, answer:String) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    Column (modifier = Modifier
        .clickable { isExpanded = !isExpanded }
        .padding(20.dp, 10.dp)){
        FaqTitleComposable(question = question, isExpanded)
        FaqAnswerComposable(answer, isExpanded)
    }
}

@Composable
fun FaqTitleComposable(question:String, isExpanded:Boolean){
    Row{
        Text(modifier = Modifier
            .weight(9f)
            .align(Alignment.CenterVertically)
            .padding(0.dp, 0.dp, 10.dp, 0.dp),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            text = question)
        Icon(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            painter = painterResource(if(isExpanded) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = stringResource(id = R.string.expand_button))
    }
}

@Composable
fun FaqAnswerComposable(answer: String, isExpanded:Boolean){
    if(isExpanded) {
        Row (modifier = Modifier.padding(0.dp, 20.dp)){
            Text(modifier = Modifier
                .weight(9f)
                .align(Alignment.CenterVertically),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                text = answer)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FaqItemComposablePreview(){
    FaqItemComposable("I've created a habit. Now what?","I've created a habit. Now what?")
}