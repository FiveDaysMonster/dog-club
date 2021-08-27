package com.example.lcd3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.example.lcd3.R.array.dog_list;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ui_level_3 extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;

    LinearLayout lay_homepage, lay_forum, lay_question, lay_book, lay_message;
    Integer now_view = 2, last_view = 2;
    ImageView img_homepage, img_forum, img_question, img_book, img_message;

    //----------------------------------------------
    ViewPager viewPager;
    ArrayList<View> myViews;
    myAdapter myA;
    View v1,v2,v3,v4,v5;
    //----------------------------------------------

    String nickName, gender, identity, userID, account_level = "3";

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    TextView t_CalendarText, t_memberText, t_homepageText;

    private static String CALANDER_URL = "content://com.android.calendar/calendars";
    private static String CALANDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALANDER_REMIDER_URL = "content://com.android.calendar/reminders";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_level3);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        noStatusBar();
        init();
        vp_init();
        click_event();
        get_question_data();
        vp_listener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        userID = account.getId();
        if(userID.equals("112004179482517820474")){
            img_book.setImageResource(R.drawable.book1);
            t_CalendarText.setText("圖鑑");
            img_message.setImageResource(R.drawable.message1);
            t_memberText.setText("訊息");
            img_homepage.setImageResource(R.drawable.homepage1);
            t_homepageText.setText("首頁");
        }else if(userID.equals("116063306527075552878")){
            img_book.setImageResource(R.drawable.calendar);
            t_CalendarText.setText("排班");
            img_message.setImageResource(R.drawable.member1);
            t_memberText.setText("成員");
            img_homepage.setImageResource(R.drawable.chart1);
            t_homepageText.setText("犬隻食量");
        }
        System.out.println("_____________________"+userID);
        DatabaseReference databaseReference = firebaseDatabase.getReference("accounts");
        databaseReference.child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    for(DataSnapshot ds : task.getResult().getChildren()){
                        if(ds.getKey().equals("account_gender")){
                            gender = ds.getValue().toString();
                        }
                        else if(ds.getKey().equals("account_identity")){
                            identity = ds.getValue().toString();
                        }
                        else if(ds.getKey().equals("account_level")){
                            account_level = ds.getValue().toString();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void vp_init(){
        viewPager = (ViewPager) findViewById(R.id.myVP);
        myViews = new ArrayList<View>();
        myA = new myAdapter();
        myViews.add(new vp_homepage(ui_level_3.this));
        myViews.add(new vp_forum(ui_level_3.this));
        myViews.add(new vp_question(ui_level_3.this));
        myViews.add(new vp_book(ui_level_3.this));
        myViews.add(new vp_message(ui_level_3.this));

        viewPager.setAdapter(myA);
        viewPager.setCurrentItem(1);

    }

    private void vp_listener(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                now_view = position + 1;
                if(last_view == 1){
                    if(userID.equals("112004179482517820474")){
                        img_homepage.setImageResource(R.drawable.homepage1);
                    }else if(userID.equals("116063306527075552878")){
                        img_homepage.setImageResource(R.drawable.chart1);
                    }
                }
                else if(last_view == 2)
                    img_forum.setImageResource(R.drawable.forum1);
                else if (last_view == 3)
                    img_question.setImageResource(R.drawable.question1);
                else if(last_view == 4){
                    if(userID.equals("112004179482517820474")){
                        img_book.setImageResource(R.drawable.book1);
                    }else if(userID.equals("116063306527075552878")){
                        img_book.setImageResource(R.drawable.calendar);
                    }
                }
                else{
                    if(userID.equals("112004179482517820474")){
                        img_message.setImageResource(R.drawable.message1);
                    }else if(userID.equals("116063306527075552878")){
                        img_message.setImageResource(R.drawable.member1);
                    }
                }

                if(now_view == 1){
                    if(userID.equals("112004179482517820474")){
                        img_homepage.setImageResource(R.drawable.homepage2);
                    }else if(userID.equals("116063306527075552878")){
                        img_homepage.setImageResource(R.drawable.chart2);
                    }
                }
                else if(now_view == 2)
                    img_forum.setImageResource(R.drawable.forum2);
                else if (now_view == 3)
                    img_question.setImageResource(R.drawable.question2);
                else if(now_view == 4){
                    if(userID.equals("112004179482517820474")){
                        img_book.setImageResource(R.drawable.book2);
                    }else if(userID.equals("116063306527075552878")){
                        img_book.setImageResource(R.drawable.calendar2);
                    }
                }
                else{
                    if(userID.equals("112004179482517820474")){
                        img_message.setImageResource(R.drawable.message2);
                    }else if(userID.equals("116063306527075552878")){
                        img_message.setImageResource(R.drawable.member2);
                    }
                }

                last_view = now_view;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void init(){
        firebaseDatabase = FirebaseDatabase.getInstance();

        lay_homepage = (LinearLayout) findViewById(R.id.lay_homepage);
        lay_forum = (LinearLayout) findViewById(R.id.lay_forum);
        lay_question = (LinearLayout) findViewById(R.id.lay_question);
        lay_book = (LinearLayout) findViewById(R.id.lay_book);
        lay_message = (LinearLayout) findViewById(R.id.lay_message);

        img_homepage = (ImageView) findViewById(R.id.img_homepage);
        img_forum = (ImageView) findViewById(R.id.img_forum);
        img_question = (ImageView) findViewById(R.id.img_question);
        img_book = (ImageView) findViewById(R.id.img_book);

        img_message = (ImageView) findViewById(R.id.img_message);

        t_CalendarText = (TextView) findViewById(R.id.ttt_book);
        t_memberText = (TextView) findViewById(R.id.ttt_member);
        t_homepageText = (TextView) findViewById(R.id.ttt_homepage);

        //----------
        al_year = new ArrayList<String>();
        al_month = new ArrayList<String>();
        al_day = new ArrayList<String>();
        al_hour = new ArrayList<String>();
        al_minute = new ArrayList<String>();
        al_tag = new ArrayList<String>();
        al_title = new ArrayList<String>();
        al_content = new ArrayList<String>();
        al_timestamp = new ArrayList<String>();
        al_clickTimes = new ArrayList<String>();
        al_clickMale = new ArrayList<String>();
        al_clickFemale = new ArrayList<String>();
        al_clickStudent = new ArrayList<String>();
        al_clickTeacher = new ArrayList<String>();
        al_clickOthers = new ArrayList<String>();
        al_violation = new ArrayList<String>();

        alQ_timestamp = new ArrayList<String>();
        alQ_title = new ArrayList<String>();
        alQ_content = new ArrayList<String>();
        alQ_time = new ArrayList<String>();
        alQ_tag = new ArrayList<ArrayList<String>>();
        alQ_clickTime = new ArrayList<String>();
        alQ_tags_num = new ArrayList<Integer>();

    }

    private void noStatusBar(){
        getSupportActionBar().hide(); //隱藏標題
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態
    }

    private void click_event(){
        lay_homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                now_view = 1;
                viewPager.setCurrentItem(0);
                change_button();
                //Toast.makeText(ui_level_3.this,"homepage",Toast.LENGTH_SHORT).show();
            }
        });
        lay_forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                now_view = 2;
                viewPager.setCurrentItem(1);
                change_button();
                //Toast.makeText(ui_level_3.this,"forum",Toast.LENGTH_SHORT).show();
            }
        });
        lay_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                now_view = 3;
                viewPager.setCurrentItem(2);
                change_button();
                //Toast.makeText(ui_level_3.this,"question",Toast.LENGTH_SHORT).show();
            }
        });
        lay_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                now_view = 4;
                viewPager.setCurrentItem(3);
                change_button();
                Toast.makeText(ui_level_3.this,"book",Toast.LENGTH_SHORT).show();
            }
        });
        lay_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                now_view = 5;
                viewPager.setCurrentItem(4);
                change_button();
                //Toast.makeText(ui_level_3.this,"message",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void change_button(){
        //112004179482517820474
        //116063306527075552878
        if(last_view == 1){
            if(userID.equals("112004179482517820474")){
                img_homepage.setImageResource(R.drawable.homepage1);
            }else if(userID.equals("116063306527075552878")){
                img_homepage.setImageResource(R.drawable.chart1);
            }
        }
        else if(last_view == 2)
            img_forum.setImageResource(R.drawable.forum1);
        else if (last_view == 3)
            img_question.setImageResource(R.drawable.question1);
        else if(last_view == 4){
            if(userID.equals("112004179482517820474")){
                img_book.setImageResource(R.drawable.book1);
            }else if(userID.equals("116063306527075552878")){
                img_book.setImageResource(R.drawable.calendar);
            }
        }
        else{
            if(userID.equals("112004179482517820474")){
                img_message.setImageResource(R.drawable.message1);
            }else if(userID.equals("116063306527075552878")){
                img_message.setImageResource(R.drawable.member1);
            }
        }


        if(now_view == 1){
            if(userID.equals("112004179482517820474")){
                img_homepage.setImageResource(R.drawable.homepage2);
            }else if(userID.equals("116063306527075552878")){
                img_homepage.setImageResource(R.drawable.chart2);
            }
        }
        else if(now_view == 2)
            img_forum.setImageResource(R.drawable.forum2);
        else if (now_view == 3)
            img_question.setImageResource(R.drawable.question2);
        else if(now_view == 4){
            if(userID.equals("112004179482517820474")){
                img_book.setImageResource(R.drawable.book2);
            }else if(userID.equals("116063306527075552878")){
                img_book.setImageResource(R.drawable.calendar2);
            }
        }
        else{
            if(userID.equals("112004179482517820474")){
                img_message.setImageResource(R.drawable.message2);
            }else if(userID.equals("116063306527075552878")){
                img_message.setImageResource(R.drawable.member2);
            }
        }

        last_view = now_view;
    }

    private class myAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return myViews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position,
                                @NonNull Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(myViews.get(position));
            listener_homepage();
            listener_forum();
            listener_question();
            listener_book();
            listener_message();
            return myViews.get(position);
        }
    }

    LinearLayout homepage_l1, homepage_l2;
    Button b_search;
    TextView t_search;
    NumberPicker np_year, np_month, np_name;
    String[] dog_name = {"威士比","皮皮","小黑","芬達","黑木耳","嚕嚕"};
    LineChart myLineChart;
    ArrayList a_data;
    ArrayList ax,ay;
    LineDataSet s1;

    private void listener_homepage(){
        View v = myViews.get(0);

        a_data = new ArrayList();
        ax = new ArrayList();
        ay = new ArrayList();
        myLineChart = (LineChart) v.findViewById(R.id.mylinechart);
        initX(31);
        initY();
        np_year = (NumberPicker) v.findViewById(R.id.pick_year);
        np_month = (NumberPicker) v.findViewById(R.id.pick_month);
        np_name = (NumberPicker) v.findViewById(R.id.pick_dog_name);
        np_year.setMinValue(2000);
        np_year.setMaxValue(2021);
        np_year.setValue(2020);
        np_month.setMaxValue(12);
        np_month.setMinValue(1);
        np_month.setValue(5);
        np_name.setDisplayedValues(dog_name);
        np_name.setMinValue(0);
        np_name.setMaxValue(dog_name.length-1);
        homepage_l1 = (LinearLayout) v.findViewById(R.id.homepage_l1);
        homepage_l2 = (LinearLayout) v.findViewById(R.id.homepage_l2);
        b_search = (Button) v.findViewById(R.id.b_search);
        t_search = (TextView) v.findViewById(R.id.dogEatTime);
        //-------------------------------------------------

        Description description=myLineChart.getDescription();
        description.setText("犬隻食量");
        description.setTextColor(Color.parseColor("#ffffff"));
        myLineChart.setNoDataText("no Data");

        a_data.add(new Entry(1,1.3f));
        a_data.add(new Entry(2,1.1f));
        a_data.add(new Entry(3,1.2f));
        a_data.add(new Entry(4,1.2f));
        a_data.add(new Entry(5,0.8f));
        a_data.add(new Entry(6,1.2f));
        a_data.add(new Entry(7,1.4f));
        a_data.add(new Entry(8,0.5f));
        a_data.add(new Entry(9,0.4f));
        a_data.add(new Entry(10,0.7f));
        a_data.add(new Entry(11,0.5f));
        a_data.add(new Entry(12,0.6f));
        a_data.add(new Entry(13,1.1f));
        a_data.add(new Entry(14,1.2f));
        a_data.add(new Entry(15,1.1f));
        a_data.add(new Entry(16,1.1f));
        a_data.add(new Entry(17,1.3f));
        a_data.add(new Entry(18,1.2f));
        a_data.add(new Entry(19,1.3f));
        a_data.add(new Entry(20,1.1f));
        a_data.add(new Entry(21,0.8f));
        a_data.add(new Entry(22,0.9f));
        a_data.add(new Entry(23,1.1f));
        a_data.add(new Entry(24,1.1f));
        a_data.add(new Entry(25,1.2f));
        a_data.add(new Entry(26,1.3f));
        a_data.add(new Entry(27,1.2f));
        a_data.add(new Entry(28,1.1f));
        a_data.add(new Entry(29,1.1f));
        a_data.add(new Entry(30,0.9f));
        a_data.add(new Entry(31,1.1f));

        s1 = new LineDataSet(a_data,"單位(匙)");
        s1.setValueTextColor(Color.parseColor("#ffffff"));
        s1.setColor(Color.rgb(255,0,0));//線的顏色
        s1.setCircleColor(Color.rgb(255,0,0));//圓點顏色
        s1.setMode(LineDataSet.Mode.LINEAR);
        s1.setCircleRadius(2);//圓點大小
        s1.setDrawCircleHole(false);//實心圓
        s1.setLineWidth(1.5f);//線的粗細
        s1.setValueTextSize(10f);//圓點字大小
        s1.setValueFormatter(new DefaultValueFormatter(1));//小數位數
        s1.setHighlightLineWidth(2f);//十字線寬度

        LineData lineData=new LineData(s1);
        Legend l1=myLineChart.getLegend();
        l1.setTextColor(Color.parseColor("#ffffff"));
        myLineChart.setData(lineData);
        myLineChart.invalidate();

        //-------------------------------------------------

        if(userID.equals("112004179482517820474")){
            homepage_l1.setVisibility(View.VISIBLE);
            homepage_l2.setVisibility(View.INVISIBLE);
        }else if(userID.equals("116063306527075552878")){
            homepage_l1.setVisibility(View.INVISIBLE);
            homepage_l2.setVisibility(View.VISIBLE);
        }
        b_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLineChart.setVisibility(View.VISIBLE);
            }
        });


    }
    public void initX(Integer m){
        XAxis xAxis=myLineChart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//x軸顯示在下方
        xAxis.setTextSize(12);//x軸文字大小
        xAxis.setGranularity(1);//x軸間隔
        xAxis.setAxisLineWidth(2);//x軸寬度
        xAxis.setAxisMaximum(m);//x軸最大座標
        xAxis.setAxisMinimum(1);//x軸最小座標
        xAxis.setTextColor(Color.parseColor("#ffffff"));

    }
    public void initY(){
        YAxis rightAxis=myLineChart.getAxisRight();
        YAxis leftAxis=myLineChart.getAxisLeft();

        rightAxis.setTextColor(Color.BLUE);//y軸文字顏色
        rightAxis.setTextSize(12);//y軸文字大小
        rightAxis.setGranularity(1);//y軸間隔
        rightAxis.setAxisLineWidth(1);//y軸寬度
        rightAxis.setAxisMinimum(0);
        rightAxis.setTextColor(Color.parseColor("#ffffff"));

        leftAxis.setTextColor(Color.BLUE);
        leftAxis.setTextSize(12);
        leftAxis.setGranularity(1);
        leftAxis.setAxisLineWidth(1);
        leftAxis.setAxisMinimum(0);
        leftAxis.setTextColor(Color.parseColor("#ffffff"));


    }
    //--------------------------
    ArrayList<String> al_year, al_month, al_day, al_hour, al_minute
            , al_tag, al_title, al_content
            , al_timestamp, al_clickTimes, al_clickMale, al_clickFemale, al_violation
            , al_clickStudent, al_clickTeacher, al_clickOthers;
    ListView l_articles;

    private void get_article_data(){

        DatabaseReference databaseReference = firebaseDatabase.getReference("articles");
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    get_article_data();
                }
                else {
                    al_violation.clear();
                    al_year.clear();al_month.clear();al_day.clear();al_hour.clear();al_minute.clear();
                    al_tag.clear();al_title.clear();al_content.clear();al_timestamp.clear();
                    for(DataSnapshot ds : task.getResult().getChildren()){
                        al_timestamp.add(ds.getKey());
                        for(DataSnapshot ds2 : ds.getChildren()){
                            if (ds2.getKey().equals("year"))
                                al_year.add(ds2.getValue().toString());
                            else if(ds2.getKey().equals("month"))
                                al_month.add(ds2.getValue().toString());
                            else if(ds2.getKey().equals("day"))
                                al_day.add(ds2.getValue().toString());
                            else if(ds2.getKey().equals("hour"))
                                al_hour.add(ds2.getValue().toString());
                            else if(ds2.getKey().equals("minute"))
                                al_minute.add(ds2.getValue().toString());
                            else if(ds2.getKey().equals("tags"))
                                al_tag.add(ds2.getValue().toString());
                            else if(ds2.getKey().equals("title"))
                                al_title.add(ds2.getValue().toString());
                            else if(ds2.getKey().equals("content"))
                                al_content.add(ds2.getValue().toString());
                            else if(ds2.getKey().equals("click_times"))
                                al_clickTimes.add(ds2.getValue().toString());
                            else if(ds2.getKey().equals("click_male"))
                                al_clickMale.add(ds2.getValue().toString());
                            else if(ds2.getKey().equals("click_female"))
                                al_clickFemale.add(ds2.getValue().toString());
                            else if(ds2.getKey().equals("click_student"))
                                al_clickStudent.add(ds2.getValue().toString());
                            else if(ds2.getKey().equals("click_teacher"))
                                al_clickTeacher.add(ds2.getValue().toString());
                            else if(ds2.getKey().equals("click_others"))
                                al_clickOthers.add(ds2.getValue().toString());
                            else if(ds2.getKey().equals("violation"))
                                al_violation.add(ds2.getValue().toString());
                        }
                    }
                }
            }
        });
    }

    private void listener_forum(){
        View v = myViews.get(1);
        Button b_addArticle = (Button) v.findViewById(R.id.b_addArticle);
        l_articles = (ListView) v.findViewById(R.id.list_article);
        get_article_data();
        l_articles.setAdapter(new myForumAdapter());
        b_addArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent = new Intent(ui_level_3.this,add_new_article_edit_ui.class);
                 intent.putExtra("nick_name",nickName);
                 startActivity(intent);
                 finish();
            }
        });


        l_articles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DatabaseReference databaseReference = firebaseDatabase.getReference("articles");
                //click times
                databaseReference.child(al_timestamp.get(position)).child("click_times")
                        .setValue(Integer.parseInt(al_clickTimes.get(position))+1);
                al_clickTimes.set(position,String.valueOf(Integer.parseInt(al_clickTimes.get(position))+1));
                //click gender
                if(gender.equals("1")){
                    //male
                    databaseReference.child(al_timestamp.get(position)).child("click_male")
                            .setValue(Integer.parseInt(al_clickMale.get(position))+1);
                    al_clickMale.set(position,String.valueOf(Integer.parseInt(al_clickMale.get(position))+1));
                }
                else{
                    //female
                    databaseReference.child(al_timestamp.get(position)).child("click_female")
                            .setValue(Integer.parseInt(al_clickFemale.get(position))+1);
                    al_clickFemale.set(position,String.valueOf(Integer.parseInt(al_clickFemale.get(position))+1));
                }

                //click identity
                if(identity.equals("1")){
                    //student
                    databaseReference.child(al_timestamp.get(position)).child("click_student")
                            .setValue(Integer.parseInt(al_clickStudent.get(position))+1);
                    al_clickStudent.set(position,String.valueOf(Integer.parseInt(al_clickStudent.get(position))+1));
                }
                else if(identity.equals("2")){
                    //student
                    databaseReference.child(al_timestamp.get(position)).child("click_teacher")
                            .setValue(Integer.parseInt(al_clickTeacher.get(position))+1);
                    al_clickTeacher.set(position,String.valueOf(Integer.parseInt(al_clickTeacher.get(position))+1));
                }
                else{
                    //student
                    databaseReference.child(al_timestamp.get(position)).child("click_others")
                            .setValue(Integer.parseInt(al_clickOthers.get(position))+1);
                    al_clickOthers.set(position,String.valueOf(Integer.parseInt(al_clickOthers.get(position))+1));
                }
                get_article_data();

                l_articles.setAdapter(new myForumAdapter());
                Intent intent = new Intent(ui_level_3.this,show_article.class);
                intent.putExtra("articleID",al_timestamp.get(position));
                startActivity(intent);
            }
        });

        l_articles.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(account_level.equals("3")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ui_level_3.this);
                    builder.setTitle("檢舉")
                            .setMessage("是否檢舉此文章")
                            .setNegativeButton("否",null)
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    article_violation(position);
                                }
                            })
                            .show();
                    return true;
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ui_level_3.this);
                    builder.setTitle("刪除")
                            .setMessage("是否刪除此文章")
                            .setNegativeButton("否",null)
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    article_delete(position);
                                    get_article_data();
                                    l_articles.setAdapter(new myForumAdapter());
                                }
                            })
                            .show();
                    return true;
                }
            }
        });
    }
    //-------------------------------

    CheckBox c_dogHurtHuman, c_dogGotHurt, c_dogRunCar, c_others;
    EditText e_others_question, e_title_question, e_content_question;
    Button b_send_question;
    ListView question_list;
    LinearLayout l_myquestion;

    ArrayList<String> alQ_timestamp, alQ_title, alQ_content, alQ_time, alQ_clickTime;
    ArrayList<ArrayList<String>> alQ_tag;
    ArrayList<Integer> alQ_tags_num;

    LinearLayout l1,l2;
    private void listener_question(){


        View v = myViews.get(2);

        c_dogHurtHuman = (CheckBox) v.findViewById(R.id.c_dogHurtHuman);
        c_dogGotHurt = (CheckBox) v.findViewById(R.id.c_dogGotHurt);
        c_dogRunCar = (CheckBox) v.findViewById(R.id.c_dogRunCar);
        c_others = (CheckBox) v.findViewById(R.id.c_others);
        e_others_question = (EditText) v.findViewById(R.id.e_questionKind);
        e_title_question = (EditText) v.findViewById(R.id.e_title_question);
        e_content_question = (EditText) v.findViewById(R.id.e_content_question);
        b_send_question = (Button) v.findViewById(R.id.b_announce_question);
        question_list = (ListView) v.findViewById(R.id.question_list);



        l_myquestion = (LinearLayout) v.findViewById(R.id.my_question_linearlayout);


        if(account_level.equals("3")){
            question_list.setVisibility(View.INVISIBLE);
            l_myquestion.setVisibility(View.VISIBLE);
        }else{
            question_list.setVisibility(View.VISIBLE);
            l_myquestion.setVisibility(View.INVISIBLE);
            question_list.setAdapter(new question_adapter());
        }

        question_list_listener();

        show_other_editText();
        send_question();
    }
    private void show_other_editText(){
        c_others.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(c_others.isChecked()){
                    e_others_question.setVisibility(View.VISIBLE);
                }else{
                    e_others_question.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    private void send_question(){
        b_send_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(c_dogHurtHuman.isChecked() || c_dogGotHurt.isChecked() || c_dogRunCar.isChecked() || (c_others.isChecked()&&!e_others_question.equals(""))){
                    if(!e_title_question.getText().toString().equals("") && !e_content_question.getText().toString().equals("")){
                        ArrayList tags = new ArrayList();
                        tags.clear();
                        DatabaseReference databaseReference = firebaseDatabase.getReference("questions");
                        String questionID = String.valueOf(System.currentTimeMillis());
                        Calendar calendar = Calendar.getInstance();
                        databaseReference.child(questionID).child("title").setValue(e_title_question.getText().toString());
                        databaseReference.child(questionID).child("content").setValue(e_content_question.getText().toString());
                        if(c_dogHurtHuman.isChecked())
                            tags.add(c_dogHurtHuman.getText().toString());
                        if(c_dogGotHurt.isChecked())
                            tags.add(c_dogGotHurt.getText().toString());
                        if(c_dogRunCar.isChecked())
                            tags.add(c_dogRunCar.getText().toString());
                        if(c_others.isChecked())
                            tags.add(e_others_question.getText().toString());
                        databaseReference.child(questionID).child("tags_num").setValue(tags.size());
                        for(int i=1;i<=tags.size();i++){
                            databaseReference.child(questionID).child("tags").child("tag"+String.valueOf(i)).setValue(tags.get(i-1));
                        }
                        databaseReference.child(questionID).child("questioner").setValue(userID);
                        databaseReference.child(questionID).child("leave_message").setValue(0);
                        databaseReference.child(questionID).child("questioner_nickName").setValue(nickName);
                        databaseReference.child(questionID).child("year").setValue(calendar.get(Calendar.YEAR));
                        databaseReference.child(questionID).child("month").setValue(calendar.get(Calendar.MONTH)+1);
                        databaseReference.child(questionID).child("day").setValue(calendar.get(Calendar.DAY_OF_MONTH));
                        databaseReference.child(questionID).child("hour").setValue(calendar.get(Calendar.HOUR_OF_DAY));
                        databaseReference.child(questionID).child("click_time").setValue(0);
                        databaseReference.child(questionID).child("minute").setValue(calendar.get(Calendar.MINUTE));
                        databaseReference.child(questionID).child("time").setValue(
                            String.valueOf(calendar.get(Calendar.YEAR)) + "-"
                                + String.valueOf(calendar.get(Calendar.MONTH)+1) + "-"
                                + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "  "
                                + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + ":"
                                + String.valueOf(calendar.get(Calendar.MINUTE))
                        );
                        c_dogHurtHuman.setChecked(false);
                        c_dogGotHurt.setChecked(false);
                        c_dogRunCar.setChecked(false);
                        c_others.setChecked(false);
                        e_others_question.setText("");
                        e_title_question.setText("");
                        e_content_question.setText("");
                    }
                    else
                        Toast.makeText(ui_level_3.this,"尚未填寫完成",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(ui_level_3.this,"尚未填寫完成",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void get_question_data(){
        alQ_timestamp.clear();alQ_title.clear();alQ_content.clear();
        alQ_tag.clear();alQ_time.clear();
        DatabaseReference databaseReference3 = firebaseDatabase.getReference("questions");
        databaseReference3.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    get_question_data();
                }else{
                    for(DataSnapshot ds : task.getResult().getChildren()){
                        alQ_timestamp.add(ds.getKey());
                        for(DataSnapshot ds2 : ds.getChildren()){
                            if(ds2.getKey().equals("time"))
                                alQ_time.add(ds2.getValue().toString());
                            else if(ds2.getKey().equals("title"))
                                alQ_title.add(ds2.getValue().toString());
                            else if(ds2.getKey().equals("content"))
                                alQ_content.add(ds2.getValue().toString());
                            else if(ds2.getKey().equals("tags_num"))
                                alQ_tags_num.add(Integer.parseInt(ds2.getValue().toString()));
                            else if(ds2.getKey().equals("click_time"))
                                alQ_clickTime.add(ds2.getValue().toString());
                            else if(ds2.getKey().equals("tags")){
                                ArrayList<String> ttt = new ArrayList<String>();
                                ttt.clear();
                                for(DataSnapshot ds3 : ds2.getChildren()){
                                    ttt.add(ds3.getValue().toString());
                                }
                                alQ_tag.add(ttt);
                            }

                        }
                    }
                    question_list.setAdapter(new question_adapter());
                }
            }
        });
    }
    private void question_list_listener(){
        question_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DatabaseReference databaseReference = firebaseDatabase.getReference("questions");
                databaseReference.child(alQ_timestamp.get(position)).child("click_time").setValue(Integer.parseInt(alQ_clickTime.get(position))+1);
                alQ_clickTime.set(position,String.valueOf(Integer.parseInt(alQ_clickTime.get(position))+1));

                Intent intent = new Intent(ui_level_3.this,show_question.class);
                System.out.println(alQ_timestamp);
                intent.putExtra("question_id",alQ_timestamp.get(position));
                intent.putExtra("user_nickName",nickName);
                startActivity(intent);
                get_question_data();
                question_list.setAdapter(new question_adapter());
            }
        });
    }

    private class question_adapter extends BaseAdapter{

        @Override
        public int getCount() {
            return alQ_timestamp.size();
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
            View v = getLayoutInflater().inflate(R.layout.question_list,null);
            TextView t_question_time = (TextView) v.findViewById(R.id.t_mytime_question2);
            TextView t_question_clickTime = (TextView) v.findViewById(R.id.t_clickTime_question2);
            TextView t_question_tag = (TextView) v.findViewById(R.id.t_tags_question2);
            TextView t_question_title = (TextView) v.findViewById(R.id.t_myTitle_question2);
            TextView t_question_content = (TextView) v.findViewById(R.id.t_myContent_question2);
            t_question_time.setText(alQ_time.get(position));
            t_question_title.setText(alQ_title.get(position));
            t_question_content.setText(alQ_content.get(position));
            t_question_clickTime.setText(alQ_clickTime.get(position));
            String s = "";
            for(int i=0;i<alQ_tags_num.get(position);i++){
                s = s+"#"+alQ_tag.get(position).get(i);
            }
            t_question_tag.setText(s);
            return v;

        }
    }
    //--------------------------
    Button b_left_book, b_right_book;
    TextView t_page1_book;
    Integer nowPage = 1;
    ImageView book_img1, book_img2, book_img3, book_img4, book_img5, book_img6;
    TextView book_n1, book_n2, book_n3, book_n4, book_n5, book_n6;
    TextView book_name1, book_name2, book_name3, book_name4, book_name5, book_name6;
    TextView t_calendarTest;
    Button b_calendarTest;
    private static int checkCalendarAccount(Context context) {
        Cursor userCursor = context.getContentResolver().query(Uri.parse(CALANDER_URL), null, null, null, null);
        try {
            if (userCursor == null)//查询返回空值
                return -1;
            int count = userCursor.getCount();
            if (count > 0) {//存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }
    private static String CALENDARS_NAME = "test";
    String CALENDARS_ACCOUNT_NAME = "codecetfp@gmail.com";
    String CALENDARS_ACCOUNT_TYPE = "com.google";
    String CALENDARS_DISPLAY_NAME = "测试账户";

    long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);

        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALANDER_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }
    int checkAndAddCalendarAccount(Context context){
        int oldId = checkCalendarAccount(context);
        if( oldId >= 0 ){
            return oldId;
        }else{
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }

    void addCalendarEvent(Context context,String title, String description, long beginTime){
        // 获取日历账户的id
        int calId = checkAndAddCalendarAccount(context);
        if (calId < 0) {
            // 获取账户id失败直接返回，添加日历事件失败
            Toast.makeText(ui_level_3.this,"失敗",Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues event = new ContentValues();
        event.put("title", title);
        event.put("description", description);
        // 插入账户的id
        event.put("calendar_id", calId);

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(beginTime);//设置开始时间
        long start = mCalendar.getTime().getTime();
        mCalendar.setTimeInMillis(start + 1000);//设置终止时间
        long end = mCalendar.getTime().getTime();

        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, end);
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Taipei");  //这个是时区，必须有，
        //添加事件
        Uri newEvent = context.getContentResolver().insert(Uri.parse(CALANDER_EVENT_URL), event);
        if (newEvent == null) {
            // 添加日历事件失败直接返回
            return;
        }
        //事件提醒的设定
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        // 提前10分钟有提醒
        values.put(CalendarContract.Reminders.MINUTES, 10);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = context.getContentResolver().insert(Uri.parse(CALANDER_REMIDER_URL), values);
        if(uri == null) {
            // 添加闹钟提醒失败直接返回
            return;
        }
    }

    int callbackId = 42;
    private void checkPermission(int callbackId, String... permissionsId) {
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED;
        }

        if (!permissions)
            ActivityCompat.requestPermissions(this, permissionsId, callbackId);
    }

    Button b_qrcode;
    IntentIntegrator intentIntegrator;
    private void listener_book(){
        View v = myViews.get(3);
        b_qrcode = (Button) v.findViewById(R.id.b_qrcode);
        t_calendarTest = (TextView) v.findViewById(R.id.calendar_test);
        b_calendarTest = (Button) v.findViewById(R.id.b_calendar_test);
        b_calendarTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View myView = getLayoutInflater().inflate(R.layout.alert_calender_test,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(ui_level_3.this);
                builder.setTitle("填寫班表")
                        .setView(myView)
                        .setNegativeButton("取消",null)
                        .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                t_calendarTest.setVisibility(View.VISIBLE);
                                checkPermission(callbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
                                addCalendarEvent(ui_level_3.this,"myTitle","myDescription",System.currentTimeMillis());
                            }
                        })
                        .show();
            }
        });
        b_calendarTest.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ui_level_3.this);
                builder.setTitle("取消排班")
                        .setMessage("是否取消排班 ?")
                        .setNegativeButton("否",null)
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                t_calendarTest.setVisibility(View.INVISIBLE);
                            }
                        })
                        .show();
                return true;
            }
        });
        l1 = (LinearLayout) v.findViewById(R.id.book_linearlayout1);
        l2 = (LinearLayout) v.findViewById(R.id.book_linearlayout2);
        if(userID.equals("112004179482517820474")){
            l1.setVisibility(View.VISIBLE);
            l2.setVisibility(View.INVISIBLE);
        }else if(userID.equals("116063306527075552878")){
            l1.setVisibility(View.INVISIBLE);
            l2.setVisibility(View.VISIBLE);
        }
        book_name1 = (TextView) v.findViewById(R.id.t_book_name1);
        book_name2 = (TextView) v.findViewById(R.id.t_book_name2);
        book_name3 = (TextView) v.findViewById(R.id.t_book_name3);
        book_name4 = (TextView) v.findViewById(R.id.t_book_name4);
        book_name5 = (TextView) v.findViewById(R.id.t_book_name5);
        book_name6 = (TextView) v.findViewById(R.id.t_book_name6);
        book_n1 = (TextView) v.findViewById(R.id.t_book_no1);
        book_n2 = (TextView) v.findViewById(R.id.t_book_no2);
        book_n3 = (TextView) v.findViewById(R.id.t_book_no3);
        book_n4 = (TextView) v.findViewById(R.id.t_book_no4);
        book_n5 = (TextView) v.findViewById(R.id.t_book_no5);
        book_n6 = (TextView) v.findViewById(R.id.t_book_no6);
        book_img1 = (ImageView) v.findViewById(R.id.book_i1);
        book_img2 = (ImageView) v.findViewById(R.id.book_i2);
        book_img3 = (ImageView) v.findViewById(R.id.book_i3);
        book_img4 = (ImageView) v.findViewById(R.id.book_i4);
        book_img5 = (ImageView) v.findViewById(R.id.book_i5);
        book_img6 = (ImageView) v.findViewById(R.id.book_i6);
        b_left_book = (Button) v.findViewById(R.id.b_book_leftArrow);
        b_right_book = (Button) v.findViewById(R.id.b_book_rightArrow);
        t_page1_book = (TextView) v.findViewById(R.id.t_book_page1);
        b_left_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowPage = Integer.parseInt(t_page1_book.getText().toString());
                if(nowPage > 1){
                    nowPage = nowPage-1;
                    t_page1_book.setText(String.valueOf(nowPage));
                    change_page_image(nowPage);
                    book_which_page(nowPage);
                }
            }
        });
        b_right_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowPage = Integer.parseInt(t_page1_book.getText().toString());
                if(nowPage < 6){
                    nowPage = nowPage+1;
                    change_page_image(nowPage);
                    book_which_page(nowPage);
                    t_page1_book.setText(String.valueOf(nowPage));
                }
            }
        });
        intentIntegrator=new IntentIntegrator(ui_level_3.this);
        b_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.setPrompt("將電子發票左邊QRCode放置掃描區域內");
                intentIntegrator.setCameraId(0);
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.setCaptureActivity(act_a3_scanner2.class);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.initiateScan();
            }
        });
    }

    String qrcode_id = "?";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null){
            qrcode_id = intentResult.getContents();
            if(qrcode_id.equals("2cf1383")){
                Intent intent11 = new Intent(ui_level_3.this,show_dog_file.class);
                intent11.putExtra("tag_num",qrcode_id);
                intent11.putExtra("user_id",userID);
                startActivity(intent11);
            }
        }
    }

    private void change_page_image(Integer p){
        DatabaseReference databaseReference = firebaseDatabase.getReference("accounts");
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    change_page_image(p);
                }else{
                    for(DataSnapshot dss : task.getResult().getChildren()){
                        if(dss.getKey().equals(userID)){
                            for(DataSnapshot ds : dss.getChildren()){
                                if(ds.getKey().equals("books")){
                                    for(DataSnapshot ds2 : ds.getChildren()){
                                        if(p == 1){
                                            if(ds2.getKey().equals("b1"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img1.setImageResource(R.drawable.dd3);
                                            if(ds2.getKey().equals("b2"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img2.setImageResource(R.drawable.dd4);
                                            if(ds2.getKey().equals("b3"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img3.setImageResource(R.drawable.dd5);
                                            if(ds2.getKey().equals("b4"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img4.setImageResource(R.drawable.dd7);
                                            if(ds2.getKey().equals("b5"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img5.setImageResource(R.drawable.dd8);
                                            if(ds2.getKey().equals("b6"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img6.setImageResource(R.drawable.dd10);

                                        }else if(p==2){
                                            if(ds2.getKey().equals("b7"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img1.setImageResource(R.drawable.dd12);
                                            if(ds2.getKey().equals("b8"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img2.setImageResource(R.drawable.dd13);
                                            if(ds2.getKey().equals("b9"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img3.setImageResource(R.drawable.dd14);
                                            if(ds2.getKey().equals("b10"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img4.setImageResource(R.drawable.dd16);
                                            if(ds2.getKey().equals("b11"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img5.setImageResource(R.drawable.dd17);
                                            if(ds2.getKey().equals("b12"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img6.setImageResource(R.drawable.dd18);
                                        }else if(p==3){
                                            if(ds2.getKey().equals("b13"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img1.setImageResource(R.drawable.dd19);
                                            if(ds2.getKey().equals("b14"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img2.setImageResource(R.drawable.dd20);
                                            if(ds2.getKey().equals("b15"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img3.setImageResource(R.drawable.dd22);
                                            if(ds2.getKey().equals("b16"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img4.setImageResource(R.drawable.dd23);
                                            if(ds2.getKey().equals("b17"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img5.setImageResource(R.drawable.dd25);
                                            if(ds2.getKey().equals("b18"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img6.setImageResource(R.drawable.dd26);
                                        }else if(p==4){
                                            if(ds2.getKey().equals("b19"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img1.setImageResource(R.drawable.dd27);
                                            if(ds2.getKey().equals("b20"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img2.setImageResource(R.drawable.dd28);
                                            if(ds2.getKey().equals("b21"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img3.setImageResource(R.drawable.dd29);
                                            if(ds2.getKey().equals("b22"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img4.setImageResource(R.drawable.dd30);
                                            if(ds2.getKey().equals("b23"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img5.setImageResource(R.drawable.dd31);
                                            if(ds2.getKey().equals("b24"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img6.setImageResource(R.drawable.dd32);
                                        }else if(p==5){
                                            if(ds2.getKey().equals("b25"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img1.setImageResource(R.drawable.dd33);
                                            if(ds2.getKey().equals("b26"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img2.setImageResource(R.drawable.dd34);
                                            if(ds2.getKey().equals("b27"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img3.setImageResource(R.drawable.dd37);
                                            if(ds2.getKey().equals("b28"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img4.setImageResource(R.drawable.dd38);
                                            if(ds2.getKey().equals("b29"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img5.setImageResource(R.drawable.dd39);
                                            if(ds2.getKey().equals("b30"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img6.setImageResource(R.drawable.dd40);
                                        }else if(p==6){
                                            if(ds2.getKey().equals("b31"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img1.setImageResource(R.drawable.dd41);
                                            if(ds2.getKey().equals("b32"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img2.setImageResource(R.drawable.dd45);
                                            if(ds2.getKey().equals("b33"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img3.setImageResource(R.drawable.dd46);
                                            if(ds2.getKey().equals("b34"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img4.setImageResource(R.drawable.dd47);
                                            if(ds2.getKey().equals("b35"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img5.setImageResource(R.drawable.dd48);
                                            if(ds2.getKey().equals("b36"))
                                                if(ds2.getValue().toString().equals("1"))
                                                    book_img6.setImageResource(R.drawable.dd49);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void page1(){
        book_img1.setImageResource(R.drawable.d3_);
        book_img2.setImageResource(R.drawable.d4_);
        book_img3.setImageResource(R.drawable.d5_);
        book_img4.setImageResource(R.drawable.d7_);
        book_img5.setImageResource(R.drawable.d8_);
        book_img6.setImageResource(R.drawable.d10_);
        book_n1.setText("NO . 03");
        book_n2.setText("NO . 04");
        book_n3.setText("NO . 05");
        book_n4.setText("NO . 07");
        book_n5.setText("NO . 08");
        book_n6.setText("NO . 10");
        book_name1.setText("威士比");
        book_name2.setText("皮皮");
        book_name3.setText("小黑");
        book_name4.setText("芬達");
        book_name5.setText("黑木耳");
        book_name6.setText("嚕嚕");
    }
    private void page2(){
        book_img1.setImageResource(R.drawable.d12_);
        book_img2.setImageResource(R.drawable.d13_);
        book_img3.setImageResource(R.drawable.d14_);
        book_img4.setImageResource(R.drawable.d16_);
        book_img5.setImageResource(R.drawable.d17_);
        book_img6.setImageResource(R.drawable.d18_);
        book_n1.setText("NO . 12");
        book_n2.setText("NO . 13");
        book_n3.setText("NO . 14");
        book_n4.setText("NO . 16");
        book_n5.setText("NO . 17");
        book_n6.setText("NO . 18");
        book_name1.setText("豆漿");
        book_name2.setText("小明");
        book_name3.setText("嘎逼");
        book_name4.setText("球球");
        book_name5.setText("老黑");
        book_name6.setText("小豬");
    }
    private void page3(){
        book_img1.setImageResource(R.drawable.d19_);
        book_img2.setImageResource(R.drawable.d20_);
        book_img3.setImageResource(R.drawable.d22_);
        book_img4.setImageResource(R.drawable.d23_);
        book_img5.setImageResource(R.drawable.d25_);
        book_img6.setImageResource(R.drawable.d26_);
        book_n1.setText("NO . 19");
        book_n2.setText("NO . 20");
        book_n3.setText("NO . 22");
        book_n4.setText("NO . 23");
        book_n5.setText("NO . 25");
        book_n6.setText("NO . 26");
        book_name1.setText("小愛");
        book_name2.setText("小文");
        book_name3.setText("小白");
        book_name4.setText("荳荳");
        book_name5.setText("奶基");
        book_name6.setText("大白目");
    }
    private void page4(){
        book_img1.setImageResource(R.drawable.d27_);
        book_img2.setImageResource(R.drawable.d28_);
        book_img3.setImageResource(R.drawable.d29_);
        book_img4.setImageResource(R.drawable.d30_);
        book_img5.setImageResource(R.drawable.d31_);
        book_img6.setImageResource(R.drawable.d32_);
        book_n1.setText("NO . 27");
        book_n2.setText("NO . 28");
        book_n3.setText("NO . 29");
        book_n4.setText("NO . 30");
        book_n5.setText("NO . 31");
        book_n6.setText("NO . 32");
        book_name1.setText("虎斑");
        book_name2.setText("司康");
        book_name3.setText("年年");
        book_name4.setText("奶茶");
        book_name5.setText("單眼皮");
        book_name6.setText("小卷");
    }
    private void page5(){
        book_img1.setImageResource(R.drawable.d33_);
        book_img2.setImageResource(R.drawable.d34_);
        book_img3.setImageResource(R.drawable.d37_);
        book_img4.setImageResource(R.drawable.d38_);
        book_img5.setImageResource(R.drawable.d39_);
        book_img6.setImageResource(R.drawable.d40_);
        book_n1.setText("NO . 33");
        book_n2.setText("NO . 34");
        book_n3.setText("NO . 37");
        book_n4.setText("NO . 38");
        book_n5.setText("NO . 39");
        book_n6.setText("NO . 40");
        book_name1.setText("黃卷");
        book_name2.setText("無卷");
        book_name3.setText("阿捲");
        book_name4.setText("熊熊");
        book_name5.setText("咪哥");
        book_name6.setText("白白");
    }
    private void page6(){
        book_img1.setImageResource(R.drawable.d41_);
        book_img2.setImageResource(R.drawable.d45_);
        book_img3.setImageResource(R.drawable.d46_);
        book_img4.setImageResource(R.drawable.d47_);
        book_img5.setImageResource(R.drawable.d48_);
        book_img6.setImageResource(R.drawable.d49_);
        book_n1.setText("NO . 41");
        book_n2.setText("NO . 45");
        book_n3.setText("NO . 46");
        book_n4.setText("NO . 47");
        book_n5.setText("NO . 48");
        book_n6.setText("NO . 49");
        book_name1.setText("葉子");
        book_name2.setText("雞蛋羹");
        book_name3.setText("oreo");
        book_name4.setText("妙妙");
        book_name5.setText("Jordan");
        book_name6.setText("阿士");
    }
    private void book_which_page(Integer page){
        if(page == 1)
            page1();
        else if(page ==2)
            page2();
        else if(page ==3)
            page3();
        else if(page == 4)
            page4();
        else if(page == 5)
            page5();
        else if(page == 6)
            page6();
    }
    //--------------------------
    Integer from_message_state;
    RelativeLayout r1, r2, r3;
    private void get_my_from_message(){
        DatabaseReference databaseReference = firebaseDatabase.getReference("accounts");
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    get_my_from_message();
                }else{
                    for(DataSnapshot ds : task.getResult().getChildren()){
                        if(ds.getKey().equals("112004179482517820474"))
                            for(DataSnapshot ds2 : ds.getChildren())
                                if(ds2.getKey().equals("show_from_message"))
                                    from_message_state = Integer.parseInt(ds2.getValue().toString());
                    }
                    if(userID.equals("112004179482517820474")){
                        r1.setVisibility(View.VISIBLE);
                        r2.setVisibility(View.INVISIBLE);
                        if(from_message_state == 1){
                            System.out.println("8888888888888888888888888888888888888888888888");
                            r3.setVisibility(View.VISIBLE);
                        }
                        else{
                            r3.setVisibility(View.INVISIBLE);
                        }

                    }else{
                        r2.setVisibility(View.VISIBLE);
                        r1.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    ListView member_list;

    ArrayList<String> A_name,A_level,A_email;
    Spinner spinner;
    private void listener_message(){
        View v = myViews.get(4);

        r1 = (RelativeLayout) v.findViewById(R.id.message_relativelayout1);
        r2 = (RelativeLayout) v.findViewById(R.id.message_relativelayout2);
        r3 = (RelativeLayout) v.findViewById(R.id.message_relativelayout11);
        A_name = new ArrayList<String>();
        A_level = new ArrayList<String>();
        A_email = new ArrayList<String>();
        A_name.add("kudo");
        A_name.add("darkrai");
        A_name.add("軟工系盧姵君");
        A_level.add("3");
        A_level.add("1");
        A_level.add("3");
        A_email.add("sherloxk5254@gmail.com");
        A_email.add("codecetfp@gmail.com");
        A_email.add("410977007@mail.nknu.edu.tw");
        member_list = (ListView) v.findViewById(R.id.message_list);
        get_my_from_message();
        member_list.setAdapter(new myMemberAdapter());
        member_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View v2 = getLayoutInflater().inflate(R.layout.alert_change_member_level,null);
                spinner = (Spinner) v2.findViewById(R.id.member_spinner);
                spinner.setSelection(Integer.parseInt(A_level.get(position))-1);
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println(Integer.parseInt(A_level.get(position))-1);
                AlertDialog.Builder builder = new AlertDialog.Builder(ui_level_3.this);
                builder.setTitle("更改權限")
                        .setView(getLayoutInflater().inflate(R.layout.alert_change_member_level, null))
                        .setNegativeButton("取消",null)
                        .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                A_level.set(position,"2");
                                System.out.println(spinner.getSelectedItem().toString());
                                System.out.println(A_level);
                                member_list.setAdapter(new myMemberAdapter());
                            }
                        })
                        .show();
            }
        });

        member_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ui_level_3.this);
                builder.setTitle("刪除成員")
                        .setMessage("是否刪除此成員?")
                        .setNegativeButton("否", null)
                        .setPositiveButton("是",null)
                        .show();
                return true;
            }
        });
    }

    private class myMemberAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 3;
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
            View v = getLayoutInflater().inflate(R.layout.member_list_layout,null);
            TextView t_name = (TextView) v.findViewById(R.id.t_memberName);
            TextView t_email = (TextView) v.findViewById(R.id.t_memberEmail);
            TextView t_level = (TextView) v.findViewById(R.id.t_memberLevel);
            t_name.setText(A_name.get(position));
            t_email.setText(A_email.get(position));
            t_level.setText("level : "+A_level.get(position));

            return v;
        }
    }

    //---------------------
    private class myForumAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return al_timestamp.size();
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
            View myView = getLayoutInflater().inflate(R.layout.forum_listview_article,null);
            TextView myTime, myTags, myTitle, myContent, myClickTimes, myViolation;
            ImageView myFlag;
            myFlag = (ImageView) myView.findViewById(R.id.myobject);
            myTime = (TextView) myView.findViewById(R.id.t_mytime);
            myTags = (TextView) myView.findViewById(R.id.t_tags);
            myTitle = (TextView) myView.findViewById(R.id.t_myTitle);
            myContent = (TextView) myView.findViewById(R.id.t_myContent);
            myClickTimes = (TextView) myView.findViewById(R.id.t_clickTime);
            myViolation = (TextView) myView.findViewById(R.id.t_object);
            myTime.setText(al_year.get(position)+"-"+al_month.get(position)+ "-"+al_day.get(position)+ "-"
            +"  "+al_hour.get(position)+":"+al_minute.get(position));


            myTags.setText(al_tag.get(position));
            myTitle.setText(al_title.get(position));
            myContent.setText(al_content.get(position));
            myClickTimes.setText(String.valueOf(al_clickTimes.get(position)));
            myViolation.setText(al_violation.get(position));

            if(account_level.equals("2") || account_level.equals("1")){
                myFlag.setVisibility(View.VISIBLE);
                myViolation.setVisibility(View.VISIBLE);
            }else{
                myFlag.setVisibility(View.INVISIBLE);
                myViolation.setVisibility(View.INVISIBLE);
            }

            return myView;
        }
    }

    private void article_delete(Integer position){
        DatabaseReference databaseReference = firebaseDatabase.getReference("articles");
        databaseReference.child(al_timestamp.get(position)).removeValue();
        Toast.makeText(ui_level_3.this,"刪除成功",Toast.LENGTH_SHORT).show();
    }

    private void article_violation(Integer position){
        DatabaseReference databaseReference = firebaseDatabase.getReference("articles");
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    article_violation(position);
                }else{
                    Integer violationNum = 0;
                    ArrayList al_violationID = new ArrayList();
                    al_violationID.clear();
                    for(DataSnapshot ds : task.getResult().getChildren()){
                        if(ds.getKey().equals(al_timestamp.get(position))){
                            for(DataSnapshot ds2 : ds.getChildren()){
                                if(ds2.getKey().equals("violation")){
                                    violationNum = Integer.parseInt(ds2.getValue().toString());
                                }
                            }
                        }
                    }
                    if(violationNum == 0){
                        databaseReference.child(al_timestamp.get(position)).child("violation").setValue(violationNum+1);
                        databaseReference.child(al_timestamp.get(position)).child("violation_id").child("v1").setValue(userID);
                        Toast.makeText(ui_level_3.this,"檢舉成功",Toast.LENGTH_SHORT).show();
                    }else{
                        for(int i=1;i<=violationNum;i++){
                            for(DataSnapshot ds : task.getResult().getChildren()){
                                if(ds.getKey().equals(al_timestamp.get(position))){
                                    for(DataSnapshot ds2 : ds.getChildren()){
                                        if(ds2.getKey().equals("violation_id")){
                                            for(DataSnapshot ds3 : ds2.getChildren()){
                                                if(ds3.getKey().equals("v"+String.valueOf(i))){
                                                    al_violationID.add(ds3.getValue().toString());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if(al_violationID.contains(userID)){
                            Toast.makeText(ui_level_3.this,"您已檢舉過此文章",Toast.LENGTH_SHORT).show();
                        }else{
                            databaseReference.child(al_timestamp.get(position)).child("violation").setValue(violationNum+1);
                            databaseReference.child(al_timestamp.get(position)).child("violation_id").child("v"+String.valueOf(violationNum+1)).setValue(userID);
                            Toast.makeText(ui_level_3.this,"檢舉成功",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

}