package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BlogspotHtmlViewer(
    htmlSnippet: String,
    modifier: Modifier = Modifier,
    onDownloadRequested: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }

    // Prepare full styled HTML document with dark theme and mobile responsiveness
    val fullStyledHtml = remember(htmlSnippet) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                body {
                    background-color: #0b0f19;
                    color: #e2e8f0;
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                    padding: 12px;
                    margin: 0;
                    font-size: 14px;
                    line-height: 1.6;
                }
                h1, h2, h3 {
                    color: #f8fafc;
                    font-weight: 700;
                }
                a {
                    color: #00e5ff;
                    text-decoration: none;
                }
                img {
                    max-width: 100%;
                    height: auto;
                    border-radius: 8px;
                    margin: 8px 0;
                }
                code, pre {
                    background: #151d2f;
                    color: #38bdf8;
                    font-family: monospace;
                    padding: 4px 8px;
                    border-radius: 6px;
                    font-size: 13px;
                    border: 1px solid #222f46;
                }
                ul, ol {
                    padding-left: 20px;
                }
                li {
                    margin-bottom: 6px;
                }
                .blogspot-badge {
                    display: inline-block;
                    background: #0284c7;
                    color: white;
                    padding: 3px 8px;
                    border-radius: 4px;
                    font-size: 11px;
                    font-weight: bold;
                    margin-bottom: 8px;
                }
            </style>
        </head>
        <body>
            $htmlSnippet
        </body>
        </html>
        """.trimIndent()
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    setBackgroundColor(0xFF0B0F19.toInt())

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            isLoading = true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                        }

                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                            val targetUrl = request?.url?.toString() ?: return false
                            if (targetUrl.endsWith(".zip") || targetUrl.endsWith(".mcaddon") || targetUrl.endsWith(".mcpack")) {
                                onDownloadRequested?.invoke()
                                return true
                            }
                            try {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                ctx.startActivity(browserIntent)
                            } catch (e: Exception) {
                                // Ignore
                            }
                            return true
                        }
                    }
                    loadDataWithBaseURL("https://gtamc-modhub.blogspot.com", fullStyledHtml, "text/html", "UTF-8", null)
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL("https://gtamc-modhub.blogspot.com", fullStyledHtml, "text/html", "UTF-8", null)
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
