package project.fightgopher.holder;


import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

import java.util.ArrayList;

import project.fightgopher.R;

public class LeaderBoardChildHolder extends ChildViewHolder {

    private TextView txtPerLVScore;
    private TextView txtDate;
    private ImageView imgCup;

    int[] ImgCups = {
            R.drawable.gold_cup,
            R.drawable.silver_cup,
            R.drawable.bronze_cup
    };

    public LeaderBoardChildHolder(View itemView) {
        super(itemView);
        txtPerLVScore = (TextView) itemView.findViewById(R.id.txtChildName);
        txtDate = (TextView) itemView.findViewById(R.id.txtChildDate);
        imgCup = (ImageView) itemView.findViewById(R.id.imgChildCup);

    }

    public void setChildHolderData(Context context, ArrayList<Object> arrayList) {

        if (Integer.parseInt((String) arrayList.get(3)) != 0) {

            int[] temp = (int[]) arrayList.get(4);


            txtPerLVScore.setText(
                    String.format(context.getString(R.string.LV1score), temp[0])
                            + "\n"
                            + String.format(context.getString(R.string.LV2score), temp[1])
                            + "\n"
                            + String.format(context.getString(R.string.LV3score), temp[2])
                            + "\n"
                            + String.format(context.getString(R.string.totalscoreis), Integer.parseInt((String) arrayList.get(3))));

                    txtPerLVScore.setTextSize(20);

            txtDate.setText((String) arrayList.get(2));


            if (Integer.parseInt((String) arrayList.get(3)) > 0) {
                switch ((String) arrayList.get(0)) {
                    case "First":
                        imgCup.setVisibility(View.VISIBLE);
                        imgCup.setImageResource(ImgCups[0]);
                        break;
                    case "Second":
                        imgCup.setVisibility(View.VISIBLE);
                        imgCup.setImageResource(ImgCups[1]);
                        break;
                    case "Third":
                        imgCup.setVisibility(View.VISIBLE);
                        imgCup.setImageResource(ImgCups[2]);
                        break;
                    default:
                        imgCup.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

}
