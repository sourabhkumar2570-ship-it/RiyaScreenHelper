package com.riya.screenhelper;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScreenReaderService extends AccessibilityService {
    private static volatile String latestText = "RIYA Screen Reader is on. Open another app to read visible text.";
    private ServerSocket server;
    private Thread serverThread;
    private final AtomicBoolean running = new AtomicBoolean(false);

    @Override public void onServiceConnected() {
        super.onServiceConnected();
        startLocalServer();
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;
        int type = event.getEventType();
        if (type != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
            type != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            type != AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) return;
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;
        StringBuilder out = new StringBuilder();
        collect(root, out, 0);
        root.recycle();
        String value = out.toString().trim();
        if (!value.isEmpty()) latestText = value.length() > 12000 ? value.substring(0, 12000) : value;
    }

    private void collect(AccessibilityNodeInfo node, StringBuilder out, int depth) {
        if (node == null || depth > 35 || out.length() > 12000) return;
        if (node.isPassword()) return;
        CharSequence text = node.getText();
        CharSequence desc = node.getContentDescription();
        appendUnique(out, text);
        if (desc != text) appendUnique(out, desc);
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                collect(child, out, depth + 1);
                child.recycle();
            }
        }
    }

    private void appendUnique(StringBuilder out, CharSequence value) {
        if (value == null) return;
        String s = value.toString().trim();
        if (s.isEmpty() || out.indexOf(s) >= 0) return;
        if (out.length() > 0) out.append('\n');
        out.append(s);
    }

    private void startLocalServer() {
        if (!running.compareAndSet(false, true)) return;
        serverThread = new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(8765, 10, java.net.InetAddress.getByName("127.0.0.1"))) {
                server = ss;
                while (running.get()) {
                    try (Socket client = ss.accept()) {
                        client.setSoTimeout(2000);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                        String line = reader.readLine();
                        while (line != null && !line.isEmpty()) line = reader.readLine();
                        JSONObject body = new JSONObject();
                        body.put("text", latestText);
                        byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
                        OutputStream os = client.getOutputStream();
                        String headers = "HTTP/1.1 200 OK\r\nContent-Type: application/json; charset=utf-8\r\nContent-Length: " + bytes.length + "\r\nConnection: close\r\n\r\n";
                        os.write(headers.getBytes(StandardCharsets.UTF_8)); os.write(bytes); os.flush();
                    } catch (Exception e) { Log.w("RiyaScreen", "client", e); }
                }
            } catch (Exception e) { Log.e("RiyaScreen", "local server stopped", e); }
            finally { running.set(false); }
        }, "RiyaLocalScreenBridge");
        serverThread.setDaemon(true);
        serverThread.start();
    }

    @Override public void onInterrupt() { latestText = "RIYA Screen Reader interrupted."; }
    @Override public void onDestroy() {
        running.set(false);
        try { if (server != null) server.close(); } catch (Exception ignored) {}
        super.onDestroy();
    }
}
