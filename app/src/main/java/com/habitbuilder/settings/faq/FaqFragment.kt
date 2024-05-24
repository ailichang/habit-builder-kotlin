package com.habitbuilder.settings.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.habitbuilder.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationBarView

class FaqFragment: Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var faqAdapter: FaqAdapter
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var dividerItemDecoration: DividerItemDecoration

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_faq, container, false)
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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.faq_recycler_view)
        val linearLayoutManager = LinearLayoutManager(view.context)
        recyclerView.layoutManager = linearLayoutManager
        dividerItemDecoration =
            DividerItemDecoration(recyclerView.context, linearLayoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
        faqAdapter = FaqAdapter()
        recyclerView.adapter = faqAdapter
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
        faqAdapter.submitList(faqsList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        appBarLayout.removeOnOffsetChangedListener(onOffsetChangedListener)
    }
}