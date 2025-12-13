package com.example.perpustakaanoffline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip

class BookAdapter(
    private val items: MutableList<Book> = mutableListOf(),
    private val onItemClick: (Book) -> Unit = {}
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coverImageView: ImageView = itemView.findViewById(R.id.iv_book_cover)
        val titleTextView: TextView = itemView.findViewById(R.id.tv_book_title)
        val authorTextView: TextView = itemView.findViewById(R.id.tv_book_author)
        val categoryChip: Chip = itemView.findViewById(R.id.chip_book_category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = items[position]
        holder.coverImageView.setImageResource(book.coverResId)
        holder.titleTextView.text = book.title
        holder.authorTextView.text = book.author
        holder.categoryChip.text = book.category
        holder.itemView.setOnClickListener { onItemClick(book) }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(data: List<Book>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }
}