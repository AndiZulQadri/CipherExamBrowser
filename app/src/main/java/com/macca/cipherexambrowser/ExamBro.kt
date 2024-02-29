package com.macca.cipherexambrowser

import android.annotation.SuppressLint
import android.app.PictureInPictureUiState
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog

class ExamBro : AppCompatActivity() {
    private var passwordAttempts = 0
    private val maxPasswordAttempts = 3

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam_bro)
        // Full screen dan secure screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE or WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_SECURE or WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Inisialisasi komponen
        val buttonRefresh: ImageButton = findViewById(R.id.buttonRefresh)
        val buttonExit: ImageButton = findViewById(R.id.buttonExit)

        // Konfigurasi WebView
        val webBrow: WebView = findViewById(R.id.webExam)
        val webSettings = webBrow.settings
        true.also { webSettings.javaScriptEnabled = it }
        webSettings.domStorageEnabled = true
        webBrow.loadUrl(MainActivity.decryptedUrl)

        // Set OnClickListener untuk tombol refresh
        buttonRefresh.setOnClickListener {
            webBrow.reload()
        }

        // Set OnClickListener untuk tombol exit
        buttonExit.setOnClickListener {
            showExitDialog()
        }

    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val webBrow: WebView = findViewById(R.id.webExam)
        if (webBrow.canGoBack()) {
            // Jika WebView dapat kembali, kembali ke halaman sebelumnya
            webBrow.goBack()
        } else {
            // Jika tidak dapat kembali, tampilkan peringatan exit
            showExitDialog()
        }
    }

    override fun onPause() {
        super.onPause()
        showPasswordDialog()

    }

/*    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (isInPictureInPictureMode) {
            // Aplikasi sedang dalam mode Picture-in-Picture
            // Tutup mode Picture-in-Picture
            showExitDialog()
            enterPictureInPictureMode()
        }
    }*/

/*    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        if (isInPictureInPictureMode) {
            // Aplikasi sedang dalam mode Picture-in-Picture
            // Tutup mode Picture-in-Picture dan fokus kembali ke aplikasi
            showExitDialog()
            enterPictureInPictureMode()
        }
    }*/

    private fun showPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Menhadap ke pengawas Ruang")
        builder.setMessage("Jangan keluar dari aplikasi saat mengerjakan soal!!!")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val enteredPassword = input.text.toString()
            val desiredPassword = "40304519" // Ganti dengan password yang diinginkan

            if (enteredPassword == desiredPassword) {
                // Password benar, lakukan tindakan yang diinginkan
                // TODO: Tambahkan tindakan yang diinginkan di sini
                dialog.dismiss()
                passwordAttempts = 0 // Reset jumlah percobaan jika password benar
            } else {
                Toast.makeText(this, "Password Salah", Toast.LENGTH_SHORT).show()
                passwordAttempts++

                if (passwordAttempts >= maxPasswordAttempts) {
                    // Jika jumlah percobaan melebihi batas maksimal, tutup halaman ujian
                    finish()
                } else {
                    showPasswordDialog() // Memanggil ulang dialog jika password salah
                }
            }
        }

        val dialog = builder.create()
        dialog.setCancelable(false) // Tidak bisa dibatalkan (cancelable)
        dialog.setCanceledOnTouchOutside(false) // Tidak bisa dibatalkan dengan klik di luar dialog
        dialog.show()
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Exit")
            .setMessage("Apakah ujian telah selesai?")
            .setPositiveButton("Ya") { _, _ ->
                finish()
            }
            .setNegativeButton("Tidak", null)
            .show()    }
}