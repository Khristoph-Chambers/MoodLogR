package com.example.moodlog

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JournalAdapter(private val journalList: List<JournalEntryModel>, private val onItemClick: (JournalEntryModel) -> Unit) :
    RecyclerView.Adapter<JournalAdapter.JournalViewHolder>() {

    class JournalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvJournalDate: TextView = itemView.findViewById(R.id.tvJournalDate)
        val tvJournalText: TextView = itemView.findViewById(R.id.tvJournalText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_journal_entry, parent, false)
        return JournalViewHolder(view)
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        val journalEntry = journalList[position]
        holder.tvJournalDate.text = journalEntry.createdAt
        holder.tvJournalText.text = journalEntry.journalText

        holder.itemView.setOnClickListener {
            onItemClick(journalEntry)
        }

    }

    override fun getItemCount(): Int = journalList.size
}
