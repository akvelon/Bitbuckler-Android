package com.akvelon.bitbuckler.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.extension.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect

class ToolbarMenuView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var mainContainer: ViewGroup? = null
    private var subMenuContainer: ViewGroup? = null
    private var isSubmenuExpanded: Boolean = false
    private var tvExpandableTitle: TextView? = null
    private var ivExpand: ImageView? = null
    private val subItemViews = mutableListOf<TextView>()
    private var areSubItemsClickable = true

    var subItemClickAccessibility = MutableStateFlow(true)

    private val rotateCounterclockwise: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.rotate_counter_clockwise)
    }
    private val rotateClockwise: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.rotate_clockwise)
    }

    init {
        setVisible(false)
        isClickable = true
        elevation = 6f
        this.setOnClickListener { closeView() }
    }

    fun inflateItems(items: List<MenuItem>) {
        mainContainer =
            inflateLayoutWithOutAttachingIt(R.layout.toolbar_menu_container) as LinearLayout

        items.forEach { item ->
            when (item) {
                is MenuItem.Expandable -> {
                    val childView = inflateLayoutWithOutAttachingIt(
                        R.layout.menu_item_expandable,
                        mainContainer!! as LinearLayout
                    )
                    with(childView.findViewById<ConstraintLayout>(R.id.menu_item_expandable)) {
                        setOnClickListener { toggleSubmenu() }
                    }
                    with(childView.findViewById<ImageView>(R.id.iv_icon_expandable)) {
                        setImageResource(item.iconRes)
                    }
                    tvExpandableTitle =
                        childView.findViewById<TextView>(R.id.tv_title_expandable).apply {
                            text = getString(item.titleRes)
                        }
                    ivExpand = childView.findViewById(R.id.iv_expand)
                }
                is MenuItem.Regular -> {
                    val childView = inflateLayoutWithOutAttachingIt(
                        R.layout.menu_item_regular,
                        mainContainer!! as LinearLayout
                    )
                    with(childView.findViewById<ConstraintLayout>(R.id.menu_item_regular)) {
                        setOnClickListener {
                            item.onClick.invoke()
                            closeView()
                        }
                    }
                    with(childView.findViewById<TextView>(R.id.tv_title_regular)) {
                        text = getString(item.titleRes)
                    }
                    with(childView.findViewById<ImageView>(R.id.iv_icon_regular)) {
                        setImageResource(item.iconRes)
                    }
                }
                is MenuItem.Submenu -> {
                    inflateSubItems(item.subItems)
                }
            }
        }
    }

    private fun toggleSubmenu() {
        TransitionManager.beginDelayedTransition(
            subMenuContainer?.rootView as ViewGroup,
            ChangeBounds()
        )
        isSubmenuExpanded = !isSubmenuExpanded
        subMenuContainer?.setVisible(isSubmenuExpanded)
        if (isSubmenuExpanded) {
            tvExpandableTitle?.setTextColor(context.getColorFromStyles(R.attr.colorPrimary))
            ivExpand?.startAnimation(rotateCounterclockwise)
        } else {
            resetSubmenu()
        }
    }

    private fun inflateSubItems(subitems: List<SubItem>) {
        subMenuContainer =
            inflateLayoutWithOutAttachingIt(
                R.layout.menu_item_container_of_subitems,
                mainContainer!! as LinearLayout
            ) as LinearLayout

        inflateDivider()

        subitems.forEachIndexed { index, subItem ->
            val childView = inflateLayoutWithOutAttachingIt(
                R.layout.menu_item_subitem,
                subMenuContainer as LinearLayout
            )

            val titleTextView = childView.findViewById<TextView?>(R.id.tv_title_subitem)

            titleTextView?.also { tv ->
                if (index == 0) tv.setTextColor(context.getColorFromStyles(R.attr.colorPrimary))
                tv.text = getString(subItem.titleRes)
                subItemViews.add(tv)
            }

            GlobalScope.launch {
                subItemClickAccessibility.collect { flag ->
                    areSubItemsClickable = flag
                }
            }

            childView.setOnClickListener {
                if(areSubItemsClickable) {
                    titleTextView?.let { setSubitemsColor(it) }
                    subItem.onClick.invoke()
                    closeView()
                }
            }
        }

        inflateDivider()
    }

    private fun inflateLayoutWithOutAttachingIt(layoutId: Int, parent: LinearLayout? = null): View {
        val childView = (parent ?: this).inflate(layoutId)
        (parent ?: this).addView(childView)
        return childView
    }

    private fun setSubitemsColor(tv: TextView) {
        subItemViews.forEach { it.setTextColor(context.color(R.color.secondaryTextLight)) }
        tv.setTextColor(context.getColorFromStyles(R.attr.colorPrimary))
    }

    private fun closeView() {
        resetSubmenu(false)
        this.setVisible(false)
    }

    private fun resetSubmenu(withAnimation: Boolean = true) {
        isSubmenuExpanded = false
        tvExpandableTitle?.setTextColor(context.getColorFromStyles(R.attr.textColorPrimary))
        if (withAnimation) ivExpand?.startAnimation(rotateClockwise)
        else ivExpand?.clearAnimation()
        subMenuContainer?.setVisible(false)
    }

    private fun inflateDivider() {
        inflateLayoutWithOutAttachingIt(
            R.layout.menu_item_divider,
            subMenuContainer as LinearLayout
        )
    }
}

sealed class MenuItem {
    class Regular(val titleRes: Int, val iconRes: Int, val onClick: () -> Unit) : MenuItem()
    class Expandable(val titleRes: Int, val iconRes: Int) : MenuItem()
    class Submenu(val subItems: List<SubItem>) : MenuItem()
}

data class SubItem(val titleRes: Int, val onClick: () -> Unit)
