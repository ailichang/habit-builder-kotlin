package com.habitbuilder.settings.faq

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.habitbuilder.R

class FaqViewHolder (itemView: View): ViewHolder(itemView) {
    private var question:TextView
    private var answer:TextView
    init {
        question = itemView.findViewById(R.id.faq_question)
        answer = itemView.findViewById(R.id.faq_answer)
    }

    fun onBind(faqItem: FaqItem){
        question.text = faqItem.question
        answer.text = faqItem.answer
    }
}