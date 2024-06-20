package com.habitbuilder.settings.faq

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.habitbuilder.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationBarView
import com.habitbuilder.settings.ui.FaqItemComposable

class FaqFragment: Fragment() {
    private lateinit var appBarLayout: AppBarLayout
    private var onOffsetChangedListener =
        OnOffsetChangedListener { _: AppBarLayout?, verticalOffset: Int ->
            val materialToolbar =
                requireActivity().findViewById<MaterialToolbar>(R.id.tool_bar)
            if (verticalOffset == 0) {
                materialToolbar.setNavigationIconTint(requireContext().getColor(R.color.colorOnSurface))
            } else {
                materialToolbar.setNavigationIconTint(requireContext().getColor(R.color.colorOnPrimary))
            }
        }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val materialToolbar = requireActivity().findViewById<MaterialToolbar>(R.id.tool_bar)
        materialToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        appBarLayout = requireActivity().findViewById(R.id.app_bar_layout)
        onOffsetChangedListener =
            OnOffsetChangedListener { _: AppBarLayout?, verticalOffset: Int ->
                if (verticalOffset == 0) {
                    materialToolbar.setNavigationIconTint(requireContext().getColor(R.color.colorOnSurface))
                } else {
                    materialToolbar.setNavigationIconTint(requireContext().getColor(R.color.colorOnPrimary))
                }
            }
        appBarLayout.addOnOffsetChangedListener(onOffsetChangedListener)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val collapsingToolbarLayout =
            requireActivity().findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        collapsingToolbarLayout.title = requireContext().getString(R.string.faq_toolbar_title)
        val bottomNavigationView =
            requireActivity().findViewById<NavigationBarView>(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.GONE

        val faqsList: ArrayList<FaqItem> = object : ArrayList<FaqItem>() {
            init {
                add(
                    FaqItem(
                        getString(R.string.question_habit_tip),
                        getString(R.string.answer_habit_tip)
                    )
                )
                add(
                    FaqItem(
                        getString(R.string.question_create_habit),
                        getString(R.string.answer_create_habit)
                    )
                )
                add(
                    FaqItem(
                        getString(R.string.question_mission),
                        getString(R.string.answer_mission)
                    )
                )
                add(
                    FaqItem(
                        getString(R.string.question_mission_complete),
                        getString(R.string.answer_mission_complete)
                    )
                )
                add(
                    FaqItem(
                        getString(R.string.question_where_timer),
                        getString(R.string.answer_where_timer)
                    )
                )
                add(
                    FaqItem(
                        getString(R.string.question_where_counter),
                        getString(R.string.answer_where_counter)
                    )
                )
                add(
                    FaqItem(
                        getString(R.string.question_edit_habit),
                        getString(R.string.answer_edit_habit)
                    )
                )
                add(FaqItem(getString(R.string.question_level), getString(R.string.answer_level)))
                add(
                    FaqItem(
                        getString(R.string.question_receive_reward_points),
                        getString(R.string.answer_receive_reward_points)
                    )
                )
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                FaqListComposable(faqList = faqsList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        appBarLayout.removeOnOffsetChangedListener(onOffsetChangedListener)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @Composable
    fun FaqListComposable(faqList: List<FaqItem>){
        val scrollState = rememberLazyListState()
        MaterialTheme (colorScheme = if(isSystemInDarkTheme()) dynamicDarkColorScheme(LocalContext.current) else dynamicLightColorScheme(LocalContext.current)){
            Surface {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 80.dp)
                ) {
                    items(faqList.size) {
                        FaqItemComposable(
                            question = faqList[it].question,
                            answer = faqList[it].answer)
                        if (it < faqList.size - 1) {
                            HorizontalDivider(thickness = 1.dp)
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun FaqListComposablePreview(){
        val faqsList: ArrayList<FaqItem> = object : ArrayList<FaqItem>() {
            init {
                add(
                    FaqItem(
                        stringResource(R.string.question_habit_tip),
                        stringResource(R.string.answer_habit_tip)
                    )
                )
                add(
                    FaqItem(
                        stringResource(R.string.question_create_habit),
                        stringResource(R.string.answer_create_habit)
                    )
                )
            }
        }
        FaqListComposable(faqList = faqsList)
    }
}