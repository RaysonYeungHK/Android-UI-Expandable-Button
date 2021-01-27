package com.codedeco.lib.ui.widget.button

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.setPadding
import androidx.databinding.DataBindingUtil
import com.codedeco.lib.ui.R
import com.codedeco.lib.ui.databinding.WidgetExpandableButtonBinding


class ExpandableButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding: WidgetExpandableButtonBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.widget_expandable_button,
            this,
            true
    )

    private val defaultStrokeWidth: Int = 0
    private val defaultStrokeColor: ColorStateList? = null
    private val defaultTint: ColorStateList? = binding.button.backgroundTintList
    private val defaultIconTint: ColorStateList? = binding.button.iconTint
    private val defaultTextColors: ColorStateList? = binding.button.textColors
    private val defaultTextStyle: Int = Typeface.NORMAL
    private val defaultGravity: Int = Gravity.CENTER
    private val defaultFadeIn: Boolean = false
    private val defaultScaleIn: Boolean = false
    private val defaultExpanded: Boolean = false
    private val defaultAnimationDuration: Long = 300L

    private val fadeInAnimator: ObjectAnimator = ObjectAnimator.ofFloat(binding.button, View.ALPHA, 0f, 1f)
    private val scaleInXAnimator: ObjectAnimator = ObjectAnimator.ofFloat(binding.button, View.SCALE_X, 0f, 1f)
    private val scaleInYAnimator: ObjectAnimator = ObjectAnimator.ofFloat(binding.button, View.SCALE_Y, 0f, 1f)
    private val _layoutTransition: LayoutTransition = LayoutTransition().apply {
        enableTransitionType(LayoutTransition.CHANGING)
    }

    /**
     * Flag to indicate the size of the view is calculated
     */
    private var initialized: Boolean = false

    /**
     * State of the view
     */
    var isExpanded: Boolean = false
        private set

    var strokeWidth: Int = defaultStrokeWidth
        set(value) {
            field = value
            binding.button.strokeWidth = value
        }
    var strokeColor: ColorStateList? = defaultStrokeColor
        set(value) {
            field = value
            binding.button.strokeColor = value
        }
    var tint: ColorStateList? = defaultTint
        set(value) {
            field = value
            binding.button.backgroundTintList = value
        }
    var icon: Drawable? = null
        set(value) {
            field = value
            binding.button.icon = value
        }
    var iconTint: ColorStateList? = defaultIconTint
        set(value) {
            field = value
            binding.button.iconTint = value
        }

    var text: String? = null
        set(value) {
            field = value
            // We need to reset the initialized flag to recalculate the width of expanded button
            initialized = false
            binding.button.text = value
        }

    var textColors: ColorStateList? = defaultTextColors
        set(value) {
            field = value
            binding.button.setTextColor(value)
        }

    var textStyle: Int = defaultTextStyle
        set(value) {
            field = value
            binding.button.setTypeface(binding.button.typeface, value)
        }

    var fadeIn: Boolean = defaultFadeIn
    var scaleIn: Boolean = defaultScaleIn

    var animationDuration: Long = defaultAnimationDuration
        set(value) {
            field = value
            fadeInAnimator.duration = value
            scaleInXAnimator.duration = value
            scaleInYAnimator.duration = value
            _layoutTransition.setDuration(value)
        }

    init {
        if (attrs != null) {
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.ExpandableButton, defStyleAttr, 0)

            this.strokeWidth = attributes.getDimension(R.styleable.ExpandableButton_strokeWidth, defaultStrokeWidth.toFloat()).toInt()
            this.strokeColor = if (attributes.hasValue(R.styleable.ExpandableButton_strokeColor)) {
                attributes.getColorStateList(R.styleable.ExpandableButton_strokeColor)
            } else {
                defaultStrokeColor
            }
            this.tint = if (attributes.hasValue(R.styleable.ExpandableButton_android_tint)) {
                attributes.getColorStateList(R.styleable.ExpandableButton_android_tint)
            } else {
                defaultTint
            }
            this.icon = attributes.getResourceId(R.styleable.ExpandableButton_android_icon, 0)
                    .takeIf {
                        it != 0
                    }?.let {
                        AppCompatResources.getDrawable(context, it)
                    }
            this.iconTint = if (attributes.hasValue(R.styleable.ExpandableButton_iconTint)) {
                attributes.getColorStateList(R.styleable.ExpandableButton_iconTint)
            } else {
                defaultIconTint
            }
            this.text = attributes.getText(R.styleable.ExpandableButton_android_text)?.toString()
            this.textColors = if (attributes.hasValue(R.styleable.ExpandableButton_android_textColor)) {
                attributes.getColorStateList(R.styleable.ExpandableButton_android_textColor)
            } else {
                defaultTextColors
            }
            this.textStyle = attributes.getInt(R.styleable.ExpandableButton_android_textStyle, defaultTextStyle)
            this.fadeIn = attributes.getBoolean(R.styleable.ExpandableButton_fadeIn, defaultFadeIn)
            this.scaleIn = attributes.getBoolean(R.styleable.ExpandableButton_scaleIn, defaultScaleIn)
            this.isExpanded = attributes.getBoolean(R.styleable.ExpandableButton_expanded, defaultExpanded)
            this.animationDuration = attributes.getInt(R.styleable.ExpandableButton_android_animationDuration, defaultAnimationDuration.toInt()).toLong()

            attributes.recycle()
        }
        show()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (binding.root.parent != binding.root) {
            (binding.root.parent as ViewGroup).layoutTransition = _layoutTransition
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        // Forward the click event to the button itself
        binding.button.setOnClickListener(l)
    }

    fun show() {
        _layoutTransition.setDuration(0)
        collapse()
        binding.button.post {
            if (scaleIn) {
                scaleInXAnimator.start()
                scaleInYAnimator.start()
            }
            if (fadeIn) {
                fadeInAnimator.start()
            }
            binding.root.postDelayed({
                _layoutTransition.setDuration(this@ExpandableButton.animationDuration)
                if (isExpanded) {
                    expand()
                }
            }, if (scaleIn || fadeIn) {
                this@ExpandableButton.animationDuration
            } else {
                0
            })
        }
    }

    fun setExpanded(isExpanded: Boolean) {
        this.isExpanded = isExpanded
        if (isExpanded) {
            expand()
        } else {
            collapse()
        }
    }

    private fun expand() {
        if (text.isNullOrBlank()) {
            return
        }
        binding.button.apply {
            // Modify the padding to prevent icon position shift
            iconPadding = dpToPx(8)
            setPadding(dpToPx(11), 0, dpToPx(16), 0)
            text = this@ExpandableButton.text
        }
    }

    private fun collapse() {
        binding.button.apply {
            // Modify the padding to prevent icon position shift
            iconPadding = 0
            setPadding(0)
            text = ""
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}