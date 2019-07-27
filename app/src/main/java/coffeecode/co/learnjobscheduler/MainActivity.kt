package coffeecode.co.learnjobscheduler

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val jobId = 15

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onClick()
    }

    private fun onClick() {
        btn_start.setOnClickListener {
            startJob()
        }

        btn_cancel.setOnClickListener {
            cancelJob()
        }
    }

    private fun cancelJob() {
        val tm = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        tm.cancel(jobId)

        Toast.makeText(this, "Job service canceled", Toast.LENGTH_SHORT).show()

        finish()
    }


    /**
    setRequiredNetworkType,
    ketika bernilai NETWORK_TYPE_ANY berarti tidak ada ketentuan dia harus terhubung ke network jenis tertentu.
    Bila kita ingin memasang ketentuan bahwa job hanya akan berjalan ketika perangkat terhubung ke network Wi-fi,
    maka kita perlu memberikan nilai NETWORK_TYPE_UNMETERED.

    setRequiresDeviceIdle,
    menentukan apakah job akan dijalankan ketika perangkat dalam keadaan sedang digunakan atau tidak.
    Secara default, parameter ini bernilai false. Bila kita ingin job dijalankan ketika perangkat dalam kondisi
    tidak digunakan, maka kita beri nilai true.

    setRequiresCharging,
    menentukan apakah job akan dijalankan ketika batere sedang diisi atau tidak.
    Nilai true akan mengindikasikan bawah job hanya berjalan ketika batere sedang diisi. Kondisi ini dapat
    digunakan bila job yang dijalankan akan memakan waktu yang lama, sehingga membutuhkan batere yang relatif
    tidak terisi baterai

    setPeriodic,
    set berapa interval waktu kapan job akan dijalankan. Ini bisa kita gunakan untuk menjalankan job yang
    sifatnya repeat atau berulang. Nilai parameter yang kita masukkan adalah dalam milisecond,
    dan 1000 ms adalah 1 detik.

    setOverrideDeadline,
    set waktu deadline job itu akan di jalankan. Jika kita menggunakan ketentuan ini,
    maka jika dalam waktu yang telah kita tentukan job masih belum berjalan maka job
    tersebut akan di paksa untuk dijalankan.

    dan ketentuan lainnya yang bisa kita gunakan. Misalnya
    setPersisted, setMinimumLatency, dll.
    Perlu di ingat bahwa tidak semua ketentuan bisa kita gunakan secara bersama-sama
    karena sifatnya yang bertolak belakang.

    Misalnya setPeriodic tidak bisa digunakan bersamaan dengan setOverrideDeadline.
    */
    private fun startJob() {
        //1000ms = 1 detik
        val seconds = 1000
        val minute = 60 * seconds

        val mServiceComponent = ComponentName(this, GetCurrentWeatherJobService::class.java)
        val builder = JobInfo.Builder(jobId, mServiceComponent)
                // Periode interval sampai ke trigger
                // Dalam milisecond, 1000ms = 1detik
                .setPeriodic((minute * 15).toLong())
                // Kondisi network,
                // NETWORK_TYPE_ANY, berarti tidak ada ketentuan tertentu
                // NETWORK_TYPE_UNMETERED, adalah network yang tidak dibatasi misalnya wifi
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                // Kondisi device, secara default sudah pada false
                // false, berarti device tidak perlu idle ketika job ke trigger
                // true, berarti device perlu dalam kondisi idle ketika job ke trigger
                .setRequiresDeviceIdle(false)
                // Kondisi charging
                // false, berarti device tidak perlu di charge
                // true, berarti device perlu dicharge
                .setRequiresCharging(false)

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(builder.build())

        Toast.makeText(this, "Job service started", Toast.LENGTH_SHORT).show()
    }
}
