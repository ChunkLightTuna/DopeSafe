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
 */
class InfoTextAdapter(private val titles: Array<String>, private val bodies: Array<String>) : RecyclerView.Adapter<InfoTextAdapter.InfoTextViewHolder>() {

    class InfoTextViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val container: View = itemView.findViewById(R.id.info_text_container)
        internal val title: TextView = itemView.findViewById(R.id.info_text_title)
        internal val body: TextView = itemView.findViewById(R.id.info_text_body)

        init {
            container.setOnClickListener {
                if (body.visibility == View.VISIBLE) {
                    body.visibility = View.GONE
                } else {
                    body.visibility = View.VISIBLE
                }
//                container.requestLayout()
            }
        }
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoTextViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.info_text_card_view, parent, false)
        return InfoTextViewHolder(v)
    }

    override fun onBindViewHolder(holder: InfoTextViewHolder, position: Int) {
        holder.title.text = titles[position]

        val bullet = "• "

        holder.body.text = bodies[position].split("\n").map(String::trim).map { line ->
            if (line.startsWith(bullet)) {
                val stripBullets = line.substringAfter(bullet)
                val spannableString = SpannableString(stripBullets + "\n")
                spannableString.setSpan(object : LeadingMarginSpan {
                    override fun getLeadingMargin(first: Boolean): Int {
                        return bullet.length * 20
                    }

                    override fun drawLeadingMargin(c: Canvas, p: Paint, x: Int, dir: Int, top: Int, baseline: Int, bottom: Int, text: CharSequence, start: Int, end: Int, first: Boolean, layout: Layout) {
                        if (first) {
                            val orgStyle = p.style
                            p.style = Paint.Style.FILL
                            c.drawText(bullet, 0f, bottom - p.descent(), p)
                            p.style = orgStyle
                        }
                    }
                }, 0, stripBullets.length, 0)
                spannableString
            } else {
                line + "\n"
            }
        }.reduce { acc, charSequence -> TextUtils.concat(acc, charSequence) }
    }
}