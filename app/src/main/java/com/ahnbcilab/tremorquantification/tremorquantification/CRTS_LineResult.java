package com.ahnbcilab.tremorquantification.tremorquantification;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ahnbcilab.tremorquantification.data.Line;
import com.ahnbcilab.tremorquantification.data.LineData;
import com.ahnbcilab.tremorquantification.data.Spiral;
import com.ahnbcilab.tremorquantification.data.SpiralData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static com.ahnbcilab.tremorquantification.functions.main1.line_count;

public class CRTS_LineResult extends AppCompatActivity {

    RadioButton crts14_1_0, crts14_1_1, crts14_1_2, crts14_1_3, crts14_1_4;
    private int crts11, crts12, crts13, crts14;
    private boolean bool = true;
    private int check;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    public RequestManager mGlideRequestManager;
    static int count = 0;
    String downurl = null;
    private DatabaseReference databasepatient;
    private DatabaseReference databaseclinicID;

    private int leftLineCount = 0;
    private int rightLineCount = 0;
    private int finalleftLineCount;
    private int finalrightLineCount;
    private int total_line_count;
    ImageView present_line;

    RadioGroup r_group_crts;
    Button next;
    RadioButton r_crt_arr[] = new RadioButton[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crts__line_result);

        Intent intent = getIntent();

        final double[] spiral_result = intent.getDoubleArrayExtra("spiral_result");
        final double[] left_spiral_result = intent.getDoubleArrayExtra("left_spiral_result");
        final double[] line_result = intent.getDoubleArrayExtra("line_result");
        final String line_downurl = intent.getStringExtra("line_downurl");
        final String crts_right_spiral_downurl = intent.getStringExtra("crts_right_spiral_downurl");
        final String crts_left_spiral_downurl = intent.getStringExtra("crts_left_spiral_downurl");
        final String writing_downurl = intent.getStringExtra("writing_downurl");
        final String path = intent.getStringExtra("path1");
        final String edit = intent.getStringExtra("edit");
        final String PatientName = intent.getStringExtra("PatientName");
        final String Clinic_ID = intent.getStringExtra("Clinic_ID");
        final String crts_num = intent.getStringExtra("crts_num");
        final int left = intent.getIntExtra("left", -1);

        TextView hz_score = (TextView) findViewById(R.id.hz_score);
        TextView magnitude_score = (TextView) findViewById(R.id.magnitude_score);
        TextView time_score = (TextView) findViewById(R.id.time_score);
        TextView speed_score = (TextView) findViewById(R.id.speed_score);
        TextView distance_score = (TextView) findViewById(R.id.distance_score);

        hz_score.setText(String.format("%.1f", line_result[1]));
        magnitude_score.setText(String.format("%.1f", line_result[0]));
        distance_score.setText(String.format("%.1f", line_result[3]));
        time_score.setText(String.format("%.1f", line_result[2]));
        speed_score.setText(String.format("%.1f", line_result[4]));

        //의사 ID 얻어오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        String task;

        if (left == 0) {
            task = "Right";

        } else {
            task = "Left";
        }
        //image part
        present_line = findViewById(R.id.present_line);
        mGlideRequestManager = Glide.with(CRTS_LineResult.this);
        present_line.post(new Runnable() {
            @Override
            public void run() {
                mGlideRequestManager
                        .asBitmap()
                        .load(line_downurl)
                        .placeholder(R.drawable.image_loading)
                        .apply(new RequestOptions().centerCrop())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                Matrix matrix = new Matrix();
                                matrix.postRotate(90);
                                int width = resource.getWidth();
                                int height = resource.getHeight();

                                resource = Bitmap.createBitmap(resource, 0, 0, width, height, matrix, true);
                                present_line.setImageBitmap(resource);
                            }
                        });
            }
        });

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        final String timestamp = sdf.format(d);
        String[] today = timestamp.split(" ");

        crts11 = intent.getIntExtra("crts11", -1);
        crts12 = intent.getIntExtra("crts12", -1);
        crts13 = intent.getIntExtra("crts13", -1);

        if (edit.equals("yes")) {
            crts11 = intent.getIntExtra("crts11", -1);
            crts12 = intent.getIntExtra("crts12", -1);
            crts13 = intent.getIntExtra("crts13", -1);
            crts14 = intent.getIntExtra("crts14", -1);

        }
        check = intent.getIntExtra("crts14", -1);

        crts14_1_0 = (RadioButton) findViewById(R.id.crts14_1_0);
        crts14_1_1 = (RadioButton) findViewById(R.id.crts14_1_1);
        crts14_1_2 = (RadioButton) findViewById(R.id.crts14_1_2);
        crts14_1_3 = (RadioButton) findViewById(R.id.crts14_1_3);
        crts14_1_4 = (RadioButton) findViewById(R.id.crts14_1_4);


        final Button quit = (Button) findViewById(R.id.line_result_quit_button);
        next = (Button) findViewById(R.id.line_next);
        if (!edit.equals("yes")) {
            next.setVisibility(View.GONE);
        }
        r_group_crts = (RadioGroup) findViewById(R.id.crg11_1);
        r_crt_arr[0] = crts14_1_0;
        r_crt_arr[1] = crts14_1_1;
        r_crt_arr[2] = crts14_1_2;
        r_crt_arr[3] = crts14_1_3;
        r_crt_arr[4] = crts14_1_4;

        for(int i = 0; i<r_crt_arr.length; i++)
        {
            final int index = i;
            r_crt_arr[index].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    next.setVisibility(View.VISIBLE);
                }
            });

        }
        Button pre_button = (Button) findViewById(R.id.writing_pre);
        if (path.equals("CRTS_detail")) {
            if (!edit.equals("yes")) {
                next.setVisibility(View.GONE);
            }
            quit.setText("돌아가기");
            if (check == 0) {
                crts14_1_0.setChecked(true);
            } else if (check == 1) {
                crts14_1_1.setChecked(true);
            } else if (check == 2) {
                crts14_1_2.setChecked(true);
            } else if (check == 3) {
                crts14_1_3.setChecked(true);
            } else if (check == 4) {
                crts14_1_4.setChecked(true);
            }
        }
        if (edit.equals("yes")) {
            if (crts14 == 0) {
                crts14_1_0.setChecked(true);
            } else if (crts14 == 1) {
                crts14_1_1.setChecked(true);
            } else if (crts14 == 2) {
                crts14_1_2.setChecked(true);
            } else if (crts14 == 3) {
                crts14_1_3.setChecked(true);
            } else if (crts14 == 4) {
                crts14_1_4.setChecked(true);
            }
            pre_button.setVisibility(View.GONE);
        }
        databasepatient = firebaseDatabase.getReference("PatientList");
        databaseclinicID = databasepatient.child(Clinic_ID).child("Line List");
        if (left == 0) {
            databasepatient.orderByChild("ClinicID").equalTo(Clinic_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot mData : dataSnapshot.getChildren()) {
                        total_line_count = (int) mData.child("Line List").child("Right").getChildrenCount();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            databasepatient.orderByChild("ClinicID").equalTo(Clinic_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot mData : dataSnapshot.getChildren()) {
                        total_line_count = (int) mData.child("Line List").child("Left").getChildrenCount();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        Query left_query = databaseclinicID.child("Left").orderByChild("path").equalTo("CRTS");
        Query right_query = databaseclinicID.child("Right").orderByChild("path").equalTo("CRTS");
        left_query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    String key = fileSnapshot.getKey();
                    leftLineCount++;
                    Log.v("Line", "LineResult : " + key + leftLineCount);
                }
                finalleftLineCount = leftLineCount;
                Log.v("Line", "LineResult : " + leftLineCount + finalleftLineCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        right_query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    String key = fileSnapshot.getKey();
                    rightLineCount++;
                    //Log.v("Spiral", "SpiralResult : " + key + rightSpiralCount) ;
                }
                finalrightLineCount = rightLineCount;
                //Log.v("Spiral", "SpiralResult : " + rightSpiralCount + " " + finalrightSpiralCount) ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit.equals("yes")) {

                } else {
                    if (left == 0) {
                        com.ahnbcilab.tremorquantification.data.Line line = new Line(timestamp, finalrightLineCount + finalleftLineCount);
                        LineData linedata = new LineData(1.0, line_result[1], line_result[0], line_result[3], line_result[2], line_result[4]);
                        final String key = String.valueOf(databaseclinicID.child("Right").push().getKey());
                        databaseclinicID.child("Right").child(key).setValue(line);
                        databaseclinicID.child("Right").child(key).child("Right_LINE_count").setValue(finalrightLineCount);
                        databaseclinicID.child("Right").child(key).child("path").setValue(path);
                        databaseclinicID.child("Right").child(key).child("Line_Result").setValue(linedata);
                        databaseclinicID.child("Right").child(key).child("Right_total_count").setValue(total_line_count);
                        databaseclinicID.child("Right").child(key).child("URL").setValue(line_downurl);

                        databasepatient.orderByChild("ClinicID").equalTo(Clinic_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                    Long crts_count = childDataSnapshot.child("CRTS List").getChildrenCount();
                                    Long updrs_count = childDataSnapshot.child("UPDRS List").getChildrenCount();
                                    Long right_spiral_count = childDataSnapshot.child("Spiral List").child("Right").getChildrenCount();
                                    Long left_spiral_count = childDataSnapshot.child("Spiral List").child("Left").getChildrenCount();
                                    Long right_line_count = childDataSnapshot.child("Line List").child("Right").getChildrenCount();
                                    Long left_line_count = childDataSnapshot.child("Line List").child("Left").getChildrenCount();
                                    int taskNo = (int) (crts_count + updrs_count + left_line_count + right_line_count + right_spiral_count + left_spiral_count);
                                    databasepatient.child(Clinic_ID).child("TaskNo").setValue(taskNo);

                                    if (taskNo == 1) {
                                        String FirstDate = String.valueOf(childDataSnapshot.child("Line List").child("Right").child(key).child("timestamp").getValue());
                                        int idx = FirstDate.indexOf(" ");
                                        String firstDate1 = FirstDate.substring(0, idx);
                                        databasepatient.child(Clinic_ID).child("FirstDate").setValue(firstDate1);
                                        databasepatient.child(Clinic_ID).child("FinalDate").setValue(firstDate1);
                                    } else {
                                        String FinalDate = String.valueOf(childDataSnapshot.child("Line List").child("Right").child(key).child("timestamp").getValue());
                                        int idx = FinalDate.indexOf(" ");
                                        String finalDate1 = FinalDate.substring(0, idx);
                                        databasepatient.child(Clinic_ID).child("FinalDate").setValue(finalDate1);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        com.ahnbcilab.tremorquantification.data.Line line = new Line(timestamp, finalrightLineCount + finalleftLineCount);
                        LineData linedata = new LineData(1.0, line_result[1], line_result[0], line_result[3], line_result[2], line_result[4]);
                        final String key = String.valueOf(databaseclinicID.child("Left").push().getKey());
                        databaseclinicID.child("Left").child(key).setValue(line);
                        databaseclinicID.child("Left").child(key).child("Left_LINE_count").setValue(finalleftLineCount);
                        databaseclinicID.child("Left").child(key).child("path").setValue(path);
                        databaseclinicID.child("Left").child(key).child("Line_Result").setValue(linedata);
                        databaseclinicID.child("Left").child(key).child("Left_total_count").setValue(total_line_count);
                        databaseclinicID.child("Left").child(key).child("URL").setValue(line_downurl);

                        databasepatient.orderByChild("ClinicID").equalTo(Clinic_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                    Long crts_count = childDataSnapshot.child("CRTS List").getChildrenCount();
                                    Long updrs_count = childDataSnapshot.child("UPDRS List").getChildrenCount();
                                    Long right_spiral_count = childDataSnapshot.child("Spiral List").child("Right").getChildrenCount();
                                    Long left_spiral_count = childDataSnapshot.child("Spiral List").child("Left").getChildrenCount();
                                    Long right_line_count = childDataSnapshot.child("Line List").child("Right").getChildrenCount();
                                    Long left_line_count = childDataSnapshot.child("Line List").child("Left").getChildrenCount();
                                    int taskNo = (int) (crts_count + updrs_count + left_line_count + right_line_count + right_spiral_count + left_spiral_count);
                                    databasepatient.child(Clinic_ID).child("TaskNo").setValue(taskNo);

                                    if (taskNo == 1) {
                                        String FirstDate = String.valueOf(childDataSnapshot.child("Line List").child("Left").child(key).child("timestamp").getValue());
                                        int idx = FirstDate.indexOf(" ");
                                        String firstDate1 = FirstDate.substring(0, idx);
                                        databasepatient.child(Clinic_ID).child("FirstDate").setValue(firstDate1);
                                        databasepatient.child(Clinic_ID).child("FinalDate").setValue(firstDate1);
                                    } else {
                                        String FinalDate = String.valueOf(childDataSnapshot.child("Line List").child("Left").child(key).child("timestamp").getValue());
                                        int idx = FinalDate.indexOf(" ");
                                        String finalDate1 = FinalDate.substring(0, idx);
                                        databasepatient.child(Clinic_ID).child("FinalDate").setValue(finalDate1);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }


                if (crts14_1_0.isChecked()) {
                    crts14 = 0;
                } else if (crts14_1_1.isChecked()) {
                    crts14 = 1;
                } else if (crts14_1_2.isChecked()) {
                    crts14 = 2;
                } else if (crts14_1_3.isChecked()) {
                    crts14 = 3;
                } else if (crts14_1_4.isChecked()) {
                    crts14 = 4;
                } else {
                    bool = false;
                }

                if (bool) {

                } else {

                }
                if (bool) {
                    if (edit.equals("yes")) {
                        Intent intent = new Intent(getApplicationContext(), CRTSActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("path", path);
                        intent.putExtra("path1", path);
                        intent.putExtra("PatientName", PatientName);
                        intent.putExtra("Clinic_ID", Clinic_ID);
                        intent.putExtra("crts_num", crts_num);
                        intent.putExtra("crts11", crts11);
                        intent.putExtra("crts12", crts12);
                        intent.putExtra("crts13", crts13);
                        intent.putExtra("crts14", crts14);
                        intent.putExtra("line_result", line_result);
                        intent.putExtra("left_spiral_result", left_spiral_result);
                        intent.putExtra("line_downurl", line_downurl);
                        intent.putExtra("crts_right_spiral_downurl", crts_right_spiral_downurl);
                        intent.putExtra("crts_left_spiral_downurl", crts_left_spiral_downurl);
                        intent.putExtra("writing_downurl", writing_downurl);
                        intent.putExtra("spiral_result", spiral_result);
                        intent.putExtra("left", left); //left : 0, right : 1
                        startActivity(intent);
                        finish();
                    } else if (edit.equals("no")) {
                        Intent intent = new Intent(getApplicationContext(), CRTSActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("spiral_result", spiral_result);
                        intent.putExtra("left_spiral_result", left_spiral_result);
                        intent.putExtra("line_result", line_result);
                        intent.putExtra("path", "CRTS");
                        intent.putExtra("path1", "CRTS");
                        intent.putExtra("PatientName", PatientName);
                        intent.putExtra("line_downurl", line_downurl);
                        intent.putExtra("Clinic_ID", Clinic_ID);
                        intent.putExtra("crts_num", crts_num);
                        intent.putExtra("crts11", crts11);
                        intent.putExtra("crts12", crts12);
                        intent.putExtra("crts13", crts13);
                        intent.putExtra("crts14", crts14);
                        intent.putExtra("left", left); //left : 0, right : 1
                        intent.putExtra("line_downurl", line_downurl);
                        intent.putExtra("crts_right_spiral_downurl", crts_right_spiral_downurl);
                        intent.putExtra("crts_left_spiral_downurl", crts_left_spiral_downurl);
                        intent.putExtra("writing_downurl", writing_downurl);
                        startActivity(intent);
                        finish();
                    }

                }
            }
        });
        quit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                alertDisplay(Clinic_ID,PatientName);
            }
        });
        pre_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });


    }
    public void alertDisplay(final String Clinic_ID, final String PatientName) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle("종료")
                .setMessage("지금 종료하면 데이터를 모두 잃게됩니다. 종료하시겠습니까?")
                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(), PersonalPatient.class);
                        intent.putExtra("ClinicID", Clinic_ID);
                        intent.putExtra("PatientName", PatientName);
                        intent.putExtra("task", "CRTS");
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "완료 버튼을 선택해주세요", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected  void onDestroy() {
        Drawable d = present_line.getDrawable();
        if (d instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            bitmap.recycle();
            bitmap = null;
        }
        d.setCallback(null);

        super.onDestroy();
    }
}