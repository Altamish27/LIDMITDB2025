package com.google.mediapipe.examples.gesturerecognizer.features.panduan

import android.animation.ValueAnimator
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.gesturerecognizer.R
import com.google.mediapipe.examples.gesturerecognizer.data.model.ExpandablePanduanHijaiyah
import com.google.mediapipe.examples.gesturerecognizer.core.main.MainActivity
import com.google.mediapipe.examples.gesturerecognizer.core.animation.ViewAnimationUtils

class ExpandablePanduanAdapter(
    private val items: List<ExpandablePanduanHijaiyah>
) : RecyclerView.Adapter<ExpandablePanduanAdapter.ViewHolder>() {

    private val animatedPositions = mutableSetOf<Int>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerLayout: LinearLayout = itemView.findViewById(R.id.header_layout)
        val expandableLayout: LinearLayout = itemView.findViewById(R.id.expandable_layout)
        val tvHuruf: TextView = itemView.findViewById(R.id.tv_huruf)
        val tvGesture: TextView = itemView.findViewById(R.id.tv_gesture)
        val tvNamaLatin: TextView = itemView.findViewById(R.id.tv_nama_latin)
        val ivExpand: ImageView = itemView.findViewById(R.id.iv_expand)
        val tvDeskripsi: TextView = itemView.findViewById(R.id.tv_deskripsi)
        val tvTips: TextView = itemView.findViewById(R.id.tv_tips)
        val btnPractice: CardView = itemView.findViewById(R.id.btn_practice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_panduan_expandable, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // Set data
        holder.tvHuruf.text = item.huruf
        // Gesture column displays the same Arabic letter with hijaiyah_font_family (matching activity_home.xml)
        holder.tvGesture.text = item.huruf  // Use the same Arabic letter with hijaiyah_font_family
        holder.tvNamaLatin.text = item.namaLatin
        holder.tvDeskripsi.text = item.deskripsi
        holder.tvTips.text = item.tips

        // Set expanded state
        holder.expandableLayout.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
        holder.ivExpand.rotation = if (item.isExpanded) 180f else 0f

        // Click listener for expand/collapse
        holder.headerLayout.setOnClickListener {
            item.isExpanded = !item.isExpanded
            
            // Animate expand/collapse
            if (item.isExpanded) {
                expandView(holder.expandableLayout)
                rotateIcon(holder.ivExpand, 0f, 180f)
            } else {
                collapseView(holder.expandableLayout)
                rotateIcon(holder.ivExpand, 180f, 0f)
            }
        }

        // Practice button click
        holder.btnPractice.setOnClickListener {
            ViewAnimationUtils.animateClick(it) {
                val context = holder.itemView.context
                val intent = Intent(context, MainActivity::class.java)
                intent.putExtra("navigate_to", "camera")
                intent.putExtra("target_huruf", item.namaLatin)
                context.startActivity(intent)
            }
        }

        // Entrance animation for items
        if (!animatedPositions.contains(position)) {
            ViewAnimationUtils.animateListItem(holder.itemView, position)
            animatedPositions.add(position)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun expandView(view: View) {
        view.measure(
            View.MeasureSpec.makeMeasureSpec((view.parent as View).width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val targetHeight = view.measuredHeight

        view.layoutParams.height = 0
        view.visibility = View.VISIBLE

        val animator = ValueAnimator.ofInt(0, targetHeight)
        animator.addUpdateListener { animation ->
            view.layoutParams.height = animation.animatedValue as Int
            view.requestLayout()
        }
        animator.duration = 300
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    private fun collapseView(view: View) {
        val initialHeight = view.measuredHeight

        val animator = ValueAnimator.ofInt(initialHeight, 0)
        animator.addUpdateListener { animation ->
            view.layoutParams.height = animation.animatedValue as Int
            view.requestLayout()
            if (animation.animatedValue as Int == 0) {
                view.visibility = View.GONE
            }
        }
        animator.duration = 300
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    private fun rotateIcon(view: View, from: Float, to: Float) {
        view.animate()
            .rotation(to)
            .setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }
}
