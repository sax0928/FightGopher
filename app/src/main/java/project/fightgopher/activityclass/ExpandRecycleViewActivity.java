package project.fightgopher.activityclass;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import project.fightgopher.adapter.ExpandRecycleViewLeaderBoardAdapter;
import project.fightgopher.adapter.RankObject;
import project.fightgopher.LeaderBoardClass;
import project.fightgopher.R;


public class ExpandRecycleViewActivity extends AppCompatActivity {

    private ExpandableRecyclerAdapter mExpandableRecycleAdapter;
    private RecyclerView mRecycleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_leaderboard);

        mRecycleView = (RecyclerView) findViewById(R.id.recycler_view);

        List<RankObject> rankObjectArrayList = new ArrayList<>();

        //取得排名字串陣列
        String[] rankLevel = getResources().getStringArray(R.array.ranklevel);

        //取得keys
        String[] keysArray = getResources().getStringArray(R.array.keys);


        SharedPreferences tmpSharedPreferences = getSharedPreferences(LeaderBoardClass.LEADERBOARD_FILENAME, MODE_PRIVATE);

        for (int i = 0; i < rankLevel.length; i++) {
            if(tmpSharedPreferences.getInt(LeaderBoardClass.ScoreNamePattern + keysArray[i], 0) ==0){
                break;
            }
            String username = tmpSharedPreferences.getString(keysArray[i], null);
            String date = tmpSharedPreferences.getString(LeaderBoardClass.DateNamePattern + keysArray[i], null);
            String score = String.valueOf(tmpSharedPreferences.getInt(LeaderBoardClass.ScoreNamePattern + keysArray[i], 0));
            int[] perlevelscore = new int[]{tmpSharedPreferences.getInt(LeaderBoardClass.Level1PerPlayerScore + keysArray[i], 0),
                    tmpSharedPreferences.getInt(LeaderBoardClass.Level2PerPlayerScore + keysArray[i], 0),
                    tmpSharedPreferences.getInt(LeaderBoardClass.Level3PerPlayerScore + keysArray[i], 0)};

            //玩家姓名、達成日期、分數 這個集合將作為ChildView資料
            ArrayList<Object> temp = new ArrayList<>();
            temp.add(keysArray[i]); //index=0
            temp.add(username);//index=1
            temp.add(date);//index=2
            temp.add(score);//index=3
            temp.add(perlevelscore);//index=4


            rankObjectArrayList.add(new RankObject(rankLevel[i], username, score, Arrays.asList(temp)));

        }

        mExpandableRecycleAdapter = new ExpandRecycleViewLeaderBoardAdapter(this, rankObjectArrayList);
        mExpandableRecycleAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @Override
            public void onListItemExpanded(int position) {
            }

            @Override
            public void onListItemCollapsed(int position) {
            }
        });

        mRecycleView.setAdapter(mExpandableRecycleAdapter);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));


        //FloatActionButton
        FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);

        FloatingActionButton fabClear = new FloatingActionButton(this);
        fabClear.setIcon(R.drawable.icon_clearall);
        fabClear.setTitle(getString(R.string.clearbutton));
        fabClear.setColorNormal(Color.TRANSPARENT);
        fabClear.setSize(FloatingActionButton.SIZE_NORMAL);
        fabClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ExpandRecycleViewActivity.this).setMessage(R.string.clearstring).setNegativeButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (LeaderBoardClass.removeAllData()) {
                            startActivity(new Intent().setClass(ExpandRecycleViewActivity.this, IndexPage.class));
                        }
                    }
                }).setPositiveButton(R.string.canel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(true).create().show();


            }
        });

        fabMenu.addButton(fabClear);

        android.support.design.widget.FloatingActionButton fab = (android.support.design.widget.FloatingActionButton)findViewById(R.id.leaderboardFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(ExpandRecycleViewActivity.this, IndexPage.class));
                ExpandRecycleViewActivity.this.finish();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mExpandableRecycleAdapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mExpandableRecycleAdapter.onRestoreInstanceState(savedInstanceState);
    }
}
