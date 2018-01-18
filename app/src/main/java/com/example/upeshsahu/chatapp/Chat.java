package com.example.upeshsahu.chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Chat extends AppCompatActivity {
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2,userrefrence,connectedRef,chattingref,chattingto;
    Button endchat;
    boolean chatoption=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)findViewById(R.id.layout2);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        endchat=(Button)findViewById(R.id.endchat);

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://fir-c0525.firebaseio.com/message/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://fir-c0525.firebaseio.com/message/" + UserDetails.chatWith + "_" + UserDetails.username);
        userrefrence=new Firebase("https://fir-c0525.firebaseio.com/users/"+UserDetails.username);
        connectedRef=new Firebase("https://fir-c0525.firebaseio.com/.info/connected");
        chattingto=new Firebase("https://fir-c0525.firebaseio.com/users/"+UserDetails.chatWith);



        userrefrence.child("chatting").setValue(UserDetails.chatWith);
        chattingto.child("chatting").setValue(UserDetails.username);

//        userrefrence.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot!=null)
//                {
//                  //  userrefrence.child("online").onDisconnect().setValue("offline");
//                    Log.i("discnnect is working ","yuppppppp");
//                }
//            }
//
//
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });



//        connectedRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.getValue(Boolean.class))
//                {
//                   // userrefrence.child("online").onDisconnect().setValue("offline");
//                }
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });

//        connectedRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                boolean connected = snapshot.getValue(Boolean.class);
//                if (connected) {
//                    System.out.println("connected");
//                    Log.i("connection","online");
//                } else {
//                    System.out.println("not connected");
//                    Log.i("connection","offline");
//                }
//            }
//
//            @Override
//            public void onCancelled(FirebaseError error) {
//                System.err.println("Listener was cancelled");
//            }
//        });
//
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });




        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if(userName.equals(UserDetails.username)){
                    addMessageBox(  message, 1);
                }
                else{
                  //  addMessageBox(UserDetails.chatWith + ":-\n" + message, 2);
                    addMessageBox( message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });





        endchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               chatoption=true;
                Toast.makeText(Chat.this,"the chat is ended",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        chattingto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("some one else data chaanges");

                String str=dataSnapshot.getValue().toString();
                JSONObject obj = null;
                try {
                    obj = new JSONObject(str);
                    if(obj.getString("online").toString().equals("offline") || obj.getString("chatting").toString().equals("free") && !chatoption)
                    { System.out.println("she is gone");
                        chatoption=true;
                        Toast.makeText(Chat.this,"user went offline",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
 }

    @Override
    protected void onDestroy() {
        if(chatoption)
        {
            Users.btn.setText("start another chat");
        }
        else
        {
            Users.btn.setText("return to chat");
        }

        userrefrence.child("chatting").setValue("free");
        chattingto.child("chatting").setValue("free");
        super.onDestroy();
    }




    public void addMessageBox(String message, int type){

        TextView textView = new TextView(Chat.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }



}
