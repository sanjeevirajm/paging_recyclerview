import android.content.Context

object Util {
    private var isNetAvailableMutable = true

    val isNetAvailable: Boolean
        get() = isNetAvailableMutable

    fun toggleNetAvailability() {
        isNetAvailableMutable = !isNetAvailableMutable
    }

    fun convertDpToPixels(context: Context, dp: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
}
