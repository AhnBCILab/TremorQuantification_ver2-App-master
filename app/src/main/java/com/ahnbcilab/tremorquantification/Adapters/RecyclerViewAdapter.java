package com.ahnbcilab.tremorquantification.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.MediaRouteButton;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.TwoStatePreference;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.ahnbcilab.tremorquantification.data.TaskItem;
import com.ahnbcilab.tremorquantification.tremorquantification.LoginActivity;
import com.ahnbcilab.tremorquantification.tremorquantification.PatientItem;
import com.ahnbcilab.tremorquantification.tremorquantification.PatientListActivity;
import com.ahnbcilab.tremorquantification.tremorquantification.PersonalPatient;
import com.ahnbcilab.tremorquantification.tremorquantification.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private Activity activity;
    public ArrayList<PatientItem> patientList = new ArrayList<>();
    public ArrayList<PatientItem> selected_patientList = new ArrayList<>();
    Context mContext;
    boolean checkboxIsVisible = false ;

    public RecyclerViewAdapter(Context context, ArrayList<PatientItem> patientList, ArrayList<PatientItem> selected_patientList) {
        this.mContext = context;
        this.patientList = patientList;
        this.selected_patientList = selected_patientList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView patientType;
        TextView clinicID;
        TextView patientName;
        TextView dateFirst;
        TextView dateFinal;
        CheckBox checkBox ;
        ConstraintLayout cl_listitem;

        public MyViewHolder(View itemView) {
            super(itemView);
            patientType = (TextView) itemView.findViewById(R.id.typeViewItem);
            clinicID = (TextView) itemView.findViewById(R.id.clinicIDItem);
            patientName = (TextView) itemView.findViewById(R.id.patientNameItem);
            dateFirst = (TextView) itemView.findViewById(R.id.dateFirstItem);
            dateFinal = (TextView) itemView.findViewById(R.id.dateFinalItem);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox) ;
            cl_listitem = (ConstraintLayout) itemView.findViewById(R.id.cl_listitem);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_list_item, parent, false);

        final RecyclerViewAdapter.MyViewHolder vHolder = new RecyclerViewAdapter.MyViewHolder(itemView) ;
        return vHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PatientItem data = patientList.get(position);
        holder.patientType.setText(data.getPatientType());
        holder.clinicID.setText(data.getClinicID());
        holder.patientName.setText(data.getPatientName());
        holder.dateFirst.setText(data.getDateFirst());
        holder.dateFinal.setText(data.getDateFinal());
        holder.checkBox.setChecked(data.isDeleteBox());
        holder.checkBox.setVisibility(checkboxIsVisible?View.VISIBLE:View.GONE);

        if(data.getPatientType() == "P"){
            holder.patientType.setText("");
            holder.patientType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.parkinson));

        }
        else if(data.getPatientType() == "ET"){
            holder.patientType.setText("");
            holder.patientType.setBackground(ContextCompat.getDrawable(mContext, R.drawable.essential_tremor));
        }
        else{
            holder.patientType.setText("ㅡ");
        }
    }


    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public void clear() {
        int size = patientList.size() ;
        patientList.clear() ;
        notifyItemRangeRemoved(0, size);
    }


    //어댑터 정비
    public void refreshAdapter() {
        this.selected_patientList = selected_patientList;
        this.patientList = patientList;
        this.notifyDataSetChanged();
    }
    public void visible(){
        checkboxIsVisible = true ;
    }

    public void novisible(){
        checkboxIsVisible = false ;
    }

}