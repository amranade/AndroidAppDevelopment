package com.example.simplechat;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String CHAT_SOCKET_URI = "ws://192.168.29.34:1337";
    ChatWebSocket chatWebSocket;
    EditText mChatTextView;
    TextView mChatHistoryView;
    TextView mChatLabelView;

    @Nullable
    String mName;
    int mColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChatTextView = findViewById(R.id.chat_text);
        mChatHistoryView = findViewById(R.id.chat_history);
        mChatLabelView = findViewById(R.id.chat_label);

        mChatTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String message = s.toString();
                if (message.length() == 0) return;
                if (message.charAt(message.length() - 1) == '\n') {
                    message = message.substring(0, message.length() - 1);
                    chatWebSocket.send(message);
//                    mChatTextView.setText("");
                    s.clear();
                    // First text is name; // HACKY
                    if (mName == null) {
                        mName = message;
                    }
                }
            }
        });

        try {
            URI chatUri = new URI(CHAT_SOCKET_URI);
            chatWebSocket = new ChatWebSocket(chatUri);
            chatWebSocket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private class ChatWebSocket extends WebSocketClient {
        public ChatWebSocket(URI serverURI) {
            super(serverURI);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mName == null) {
                        mChatLabelView.setText(getString(R.string.choose_name));
                    }
                    mChatTextView.setEnabled(true);
                }
            });
        }

        @Override
        public void onMessage(final String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        switch (jsonObject.getString("type")) {
                            case "color":
                                String colorStr = jsonObject.getString("data");
                                mColor = parseColorFromStr(colorStr);
                                mChatLabelView.setTextColor(mColor);
                                mChatLabelView.setText(mName);
//                        mChatTextView.requestFocus();
                                break;
                            case "history":
                                JSONArray history = jsonObject.getJSONArray("data");
                                setHistory(history);
                                break;
                            case "message":
                                JSONObject messageObj = jsonObject.getJSONObject("data");
                                addMessage(messageObj);
                                break;
                            default:
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onClose(int i, String s, boolean b) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mChatTextView.setText(getString(R.string.closed_server));
                    mChatTextView.setEnabled(false);
                }
            });
        }

        @Override
        public void onError(Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mChatTextView.setText(getString(R.string.error_server));
                    mChatTextView.setEnabled(false);
                }
            });
        }
    }

    private void setHistory(JSONArray history) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            for (int i = 0;i< history.length(); ++i) {
                JSONObject jsonObject = new JSONObject(history.getString(i));
                addMessage(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addMessage(JSONObject messageObj) {
        try {
            String author = messageObj.getString("author");
            String text = messageObj.getString("text");
            String color = messageObj.getString("color");
            String time = messageObj.getString("time");
            Date date = new Date(Long.parseLong(time));
            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            String dateStr = df.format(date);
            Spannable meta = new SpannableString(author + " @ " + dateStr + ": ");
            meta.setSpan(new ForegroundColorSpan(parseColorFromStr(color)),
                    0, meta.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mChatHistoryView.append(meta);
            mChatHistoryView.append(text);
            mChatHistoryView.append("\n");



//            Integer.toHexString(parseColorFromStr(color)).toUpperCase();
//            String htmlStr = "<font color=" + color + ">" +
//                    author + " @ " + time + " : " +
//                    "</font>" +
//                    text + '\n';
//            String messageSpan = Html.escapeHtml(htmlStr);
//            mChatHistoryView.append(Html.fromHtml(messageSpan));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int parseColorFromStr(String colorStr) {
        switch (colorStr) {
            case "red":
                return Color.RED;
            case "green":
                return Color.GREEN;
            case "blue":
                return Color.BLUE;
            case "magenta":
                return Color.MAGENTA;
            case "orange":
            case "purple":
            case "plum":
            default:
                return Color.BLACK;
        }
    }
}
