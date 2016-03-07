package project.fightgopher.activityclass;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

import project.fightgopher.LeaderBoardClass;
import project.fightgopher.R;

public class IndexPage extends AppCompatActivity {

    //音效
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundCollection = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_page);

        Button ranking_button = (Button) findViewById(R.id.btnRanking);
        Button StartButton = (Button) findViewById(R.id.btnStartButton);
        //第一次進入 初始化LeaderBoard
        LeaderBoardClass.getLeaderBoardClassInstance(this, getSharedPreferences(LeaderBoardClass.LEADERBOARD_FILENAME, MODE_PRIVATE));

        buildSoundPool();

        //排行榜
        ranking_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //播放音效
                soundPool.play(soundCollection.get(1), 1f, 1f, 5, 0, 1);

                startActivity(new Intent().setClass(IndexPage.this, ExpandRecycleViewActivity.class));

                //覆寫轉換activity 動畫
                overridePendingTransition(R.anim.popupwinwow_enter, R.anim.popupwindow_exit);


            }
        });

        //開始
        StartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放音效
                soundPool.play(soundCollection.get(1), 1f, 1f, 5, 0, 1);

                startActivity(new Intent().setClass(IndexPage.this, LevelOneActivity.class));

                //覆寫轉換activity 動畫
                overridePendingTransition(R.anim.popupwinwow_enter, R.anim.popupwindow_exit);



            }
        });


    }

    private void buildSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(3)
                    .setAudioAttributes(attr)
                    .build();
        } else {
            soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        }


        soundCollection.put(1, soundPool.load(IndexPage.this, R.raw.clickbutton, 10));

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        soundPool.release();
        soundPool = null;
        super.onDestroy();

    }
}
