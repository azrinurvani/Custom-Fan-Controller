package com.example.android.customfancontroller

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

//TODO: Step 1.5 Add a top-level enum to represent the available fan speeds
private enum class FanSpeed(val label: Int) {
    OFF(R.string.fan_off),
    LOW(R.string.fan_low),
    MEDIUM(R.string.fan_medium),
    HIGH(R.string.fan_high);

    //TODO: Step 2.0 dd an extension function next() that changes the current fan speed to the next speed in the list
    fun next() = when (this){
        OFF -> LOW
        LOW -> MEDIUM
        MEDIUM -> HIGH
        HIGH -> OFF
    }
}
//TODO: Step 1.6 Add these constants. You'll use these as part of drawing the dial indicators and labels.
private const val RADIUS_OFFSET_LABEL = 30
private const val RADIUS_OFFSET_INDICATOR = -35

//TODO: Step 1.4 Create a new Kotlin class called DialView
//The @JvmOverloads annotation instructs the Kotlin compiler to generate overloads for this function
// that substitute default parameter values.
class DialView @JvmOverloads constructor(
    context : Context,
    attrs: AttributeSet? = null,
    defStyleAttr : Int = 0
) : View(context,attrs,defStyleAttr){

    //TODO: Step 1.7 Inside the DialView class, define several variables you need in order to draw the custom view.
    private var radius = 0.0f //Radius of the circle
    private var fanSpeed = FanSpeed.OFF // as a default FAN Speed init with OFF
    // position variable which will be used to draw label and indicator circle position
    private val pointPosition : PointF = PointF(0.0f,0.0f)


    //TODO: Step 3.2 declare variables to cache the attribute values.
    private var fanSpeedLowColor = 0
    private var fanSpeedMediumColor = 0
    private var fanSeedMaxColor = 0

    //TODO: Step 1.8 Inside the DialView class definition, initialize a Paint object with a handful of basic styles.
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("",Typeface.BOLD)
    }

    //TODO: Step 2.1 Setting the view's isClickable property to true enables that view to accept user input.
    init {
        isClickable = true

        //TODO: Step 3.3 add the following code using the withStyledAttributes extension function. You supply the attributes and view, and and set your local variables.
        context.withStyledAttributes(attrs, R.styleable.DialView) {
            fanSpeedLowColor = getColor(R.styleable.DialView_fanColor1, 0)
            fanSpeedMediumColor = getColor(R.styleable.DialView_fanColor2, 0)
            fanSeedMaxColor = getColor(R.styleable.DialView_fanColor3, 0)
        }

    }

    //TODO: Step 2.2 override the performClick()
    override fun performClick(): Boolean {
        if (super.performClick()) return true

        fanSpeed = fanSpeed.next()
        contentDescription = resources.getString(fanSpeed.label)

        invalidate()
        return true
    }

    //TODO: Step 1.9 override the onSizeChanged() method from the View class to calculate the size for the custom view's dial.
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = (min(width,height)/2.0 * 0.8).toFloat()
    }

    //TODO: Step 1.10 Add this code to define a computeXYForSpeed() extension function for the PointF class.
    private fun PointF.computeXYForSpeed(pos : FanSpeed, radius : Float){
        // Angles are in radians.
        val startAngle = Math.PI * (9/8.0)
        val angle = startAngle + pos.ordinal * (Math.PI/4)
        x = (radius * cos(angle).toFloat() + width/2)
        x = (radius * sin(angle).toFloat() + height/2)
    }

    //TODO: Step 1.11 Override the onDraw() method to render the view on the screen with the Canvas and Paint classes
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //TODO: Step 1.12 Inside onDraw() add this line to set the paint color to gray (Color.GRAY) or green (Color.GREEN) depending on whether the fan speed is OFF or any other value.
        // Set dial background color to green if selection not off.
        paint.color =
            if (fanSpeed == FanSpeed.OFF)
                Color.GRAY
            else
                Color.GREEN

        //TODO: Step 1.13 Add this code to draw a circle for the dial, with the drawCircle() method.
        //The width and height properties are members of the View superclass and indicate the current dimensions of the view.
        canvas?.drawCircle((width/2).toFloat(),(height/2).toFloat(),radius,paint)

        //TODO: Step 1.14 Add this code to draw a smaller circle for the fan speed indicator mark, also with the drawCircle() method.
        // Draw the indicator circle.
        val marketRadius = radius + RADIUS_OFFSET_INDICATOR
        pointPosition.computeXYForSpeed(fanSpeed,marketRadius)
        paint.color = Color.BLACK
        canvas?.drawCircle(pointPosition.x,pointPosition.y,radius/12,paint)

        //TODO: Step 1.15 Draw the fan speed labels (0, 1, 2, 3) at the appropriate positions around the dial.
        //This part of the method calls PointF.computeXYForSpeed() again to get the position for each label, and reuses the pointPosition object each time to avoid allocations.
        //Draw Text Labels
        val labelRadius = radius + RADIUS_OFFSET_LABEL
        for (i in FanSpeed.values()){
            pointPosition.computeXYForSpeed(i,labelRadius)
            val label = resources.getString(i.label)
            canvas?.drawText(label,pointPosition.x,pointPosition.y,paint)
        }

        //TODO: Step 3.5 You can use the local variables in your code. In onDraw() to set the dial color based on the current fan speed.
        paint.color = when (fanSpeed) {
            FanSpeed.OFF -> Color.GRAY
            FanSpeed.LOW -> fanSpeedLowColor
            FanSpeed.MEDIUM -> fanSpeedMediumColor
            FanSpeed.HIGH -> fanSeedMaxColor
        } as Int

    }
}