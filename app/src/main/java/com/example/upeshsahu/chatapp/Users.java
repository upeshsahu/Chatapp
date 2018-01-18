package com.example.upeshsahu.chatapp;

import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Users extends AppCompatActivity {
    TextView noUsersText;
    public static Button btn;
    ArrayList<String> onlinelist = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;
    Firebase  userrefrence,allrefrence;

    @Override
    protected void onDestroy() {

            super.onDestroy();
            userrefrence.child("online").setValue("offline");allrefrence.child(UserDetails.chatWith).child("online").setValue("offline");
     }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
     getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        noUsersText = (TextView)findViewById(R.id.noUsersText);
        btn=(Button)findViewById(R.id.startbtn);

        Firebase.setAndroidContext(this);
        userrefrence=new Firebase("https://fir-c0525.firebaseio.com/users/"+UserDetails.username);
        allrefrence=new Firebase("https://fir-c0525.firebaseio.com/users");
        pd = new ProgressDialog(Users.this);
        pd.setMessage("Loading...");
        pd.show();

        allrefrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onlinelist.clear();
                    for(DataSnapshot d:dataSnapshot.getChildren())
                    {

                        try {
                            String name=d.getKey();
                            if(UserDetails.username!=name)
                      { //  System.out.print("name is "+name);
                              String s=d.getValue().toString();
                                System.out.print("string is  "+s);
                                JSONObject obj = new JSONObject(s);

                                System.out.print("online is  is "+obj.getString("online"));
                                if("free".equals(obj.getString("chatting")) && "online".equals(obj.getString("online")) && !name.equals(UserDetails.username))
                                {
                                    onlinelist.add(name);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    for(int i=0;i<onlinelist.size();i++)
                    {
                        System.out.println("list is "+onlinelist.get(i));
                    }



            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        String url = "https://fir-c0525.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(Users.this);
        rQueue.add(request);





        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("this button is pressesd");
             if(btn.getText().toString().equals("start another chat") || (btn.getText().toString().equals("start chat ")))
             {
                 userrefrence.child("chatting").setValue("free");
                 connecttochat();
             }
             else if((btn.getText().toString().equals("return to chat")))
                {

                    startActivity(new Intent(Users.this, Chat.class));
                }
            }
        });
    }

    public void doOnSuccess(String s){
        try {

            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();

            String key = "";
            while(i.hasNext()){
                key = i.next().toString();
                String str=obj.getString(key);
                JSONObject objstr = new JSONObject(str);
                    if(objstr.getString("online").equals("online") &&  objstr.getString("chatting").equals("free")  && !key.equals(UserDetails.username)) {
                        onlinelist.add(key);
                    }
                totalUsers++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(totalUsers <=1){
            noUsersText.setVisibility(View.VISIBLE);
            //usersList.setVisibility(View.GONE);
        }
        else{
            noUsersText.setVisibility(View.GONE);

        }

        pd.dismiss();
    }

    public void connecttochat()
    {
        Random r=new Random();
        int k,s=onlinelist.size();
        if(s!=0)
        {
             k=r.nextInt(onlinelist.size());
            System.out.print("random integer is "+k+" ");
            UserDetails.chatWith=onlinelist.get(k);
            System.out.println("you are randomly connected to "+UserDetails.chatWith);
            startActivity(new Intent(Users.this, Chat.class));
            Toast.makeText(Users.this, "you are connected to "+ UserDetails.chatWith, Toast.LENGTH_SHORT).show();
        }

    }
}