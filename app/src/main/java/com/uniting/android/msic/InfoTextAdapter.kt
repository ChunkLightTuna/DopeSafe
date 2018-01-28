package com.uniting.android.msic

import android.graphics.Canvas
import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.text.TextUtils
import android.text.Layout
import android.text.style.LeadingMarginSpan
import android.text.SpannableString


/**
 * Created by Chris.Oelerich on 1/20/2018.
 *
 * RecyclerViewAdapter for title/ body items. Does a lot of hacky shit to get bulleted lists to indent
 * TODO: bold and italics support
 */
class InfoTextAdapter(private val titles: Array<String>, private val bodies: Array<String>, private val collapse: Boolean) : RecyclerView.Adapter<InfoTextAdapter.InfoTextViewHolder>() {

    private val bullet = "• "

    class InfoTextViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val container: View = itemView.findViewById(R.id.info_text_container)
        internal val title: TextView = itemView.findViewById(R.id.info_text_title)
        internal val body: TextView = itemView.findViewById(R.id.info_text_body)
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoTextViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.info_text_card_view, parent, false)
        return InfoTextViewHolder(v)
    }

    override fun onBindViewHolder(holder: InfoTextViewHolder, position: Int) {

        //TODO: Hard Mode: https://youtu.be/EjTJIDKT72M?t=5m50s
        if (collapse) {
            holder.container.setOnClickListener {
                if (holder.body.visibility == View.VISIBLE) {
                    holder.body.visibility = View.GONE
                } else {
                    holder.body.visibility = View.VISIBLE
                }
            }
            holder.body.visibility = if (position > 0) View.GONE else View.VISIBLE
        }

        holder.title.text = titles[position]
        holder.body.text = bodies[position].split("\n").map(String::trim).map { line ->

            if (line.startsWith(bullet) || (line.length >= 3 && line.substring(0, 3).matches(Regex("[1-9]\\. ")))) {

                val offset = 2
                val before = String(CharArray(offset, { ' ' }))
                val after = offset * 5

                val delimiter = if (line.startsWith(bullet)) {
                    "$before$bullet"
                } else {
                    "$before${line[0]}. "
                }

                val margin = after * delimiter.length

                val noBully = line.substringAfter(delimiter.substringAfter(before))
                val spannableString = SpannableString(noBully + "\n")
                spannableString.setSpan(object : LeadingMarginSpan {
                    override fun getLeadingMargin(first: Boolean) = margin

                    override fun drawLeadingMargin(c: Canvas, p: Paint, x: Int, dir: Int, top: Int, baseline: Int, bottom: Int, text: CharSequence, start: Int, end: Int, first: Boolean, layout: Layout) {
                        if (first) {
                            val orgStyle = p.style
                            p.style = Paint.Style.FILL
                            c.drawText(delimiter, 0f, bottom - p.descent(), p)
                            p.style = orgStyle
                        }
                    }
                }, 0, noBully.length, 0)

                spannableString
            } else {
                line + "\n"
            }
        }.reduce { a, c -> TextUtils.concat(a, c) }

    }
}