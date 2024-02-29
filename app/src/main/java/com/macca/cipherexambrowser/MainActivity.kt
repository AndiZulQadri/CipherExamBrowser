package com.macca.cipherexambrowser

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.macca.cipherexambrowser.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    private val layeredCipher = LayeredCipher()
    private lateinit var binding: ActivityMainBinding
    companion object {
        var decryptedUrl: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Memeriksa ketersediaan koneksi internet saat aplikasi pertama kali dibuka
        if (!isInternetAvailable()) {
            showNoInternetDialog()
        }
        

            binding.buttonStartExam.setOnClickListener {
                startLockTaskIfNeeded()
                val intent = Intent(this, ExamBro::class.java)
                startActivity(intent)
            }


            binding.buttonPinApp.setOnClickListener {
                try {
                    startLockTaskIfNeeded()
                } catch (e: SecurityException) {
                    Snackbar.make(binding.root, "Pinning failed: ${e.message}", Snackbar.LENGTH_SHORT).show()
                }
            }




            binding.buttonExit.setOnClickListener {
                try {
                    stopLockTaskIfNeeded()
                    clearApplicationCache()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    finish() // Close the activity
                }
            }

            binding.buttonPaste.setOnClickListener {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = clipboard.primaryClip

                if (clipData != null && clipData.itemCount > 0) {
                    val pastedText = clipData.getItemAt(0).text.toString()
                    // Set hasil paste ke dalam EditText
                    binding.editTextManualLink.setText(pastedText)
                    // Misalnya, set teks pada suatu TextView
                    binding.textViewCurrentLink.text = pastedText
                } else {
                    // Clipboard kosong
                    Toast.makeText(this, "Clipboard kosong", Toast.LENGTH_SHORT).show()
                }
            }

            binding.buttonEncrypt.setOnClickListener {
            val inputUrl = binding.editTextManualLink.text.toString()
            if (inputUrl.isNotEmpty()) {
                val decryptedUrl = layeredCipher.decrypt(inputUrl)
                MainActivity.decryptedUrl = decryptedUrl // Set decryptedUrl di MainActivity
                binding.textViewCurrentLink.text = "$decryptedUrl"
            } else {
                Toast.makeText(this, "url kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Tidak Ada Koneksi Internet")
        builder.setMessage("Aplikasi memerlukan koneksi internet. Aktifkan koneksi internet sekarang?")
        builder.setPositiveButton("Aktifkan") { dialogInterface: DialogInterface, _: Int ->
            // Buka pengaturan koneksi untuk mengaktifkan internet
            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("Tutup Aplikasi") { _, _ ->
            // Tutup aplikasi jika pengguna memilih untuk tidak mengaktifkan internet
            finish()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun stopLockTaskIfNeeded() {
        stopLockTask()
    }

    private fun startLockTaskIfNeeded() {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (!am.isInLockTaskMode) {
                startLockTask()
            }
        } else {
            if (am.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_NONE) {
                startLockTask()
            }
        }
    }

    private fun clearApplicationCache() {
        try {
            val cacheDir = cacheDir
            if (cacheDir != null) {
                val applicationDir = File(cacheDir.parent)
                if (applicationDir.exists()) {
                    val children = applicationDir.list()
                    for (s in children) {
                        if (s != "lib") {
                            deleteDir(File(applicationDir, s))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteDir(dir: File): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children!!.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        return dir.delete()
    }
}
