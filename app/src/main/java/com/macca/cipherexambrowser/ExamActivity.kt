package com.macca.cipherexambrowser
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.macca.cipherexambrowser.databinding.ActivityExamBinding

class ExamActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExamBinding
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var buttonRefresh: Button
    private lateinit var buttonExit: Button
    private lateinit var buttonLayout: View

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Log.d("ExamActivity", "onCreate: Start")


        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE or WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_SECURE or WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        binding = ActivityExamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = binding.webView
        progressBar = binding.progressBar
        buttonRefresh = binding.buttonRefresh
        buttonExit = binding.buttonExit
        buttonLayout = binding.buttonLayout

        // Konfigurasi WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

        // Set WebViewClient untuk menangani tautan dalam aplikasi
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Menonaktifkan tombol back jika tidak ada halaman sebelumnya di WebView
                buttonExit.isEnabled = webView.canGoBack()
            }
        }


        // Set WebChromeClient untuk menangani progres halaman
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = newProgress
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
        // Load URL ke WebView
        Log.d("ExamActivity", "onCreate: Loading URL")
        webView.loadUrl(MainActivity.decryptedUrl)

        // Tombol Refresh
        buttonRefresh.setOnClickListener {
            webView.reload()
        }

        // Tombol Exit
        buttonExit.setOnClickListener {
            if (webView.canGoBack()) {
                // Jika ada halaman sebelumnya, kembali ke halaman sebelumnya di WebView
                webView.goBack()
            } else {
                // Jika tidak ada halaman sebelumnya, tampilkan dialog peringatan
                val alertdialog: AlertDialog.Builder = AlertDialog.Builder(this)
                alertdialog.setTitle("Peringatan")
                alertdialog.setMessage("Apakah anda telah selesai mengerjakan soal, dan ingin keluar ???")
                alertdialog.setPositiveButton("Iya") { _, _ ->
                    val intent = Intent(this@ExamActivity, MainActivity::class.java)
                    startActivity(intent)
                    this@ExamActivity.finish()
                }
                alertdialog.setNegativeButton("Tidak") { dialog, _ -> dialog.cancel() }
                alertdialog.create()
                alertdialog.show()
            }        }

        Log.d("ExamActivity", "onCreate: End")
    }

    // Handle tombol back untuk navigasi di dalam WebView
    override fun onBackPressed() {
        super.onBackPressed()
        // Mencegah tombol back untuk keluar dari aplikasi
        val alertdialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertdialog.setTitle("Peringatan")
        alertdialog.setMessage("Apakah anda telah selesai mengerjakan soal, dan ingin keluar ???")
        alertdialog.setPositiveButton(
            "Iya"
        ) { _, _ ->
            val intent = Intent(this@ExamActivity, MainActivity::class.java)
            startActivity(intent)
            this@ExamActivity.finish()
        }
        alertdialog.setNegativeButton(
            "Tidak"
        ) { dialog, _ -> dialog.cancel() }
        alertdialog.create()
        alertdialog.show()
    }

        override fun onPause() {
            super.onPause()
            fun showPasswordDialog() {
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
                    } else {
                        Toast.makeText(this, "Password Salah", Toast.LENGTH_SHORT).show()
                        showPasswordDialog() // Memanggil ulang dialog jika password salah
                    }
                }

                val dialog = builder.create()
                dialog.setCancelable(false) // Tidak bisa dibatalkan (cancelable)
                dialog.setCanceledOnTouchOutside(false) // Tidak bisa dibatalkan dengan klik di luar dialog
                dialog.show()
            }

// Panggil metode showPasswordDialog() untuk menampilkan dialog password
            showPasswordDialog()
        }
    }

