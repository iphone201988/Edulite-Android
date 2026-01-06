package com.edu.lite.ui.dash_board.home.quiz.question.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edu.lite.R
import com.edu.lite.data.model.OptionData
import com.edu.lite.databinding.RvAnswerItemBinding

class OptionAdapter(
    private val options: List<OptionData?>,
    private val selectedOptionId: String?,
    private val isClickable: Boolean,
    private val onOptionClick: (OptionData?) -> Unit
) : RecyclerView.Adapter<OptionAdapter.OptionViewHolder>() {

    private var currentSelectedId: String? = selectedOptionId

    inner class OptionViewHolder(val binding: RvAnswerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(option: OptionData?) {
            binding.tvOption.text = option?.text ?: ""
            binding.root.setOnClickListener {
                if (!isClickable) return@setOnClickListener
                currentSelectedId = option?._id
                onOptionClick(option)
                notifyDataSetChanged()
            }
            if (option?._id == currentSelectedId) {
                binding.ivAnswer.setImageResource(R.drawable.selected_answer)
            } else {
                binding.ivAnswer.setImageResource(R.drawable.un_selected_answer)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val binding = RvAnswerItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OptionViewHolder(binding)
    }

    override fun getItemCount(): Int = options.size

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.bind(options[position])
    }
}


