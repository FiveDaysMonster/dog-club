package com.example.lcd3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class show_question extends AppCompatActivity {

    String questionID;
    ArrayList<String> a_tags, al_message, al_name, al_time;
    FirebaseDatabase firebaseDatabase;
    TextView t_tag, t_title, t_content, t_time, t_leave_message;
    Integer i_tagNum;
    String s_title, s_content, s_time, s_nickname;
    Integer i_leave_message;
    Button b_leave_message;
    MyListView myListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_question);

        firebaseDatabase = FirebaseDatabase.getInstance();
        a_tags = new ArrayList<String>();
        al_message = new ArrayList<String>();
        al_message.add("message1");al_message.add("message2");al_message.add("message3");
        al_time = new ArrayList<String>();
        al_time.add("2021/6/3 10:11");al_time.add("2021/6/3 10:14");al_time.add("2021/6/3 10:18");
        al_name = new ArrayList<String>();
        al_name.add("member1");al_name.add("member2");al_name.add("member3");
        t_tag = (TextView) findViewById(R.id.t_showQuestion_tag);
        t_title = (TextView) findViewById(R.id.t_showQuestion_title);
        t_content = (TextView) findViewById(R.id.t_showQuestion_content);
        t_time = (TextView) findViewById(R.id.t_showQuestion_time);
        t_leave_message = (TextView) findViewById(R.id.messageQuestion_num);
        b_leave_message = (Button) findViewById(R.id.b_leave_Questionmessage);
        myListView = (MyListView) findViewById(R.id.list_showQuestion);


        noStatusBar();
        Intent intent = getIntent();
        questionID = intent.getStringExtra("question_id");
        s_nickname = intent.getStringExtra("user_nickName");

        get_question_data();
        myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(show_question.this);
                builder.setTitle("發布")
                        .setMessage("是否將此做為問題解決方案")
                        .setNegativeButton("否", null)
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                                DatabaseReference databaseReference = firebaseDatabase.getReference("accounts");
                                databaseReference.child("112004179482517820474").child("get_message").child("m1").setValue("您的問題已得到回覆，內容如下：\nmessage2");
                                databaseReference.child("112004179482517820474").child("show_from_message").setValue(1);
                                Toast.makeText(show_question.this,"為題已發布",Toast.LENGTH_SHORT);
                            }
                        })
                        .show();
                return true;
            }
        });
        //bt_event();


    }

    private void noStatusBar(){
        getSupportActionBar().hide(); //隱藏標題
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態
    }
    private void get_question_data(){
        DatabaseReference databaseReference = firebaseDatabase.getReference("questions");
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful())
                    get_question_data();
                else{
                    for (DataSnapshot ds : task.getResult().getChildren()){
                        if(ds.getKey().equals(questionID)){
                            for(DataSnapshot ds2 : ds.getChildren()){
                                if(ds2.getKey().equals("tags")){
                                    for(DataSnapshot ds3 : ds2.getChildren()){
                                        a_tags.add(ds3.getValue().toString());
                                    }
                                }
                                else if(ds2.getKey().equals("tags_num"))
                                    i_tagNum = Integer.parseInt(ds2.getValue().toString());
                                else if(ds2.getKey().equals("title"))
                                    s_title = ds2.getValue().toString();
                                else if(ds2.getKey().equals("content"))
                                    s_content = ds2.getValue().toString();
                                else if(ds2.getKey().equals("time"))
                                    s_time = ds2.getValue().toString();
                                else if(ds2.getKey().equals("leave_message"))
                                    i_leave_message = Integer.parseInt(ds2.getValue().toString());
                            }
                        }
                    }

                    String s = "";
                    for(int i=0;i<i_tagNum;i++){
                        s = s+"#"+a_tags.get(i);
                    }
                    t_tag.setText(s);
                    t_title.setText(s_title);
                    t_content.setText(s_content);
                    t_time.setText(s_time);
                    if(i_leave_message == 0){
                        t_leave_message.setText("尚未有留言");
                    }
                    else{
                        t_leave_message.setText("留言(3)");
                        myListView.setAdapter(new myListAdapter());
                    }
                }
            }
        });
    }
    EditText e;
    private void bt_event(){
        b_leave_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View myAlert = LayoutInflater.from(show_question.this).inflate(R.layout.alert_leave_message,null);
                e = (EditText) myAlert.findViewById(R.id.e_leave_message);
                AlertDialog.Builder builder = new AlertDialog.Builder(show_question.this);
                builder.setTitle("留言")
                        .setView(myAlert)
                        .setNegativeButton("取消",null)
                        .setPositiveButton("發佈", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(!e.getText().toString().equals("")){
                                    DatabaseReference d = firebaseDatabase.getReference("questions");

                                    d.child(questionID).child("messages").child("m"+String.valueOf(i_leave_message+1)).setValue(e.getText().toString());
                                    d.child(questionID).child("leave_message").setValue(i_leave_message+1);
                                    d.child(questionID).child("leave_message_names").child("n" + String.valueOf(i_leave_message + 1)).setValue(s_nickname);
                                    Calendar calendar = Calendar.getInstance();
                                    String myTime = "";
                                    myTime = String.valueOf(calendar.get(Calendar.YEAR))+"-"
                                            +String.valueOf(calendar.get(Calendar.MONTH)+1)+"-"
                                            +String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"  "
                                            +String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))+":"
                                            +String.valueOf(calendar.get(Calendar.MINUTE));
                                    d.child(questionID).child("leave_message_times").child("t" + String.valueOf(i_leave_message + 1)).setValue(myTime);
                                    al_message.add(e.getText().toString());
                                    i_leave_message+=1;
                                    System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"+i_leave_message);
                                    al_name.add(s_nickname);
                                    al_time.add(myTime);
                                    t_leave_message.setText("留言 ("+i_leave_message+")");
                                    Toast.makeText(show_question.this,"發佈成功",Toast.LENGTH_SHORT).show();

                                }else{
                                    Toast.makeText(show_question.this,"輸入不可為空",Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
            }
        });
    }

    private class myListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return al_message.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //handler.sendEmptyMessage(656463);
            View v = getLayoutInflater().inflate(R.layout.l_leave_message,null);

            TextView t_name = v.findViewById(R.id.t_leave_message_name);
            TextView t_time = v.findViewById(R.id.t_leave_message_time);
            TextView t_message = v.findViewById(R.id.t_leave_message_content);
            t_name.setText(al_name.get(position));
            t_time.setText(al_time.get(position));
            t_message.setText(al_message.get(position));

            return v;
        }
    }


}