package project.fightgopher.activityclass;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import project.fightgopher.LeaderBoardClass;
import project.fightgopher.R;
import project.fightgopher.iLevelData;

import static project.fightgopher.iLevelData.HeartCountKey;
import static project.fightgopher.iLevelData.IntentKey;
import static project.fightgopher.iLevelData.ScoreArrayListKey;
import static project.fightgopher.iLevelData.perHeartScore;

public class FinishGameActivity extends AppCompatActivity {


    private Context ContextFinishGame = this;
    private Toast showToast;
    private boolean isNewRecordCreated = false;
    private boolean isPopupWindowFocus = false;

    //取得前3關分數和其他資料Bundle
    private Bundle DataBundle;

    //what
    private final int REFRESHSTAR = 1;
    private final int ANIMATION = 2;

    //音效
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundCollection = new HashMap<>();
    private View[] ViewArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_game);

        DataBundle = getIntent().getBundleExtra(IntentKey);

        ViewArray = new View[]{
                ((TextView) findViewById(R.id.showLV1Score)),
                ((TextView) findViewById(R.id.showLV2Score)),
                ((TextView) findViewById(R.id.showLV3Score)),
                ((ImageView) findViewById(R.id.heart)),
                ((TextView) findViewById(R.id.txtAwardScore)),
                ((TextView) findViewById(R.id.txtTotalScore))
        };


        ThisLevelIsComplete();

        //建立音效池
        buildSoundPool();
    }

    private void buildSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(attr)
                    .build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }


        soundCollection.put(1, soundPool.load(FinishGameActivity.this, R.raw.achievement, 1));
        soundCollection.put(2, soundPool.load(FinishGameActivity.this, R.raw.show, 1));
        soundCollection.put(3, soundPool.load(FinishGameActivity.this, R.raw.clickbutton, 1));


    }


    private Handler mainHandler = new LevelOneActivity.CustomHandler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {


            TranslateAnimation translateAnimation = new TranslateAnimation(-600, 0f, 0f, 0f);
            translateAnimation.setDuration(1200);
            translateAnimation.setRepeatCount(0);

            switch (msg.what) {
                case REFRESHSTAR:


                    //處理星星顯示
                    findViewById(msg.getData().getInt("KeyID")).setBackground(getResources().getDrawable(msg.getData().getInt("drawable")));

                    if (msg.getData().getInt("KeyID") == R.id.imgStar9)
                        findViewById(R.id.imgBtnOK).setVisibility(View.VISIBLE);

                    break;

                case ANIMATION:

                    //播放音效
                    soundPool.play(soundCollection.get(2), 1f, 1f, 10, 0, 0.8f);

                    ViewArray[msg.arg1].setVisibility(View.VISIBLE);
                    ViewArray[msg.arg1].startAnimation(translateAnimation);

                    break;
            }
        }
    };


    public void ThisLevelIsComplete() {
        if (!((AppCompatActivity) ContextFinishGame).isFinishing()) {

            final SharedPreferences preferences = getSharedPreferences(LeaderBoardClass.LEADERBOARD_FILENAME, MODE_PRIVATE);

            final ArrayList<Integer> ScoreArrayList = DataBundle.getIntegerArrayList(ScoreArrayListKey);

            final int levelOne = ScoreArrayList.get(0);
            final int levelTwo = ScoreArrayList.get(1);
            final int levelThree = ScoreArrayList.get(2);

            //愛心數目
            final int heartCount = DataBundle.getInt(HeartCountKey);

            //總分
            final int totalScore = levelOne + levelTwo + levelThree + heartCount * perHeartScore;

            //設定OnTouch傾聽器
            View.OnTouchListener BestScoreTouchEvent = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Object object = v.getTag();
                    int level = Integer.valueOf((String) object);

                    if (showToast == null)
                        showToast = Toast.makeText(ContextFinishGame, "", Toast.LENGTH_SHORT);

                    showToast.setText(String.format(getString(R.string.bestscoreToast), level, LeaderBoardClass.getBestScore(level), level == 1 ? levelOne : level == 2 ? levelTwo : levelThree));
                    showToast.show();

                    return false;
                }
            };

            //更新TextView顯示分數
            TextView txtScore1 = (TextView) findViewById(R.id.showLV1Score);
            txtScore1.setOnTouchListener(BestScoreTouchEvent);
            txtScore1.setText(String.format(getString(R.string.LV1score), levelOne));

            TextView txtScore2 = (TextView) findViewById(R.id.showLV2Score);
            txtScore2.setText(String.format(getString(R.string.LV2score), levelTwo));
            txtScore2.setOnTouchListener(BestScoreTouchEvent);

            TextView txtScore3 = (TextView) findViewById(R.id.showLV3Score);
            txtScore3.setText(String.format(getString(R.string.LV3score), levelThree));
            txtScore3.setOnTouchListener(BestScoreTouchEvent);

            ((TextView) findViewById(R.id.txtAwardScore)).setText(String.format(getString(R.string.award_heart), heartCount, iLevelData.perHeartScore, heartCount * perHeartScore));
            ((TextView) findViewById(R.id.txtTotalScore)).setText(String.format(getString(R.string.totalscoreis), totalScore));

            //星星評分執行緒
            final Thread starThread = new Thread(new Runnable() {

                int temp = 0;
                int[] best = new int[]{
                        preferences.getInt(LeaderBoardClass.getLevelOneBestScore(), 0),
                        preferences.getInt(LeaderBoardClass.getLevelTwoBestScore(), 0),
                        preferences.getInt(LeaderBoardClass.getLevelThreeBestScore(), 0)
                };
                int[] PlayerGetScore = new int[]{
                        levelOne,
                        levelTwo,
                        levelThree
                };
                int[][] starArray = new int[][]{
                        {R.id.imgStar1, R.id.imgStar2, R.id.imgStar3},
                        {R.id.imgStar4, R.id.imgStar5, R.id.imgStar6},
                        {R.id.imgStar7, R.id.imgStar8, R.id.imgStar9}
                };
                Message tempMessage;
                Bundle tempBudle;
                int tempStar = 0;

                @Override
                public void run() {
                    try {
                        while (temp < starArray.length * starArray.length) {

                            tempStar = 0;
                            int x = temp / 3;
                            int y = temp % 3;

                            tempBudle = new Bundle();
                            tempMessage = new Message();
                            tempBudle.putInt("KeyID", starArray[x][y]);

//
//                                0=空心 1=實心 預設為0 = 空心
//                                規則:得分不為0且高於該level最高分數 3顆星
//                                    得分不為0且得分和該level最高分數差距小於等於3 2顆星
//                                    得分高於0 不符合上述條件為  1顆星
//                                    得分為0 0顆星
//                                    y range:0~2 各代表從左到右星星位置
//

                            if (best[x] <= PlayerGetScore[x] & PlayerGetScore[x] > 0)   //屬於3顆星
                                tempStar = 1;
                            else if (PlayerGetScore[x] > 0 & best[x] - 3 <= PlayerGetScore[x] & y <= 1)   //2顆星
                                tempStar = 1;
                            else if (PlayerGetScore[x] > 0 & y == 0)    //1顆星
                                tempStar = 1;


                            tempBudle.putInt("drawable", tempStar == 1 ? R.drawable.icon_bestscore : R.drawable.icon_bestscoreempty);

                            tempMessage.setData(tempBudle);
                            tempMessage.what = REFRESHSTAR;
                            mainHandler.sendMessage(tempMessage);
                            temp++;

                            //每顆星星延遲顯示
                            Thread.sleep(200);
                        }

                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });


            new Thread(new Runnable() {
                @Override
                public void run() {

                    int index = 0;

                    while (index < ViewArray.length) {

                        mainHandler.sendMessage(Message.obtain(mainHandler, ANIMATION, index, 0));

                        index++;
                        try {
                            Thread.sleep(1000);
                        } catch (Exception ex) {

                        }
                    }

                    starThread.start();

                }
            }).start();


            ((ImageButton) findViewById(R.id.imgBtnOK)).

                    setOnClickListener(new View.OnClickListener() {


                                           @Override
                                           public void onClick(View v) {
                                               int rank = LeaderBoardClass.checkScoreIfHigherRank(totalScore);

                                               ScoreboardDisplay(View.INVISIBLE, false);

                                               //播放音效
                                               soundPool.play(soundCollection.get(3), 1f, 1f, 10, 0, 1);

                                               //檢查分數是否足夠上排行榜 返回排名
                                               if (rank != -1 & !isNewRecordCreated) {
                                                   isNewRecordCreated = showDialogifLeaderBoardUpdate(rank, totalScore, new int[]{levelOne, levelTwo, levelThree});
                                               } else
                                                   askNextStep();

                                           }
                                       }

                    );
        }

    }

    private void ScoreboardDisplay(int display, boolean bool) {

        if (bool)
            return;

        View[] view = {
                (ImageView) findViewById(R.id.createnameAward),
                (TextView) findViewById(R.id.showLV1Score),
                (TextView) findViewById(R.id.showLV2Score),
                (TextView) findViewById(R.id.showLV3Score),
                (ImageView) findViewById(R.id.heart),
                (TextView) findViewById(R.id.txtAwardScore),
                (ImageButton) findViewById(R.id.imgBtnOK),
                (LinearLayout) findViewById(R.id.linstar01),
                (LinearLayout) findViewById(R.id.linstar02),
                (LinearLayout) findViewById(R.id.linstar03),
                (TextView) findViewById(R.id.txtTotalScore),
                (ImageView) findViewById(R.id.imgLine)
        };

        Animation tempAnimation = AnimationUtils.loadAnimation(FinishGameActivity.this, R.anim.popupwinwow_enter);

        boolean visibleOrInvisible = display == View.VISIBLE ? true : false;

        for (int i = 0; i < view.length; i++) {
            if (visibleOrInvisible)
                view[i].startAnimation(tempAnimation);

            view[i].setVisibility(visibleOrInvisible == true ? View.VISIBLE : View.INVISIBLE);

        }

    }

    //分數如果足夠上排行榜
    private boolean showDialogifLeaderBoardUpdate(int rank, final int totalscore, final int[] perlevelscore) {


        //這方法要show的layout
        final int LAYOUT = R.layout.custom_createnamelayout;


        final View CreateView = getLayoutInflater().inflate(LAYOUT, null);
        final PopupWindow popwindow = new PopupWindow(CreateView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);


//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        Point point = new Point();
//        WindowManager windowManager = getWindowManager();
//        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
//        windowManager.getDefaultDisplay().getSize(point);
//
//        Bitmap bitmap = Bitmap.createBitmap(point.x / ((int) displayMetrics.xdpi / 160),
//                point.y / ((int) displayMetrics.ydpi / 160), Bitmap.Config.ARGB_8888);
//        popwindow.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));


        popwindow.setFocusable(true);

        popwindow.setAnimationStyle(R.style.PopupAnimation);
        popwindow.update();

        popwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ScoreboardDisplay(View.VISIBLE, isPopupWindowFocus);
            }
        });

        popwindow.showAtLocation(findViewById(R.id.createnameAward), Gravity.CENTER, 0, 0);
        final EditText editText = (EditText) CreateView.findViewById(R.id.editCreateName);

        ((TextView) CreateView.findViewById(R.id.createnameTittle)).setText(spannable(rank));


        ((ImageButton) CreateView.findViewById(R.id.btnSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果有輸入名字就儲存
                if (!editText.getText().toString().equals("")) {

                    SharedPreferences preferences = getSharedPreferences(LeaderBoardClass.LEADERBOARD_FILENAME, MODE_PRIVATE);
                    ArrayList<Integer> ScoreArrayList = DataBundle.getIntegerArrayList(ScoreArrayListKey);

                    //儲存各關卡最高分數
                    //關卡更新
                    if (preferences.getInt(LeaderBoardClass.getLevelOneBestScore(), 0) < ScoreArrayList.get(0)) {
                        LeaderBoardClass.UpdateBestScore(ScoreArrayList.get(0), 1);
                    }
                    //關卡2更新
                    if (preferences.getInt(LeaderBoardClass.getLevelTwoBestScore(), 0) < ScoreArrayList.get(1)) {
                        LeaderBoardClass.UpdateBestScore(ScoreArrayList.get(1), 2);
                    }
                    //關卡3更新
                    if (preferences.getInt(LeaderBoardClass.getLevelThreeBestScore(), 0) < ScoreArrayList.get(2)) {
                        LeaderBoardClass.UpdateBestScore(ScoreArrayList.get(2), 3);
                    }

                    //儲存紀錄
                    LeaderBoardClass.savePrefShared(editText.getText().toString(), totalscore, perlevelscore);

                    if (showToast == null)
                        showToast = Toast.makeText(ContextFinishGame, getString(R.string.savedone), Toast.LENGTH_SHORT);
                    else {
                        showToast.setText(getString(R.string.savedone));
                        showToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        showToast.show();
                    }

                }

                //播放音效
                soundPool.play(soundCollection.get(3), 1f, 1f, 10, 0, 1);

                isPopupWindowFocus = true;
                popwindow.dismiss();

                askNextStep();

            }

        });

        //不儲存按鈕
        ((ImageButton) CreateView.findViewById(R.id.btnNoSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //播放音效
                soundPool.play(soundCollection.get(3), 1f, 1f, 10, 0, 1);


                isPopupWindowFocus = true;
                popwindow.dismiss();

                askNextStep();
            }
        });


        //播放音效
        soundPool.play(soundCollection.get(1), 1f, 1f, 10, 0, 1);


        return true;
    }

    private Spannable spannable(int rank) {

        String string = String.format(getString(R.string.afterFinishAndLeadrBoardUpdate), rank);

        Matcher matcher = Pattern.compile("\\d{1,2}").matcher(string);

        int index = 0;

        if (matcher.find())
            index = TextUtils.indexOf(string, matcher.group());

        Spannable spannable = new SpannableString(string);

        //設定字體顏色
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.accuratePercent)), index, index + matcher.group().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //設定字體大小
        spannable.setSpan(new RelativeSizeSpan(2f), index, index + matcher.group().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannable;

    }

    private void askNextStep() {
        final View view = getLayoutInflater().inflate(R.layout.custom_nextstepmenu, null);
        final PopupWindow popwindow = new PopupWindow(view, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        popwindow.showAtLocation(findViewById(R.id.createnameAward), Gravity.CENTER_VERTICAL, 0, 0);
        popwindow.setAnimationStyle(R.style.PopupAnimation);
        popwindow.update();
        final RadioGroup radio = (RadioGroup) view.findViewById(R.id.radioGroup);

        view.findViewById(R.id.imgBtnNextStepOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (radio.getCheckedRadioButtonId()) {
                    case R.id.radio_backtoindex:
                        startActivity(new Intent().setClass(ContextFinishGame, IndexPage.class));
                        FinishGameActivity.this.finish();
                        break;
                    case R.id.radio_resetgame:
                        startActivity(new Intent().setClass(ContextFinishGame, LevelOneActivity.class));
                        FinishGameActivity.this.finish();
                        break;
                    case R.id.radio_backtoseeresult:

                        ScoreboardDisplay(View.VISIBLE, false);
                        popwindow.dismiss();

                        break;
                }

                //播放音效
                soundPool.play(soundCollection.get(3), 1f, 1f, 10, 0, 1);

            }
        });
    }

    @Override
    protected void onDestroy() {
        soundPool.release();
        soundPool = null;
        super.onDestroy();
    }
}

