package davidfdez.capteuratmospherique;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

public abstract class AbstractChartsActivity extends AppCompatActivity {
    protected String user;

    protected void configureWebView(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Toast.makeText(getApplicationContext(), consoleMessage.message() + " At line " + consoleMessage.lineNumber(), Toast.LENGTH_LONG).show();
                return super.onConsoleMessage(consoleMessage);
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getIntent().getExtras().getString("user");
    }
}
