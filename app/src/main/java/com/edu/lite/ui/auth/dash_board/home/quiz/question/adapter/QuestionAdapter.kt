package com.edu.lite.ui.auth.dash_board.home.quiz.question.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edu.lite.data.model.OptionData
import com.edu.lite.data.model.QuestionData
import com.edu.lite.databinding.QuestionListItemBinding

class QuestionAdapter(
    private var questions: List<QuestionData?>,
    private val onOptionSelected: (questionPosition: Int, option: OptionData?) -> Unit
) : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

    inner class QuestionViewHolder(val binding: QuestionListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(question: QuestionData?, position: Int) {
            binding.tvQuestionTitle.text = question?.question ?: ""

            // Pass previously selected option ID to OptionAdapter
            val adapter = OptionAdapter(
                question?.options ?: emptyList(),
                question?.userSelectedOptionId
            ) { selectedOption ->
                // Save selection in QuestionData
                question?.userSelectedOptionId = selectedOption?._id
                onOptionSelected(position, selectedOption)
            }

            binding.rvAnswer.adapter = adapter
            binding.rvAnswer.layoutManager = LinearLayoutManager(binding.root.context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = QuestionListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuestionViewHolder(binding)
    }

    override fun getItemCount(): Int = questions.size

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position], position)
    }

    fun updateQuestions(newQuestions: List<QuestionData?>) {
        this.questions = newQuestions
        notifyDataSetChanged()
    }
}

