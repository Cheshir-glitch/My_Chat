package com.example.my_chat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DiscussionActivity extends AppCompatActivity {
    Button btnSengMsg;
    EditText etMsg;
    ListView lvDiscussions;
    ArrayList<String> listConversation = new ArrayList<>();
    ArrayAdapter arrayAdapt;
    private DatabaseReference dbr;
    String Username , SelectedTopic , user_msg_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);


        btnSengMsg = (Button) findViewById(R.id.btnSendMsg);
        etMsg = (EditText) findViewById(R.id.etMessage);

        lvDiscussions = (ListView) findViewById(R.id.lvConversation);

        arrayAdapt = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listConversation);
        lvDiscussions.setAdapter(arrayAdapt );


        Username = getIntent().getExtras().get("user_name").toString();
        SelectedTopic = getIntent().getExtras().get("selected_topic").toString();
        setTitle("Topic :" + SelectedTopic);

        dbr = FirebaseDatabase.getInstance().getReference().child(SelectedTopic);
         btnSengMsg.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Map<String,Object> map  = new HashMap<String,Object>();
                 user_msg_key = dbr.push().getKey();
                 dbr.updateChildren(map);
                 DatabaseReference dbr_s = dbr.child(user_msg_key);
                 Map<String,Object> map_s = new HashMap<String,Object>();
                 map_s.put("msg",etMsg.getText().toString());
                 map_s.put("user", Username);
                 dbr_s.updateChildren(map_s);
                 etMsg.getText().clear();

             }
         });


           dbr.addChildEventListener(new ChildEventListener() {
                 @Override
                 public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                     updateConversation(dataSnapshot);
                 }

                 @Override
                 public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                     updateConversation(dataSnapshot);
                 }

                 @Override
                 public void onChildRemoved(DataSnapshot dataSnapshot) {

                 }

                 @Override
                 public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                 }
             });

         }


         public void updateConversation(DataSnapshot dataSnapshot){
             String msg, user , conversation;
             Iterator i = dataSnapshot.getChildren().iterator();
             while (i.hasNext()){
                msg =(String) ((DataSnapshot)i.next()).getValue();
                user = (String) ((DataSnapshot)i.next()).getValue();


                conversation = user +  ':' + ' ' + msg;
                arrayAdapt.insert(conversation, arrayAdapt.getCount());
                arrayAdapt.notifyDataSetChanged();
             }
         }

}
