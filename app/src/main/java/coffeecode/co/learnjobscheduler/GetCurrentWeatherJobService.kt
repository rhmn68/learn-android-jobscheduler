package coffeecode.co.learnjobscheduler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.text.DecimalFormat


class GetCurrentWeatherJobService : JobService() {

    companion object{
        val TAG = GetCurrentWeatherJobService::class.java.simpleName
        const val APP_ID = "a6d3678de683ec9de8944b08ff50e349"
        const val CITY = "Bandung"
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        Log.d(TAG, "onStopJob() Executed")
        return true
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob() Executed")
        getCurrentWeather(p0)
        return true
    }

    private fun getCurrentWeather(job: JobParameters?){
        Log.d(TAG, "Running")
        val client = AsyncHttpClient()
        val url = "http://api.openweathermap.org/data/2.5/weather?q=$CITY&appid=$APP_ID"
        Log.e(TAG, "getCurrentWeather: $url")

        client.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                val result = responseBody?.let { String(it) }
                Log.d(TAG, result)
                try {
                    val responObject = JSONObject(result)
                    val currentWeather = responObject.getJSONArray("weather").getJSONObject(0).getString("main")
                    val description = responObject.getJSONArray("weather").getJSONObject(0).getString("description")
                    val tempInKelvin = responObject.getJSONObject("main").getDouble("temp")

                    val tempInCelcius = tempInKelvin - 273
                    val temprature = DecimalFormat("##.##").format(tempInCelcius)
                    val title = "Current Weather"
                    val message = "$currentWeather, $description with $temprature celcius"
                    val notifId = 100

                    showNotification(applicationContext, title, message, notifId)
                    jobFinished(job, false)
                }catch (e: Exception){
                    jobFinished(job, true)
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                // ketika proses gagal, maka jobFinished diset dengan parameter true. Yang artinya job perlu di reschedule
                jobFinished(job, true)
            }

        })
    }

    private fun showNotification(context: Context?, title: String, message: String, notifId: Int) {
        val CHANNEL_ID = "Channel_1"
        val CHANNEL_NAME = "Job scheduler channel"

        val notificationManagerCompat = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_replay_black_24dp)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context, android.R.color.black))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmSound)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            builder.setChannelId(CHANNEL_ID)
            notificationManagerCompat.createNotificationChannel(channel)
        }
        val notification = builder.build()
        notificationManagerCompat.notify(notifId, notification)
    }

}