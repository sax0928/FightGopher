package project.fightgopher.holder;


import android.os.Build;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import project.fightgopher.R;

public class LeaderBoardParentHolder extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    //箭頭圖示
    private final ImageView mImageview;

    //顯示排名
    private TextView mRecipeTextView;

    //顯示排名者名稱
    private TextView showUserName;

    public LeaderBoardParentHolder(View itemView) {
        super(itemView);
        mImageview = (ImageView) itemView.findViewById(R.id.imgParentArrow);
        mRecipeTextView = (TextView) itemView.findViewById(R.id.txtParentName);
        showUserName = (TextView) itemView.findViewById(R.id.txtName);

    }

    public void setTittleAndScore(String tittle, String username, String score) {

        mRecipeTextView.setText(tittle);
        showUserName.setText(score.equals("0") ? "" : username);
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (expanded) {
                mImageview.setRotation(ROTATED_POSITION);
            } else {
                mImageview.setRotation(INITIAL_POSITION);
            }
        }
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            RotateAnimation rotateAnimation;
            if (expanded) { // rotate clockwise
                rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            } else { // rotate counterclockwise
                rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            }

            rotateAnimation.setDuration(200);
            rotateAnimation.setFillAfter(true);
            mImageview.startAnimation(rotateAnimation);
        }
    }
}
