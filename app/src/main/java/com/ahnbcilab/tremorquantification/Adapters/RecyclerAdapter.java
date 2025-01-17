package com.ahnbcilab.tremorquantification.Adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahnbcilab.tremorquantification.data.CRTS_Result_Data;
import com.ahnbcilab.tremorquantification.tremorquantification.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<CRTS_Result_Data> listData = new ArrayList<>();
    private Context context;
    // Item의 클릭 상태를 저장할 array 객체
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    // 직전에 클릭됐던 Item의 position
    private int prePosition = -1;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.crts_result_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position), position);
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    public void addItem(CRTS_Result_Data data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textView1;
        private TextView totalScore;
        private ConstraintLayout imageView2;
        private CRTS_Result_Data data;
        private int position;
        private ImageView image;
        private TextView hz_score;
        private TextView magnitude_score;
        private TextView distance_score;
        private TextView time_score;
        private TextView speed_score;

        ItemViewHolder(View itemView) {
            super(itemView);

            textView1 = itemView.findViewById(R.id.textView1);
            totalScore = itemView.findViewById(R.id.totalScore);
            imageView2 = itemView.findViewById(R.id.imageView2);
            image = itemView.findViewById(R.id.image);
            hz_score = itemView.findViewById(R.id.hz_score);
            magnitude_score = itemView.findViewById(R.id.magnitude_score);
            distance_score = itemView.findViewById(R.id.distance_score);
            time_score = itemView.findViewById(R.id.time_score);
            speed_score = itemView.findViewById(R.id.speed_score);
        }

        void onBind(CRTS_Result_Data data, int position) {
            this.data = data;
            this.position = position;

            textView1.setText(data.getTitle());
            totalScore.setText(data.getTotalScore());
            image.setBackgroundResource(data.getImage());
            hz_score.setText((int) data.getHz_score() + "");
            magnitude_score.setText((int) data.getMagnitude_score() + "");
            distance_score.setText((int) data.getDistance_score() + "");
            time_score.setText((int) data.getTime_score() + "");
            speed_score.setText((int) data.getSpeed_score() + "");

            //imageView2.setImageResource(data.getResId());

            changeVisibility(selectedItems.get(position));

            itemView.setOnClickListener(this);
            textView1.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.linearItem:
                    if (selectedItems.get(position)) {
                        // 펼쳐진 Item을 클릭 시
                        selectedItems.delete(position);
                    } else {
                        // 직전의 클릭됐던 Item의 클릭상태를 지움
                        selectedItems.delete(prePosition);
                        // 클릭한 Item의 position을 저장
                        selectedItems.put(position, true);
                    }
                    // 해당 포지션의 변화를 알림
                    if (prePosition != -1) notifyItemChanged(prePosition);
                    notifyItemChanged(position);
                    // 클릭된 position 저장
                    prePosition = position;
                    break;
                case R.id.textView1:
                    Toast.makeText(context, data.getTitle(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        /**
         * 클릭된 Item의 상태 변경
         *
         * @param isExpanded Item을 펼칠 것인지 여부
         */
        private void changeVisibility(final boolean isExpanded) {
            // height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
            int dpValue = 450;
            float d = context.getResources().getDisplayMetrics().density;
            int height = (int) (dpValue * d);

            // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);
            // Animation이 실행되는 시간, n/1000초
            va.setDuration(800);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // value는 height 값
                    int value = (int) animation.getAnimatedValue();
                    // imageView의 높이 변경
                    imageView2.getLayoutParams().height = value;
                    imageView2.requestLayout();
                    // imageView가 실제로 사라지게하는 부분
                    imageView2.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                }
            });
            // Animation start
            va.start();
        }
    }
}
