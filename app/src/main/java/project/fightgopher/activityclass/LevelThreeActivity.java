package project.fightgopher.activityclass;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import project.fightgopher.CountDownTimer;
import project.fightgopher.LeaderBoardClass;
import project.fightgopher.R;
import project.fightgopher.iLevelData;

public class LevelThreeActivity extends AppCompatActivity implements iLevelData {


    private static final String TAG = "LevelThreeActivity_TAG";
    //Context
    private Context levelThreeContext;

    //Layout
    private LinearLayout LinearHeartLayout;


    //Button&TextView
    private ImageButton pauseButton;
    private ImageButton startButton;
    private TextView txtBestScore;
    private TextView Score_Label;
    private TextView timerLabel;
    private TextView txtLevel;

    //遊戲秒數
    private int gameSecond = 15;

    //遊戲資訊
    private int ThisGameScore = 0;
    private ImageView[] imageViewsTemp;
    private boolean isGameOver = false;
    private int heartCount;
    private int clickCorrectGopher = 0; //排除會扣分的目標和炸彈
    private int clickTotal = 0;//總共點擊畫面幾次
    private boolean isPause = false;

    //mainHandler what辨識碼
    private final int REFRESH_TEXTVIEW = 1;
    private final int COUNTDOWNTIME = 2;
    private final int TIMEISUP = 3;
    private final int REFRESH_SCORELABEL = 4;
    private final int GAMEISPAUSE = 5;

    //開始遊戲倒數器
    private int tempSecond = 0;
    private ImageView countingTimeView;

    //Time Bar
    private ProgressBar timeBar;
    private int timeProgress = 0;
    private CountDownTimer countDownTimer;
    private Object lock = new Object();

    //閃爍
    private boolean flickerchange = false;
    private TextView FlickerTextView;


    //地鼠或炸彈
    private ArrayList<Integer> taskCollection;
    private Toast showHeartMessage;

    //音效
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundCollection = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_levelthree);

        levelThreeContext = this;

        taskCollection = new ArrayList<>();

        imageViewsTemp = new ImageView[]{
                (ImageView) findViewById(R.id.LV3_hole01),
                (ImageView) findViewById(R.id.LV3_hole02),
                (ImageView) findViewById(R.id.LV3_hole03),
                (ImageView) findViewById(R.id.LV3_hole04),
                (ImageView) findViewById(R.id.LV3_hole05),
                (ImageView) findViewById(R.id.LV3_hole06),
                (ImageView) findViewById(R.id.LV3_hole07),
                (ImageView) findViewById(R.id.LV3_hole08),
                (ImageView) findViewById(R.id.LV3_hole09),
                (ImageView) findViewById(R.id.LV3_hole10),
                (ImageView) findViewById(R.id.LV3_hole11),
                (ImageView) findViewById(R.id.LV3_hole12),
                (ImageView) findViewById(R.id.LV3_hole13),
                (ImageView) findViewById(R.id.LV3_hole14)
        };

        //時間倒數條
        timeBar = (ProgressBar) findViewById(R.id.timeBar);

        //開始暫停按鈕
        pauseButton = (ImageButton) findViewById(R.id.PauseButton);
        startButton = (ImageButton) findViewById(R.id.StartButton);
        pauseButton.setEnabled(false);//暫時disable 暫停按鈕
        timerLabel = (TextView) findViewById(R.id.Time);
        timerLabel.setText(String.valueOf(gameSecond));
        txtLevel = (TextView) findViewById(R.id.txtlevelnumber);
        Score_Label = (TextView) findViewById(R.id.Score);
        countingTimeView = (ImageView) findViewById(R.id.LV3_imgCountingNumber);
        heartCount = getIntent().getBundleExtra(IntentKey).getInt(HeartCountKey);
        LinearHeartLayout = (LinearLayout) findViewById(R.id.LinearHeartLayout);


        //init showHeartMessage
        showHeartMessage = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        showHeartMessage.setGravity(Gravity.TOP, 0, 500);

        GopherChildListenerOnTouch gopherChildListenerOnTouch = new GopherChildListenerOnTouch();

        for (int i = 0; i < imageViewsTemp.length; i++) {
            imageViewsTemp[i].setOnTouchListener(gopherChildListenerOnTouch);
        }

        //遊戲暫停按鈕
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                mainHandler.sendMessageAtFrontOfQueue(Message.obtain(mainHandler, GAMEISPAUSE));
                isPause = true;
                countDownTimer.pause();
            }
        });

        //開始按鈕
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (lock) {
                    startButton.setVisibility(View.INVISIBLE);
                    pauseButton.setVisibility(View.VISIBLE);
                    isPause = false;
                    countDownTimer.restart();
                    lock.notify();
                }
            }
        });

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

        //順序為點到正確目標音效、爆炸音效、遊戲結束音效、過關、扣分音效
        soundCollection.put(1, soundPool.load(LevelThreeActivity.this, R.raw.touch, 1));
        soundCollection.put(2, soundPool.load(LevelThreeActivity.this, R.raw.bomb, 1));
        soundCollection.put(3, soundPool.load(LevelThreeActivity.this, R.raw.fail, 1));
        soundCollection.put(4, soundPool.load(LevelThreeActivity.this, R.raw.levelup, 1));
        soundCollection.put(5, soundPool.load(LevelThreeActivity.this, R.raw.clicke, 1));
        soundCollection.put(6, soundPool.load(LevelThreeActivity.this, R.raw.clickbutton, 1));


    }

    private Thread sendTask = new Thread(new Runnable() {

        Bundle bundle;
        Message message;


        @Override
        public void run() {
            try {
                while (!isGameOver) {
                    synchronized (lock) {
                        while (isPause)
                            lock.wait();

                        int posView = (int) (Math.random() * imageViewsTemp.length);

                        if (taskCollection.contains(posView) & taskCollection.size() >= 4) {

                            continue;
                        }

                        taskCollection.add(posView);

                        bundle = new Bundle();
                        bundle.putInt("posView", posView);
                        message = Message.obtain();

                        RandomTask();

                        message.what = REFRESH_TEXTVIEW;
                        message.setData(bundle);
                        mainHandler.sendMessage(message);

                        Thread.sleep(300);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void RandomTask() {
            int rand = (int) (Math.random() * 10 + 1);
            switch (rand) {
                case 1:
                case 2:
                    bundle.putInt("task", 1);//丟出+1
                    break;
                case 3:
                case 4:
                case 5:
                    bundle.putInt("task", 2);//丟出炸彈事件
                    break;
                case 6:
                case 7:
                    bundle.putInt("task", 3);//丟出+3
                    break;
                case 8:
                case 9:
                    bundle.putInt("task", 4);//丟出-2
                    break;
                default:
                    bundle.putInt("task", 5);//丟出金塊事件(+5)
                    break;
            }

        }
    });


    @Override
    protected void onResume() {
        super.onResume();

        txtLevel.setText("III");

        //顯示最佳分數
        txtBestScore = (TextView) findViewById(R.id.txtBestScore);
        txtBestScore.setText(String.format(getString(R.string.getsbestscore2), LeaderBoardClass.getBestScore(3)));

        timeBar.setMax(gameSecond);
        timeBar.setProgress(timeProgress);
        //更新愛心條
        runOnUiThread(heartRefresh);

    }


    Runnable heartRefresh = new Runnable() {
        @Override
        public void run() {
            if (heartCount == 0) {
                isGameOver = true;
                mainHandler.sendEmptyMessage(TIMEISUP);
            }

            //animationStyle
            Animation animation = AnimationUtils.loadAnimation(LevelThreeActivity.this, R.anim.heart_hide);

            int alreadyHaveHeart = LinearHeartLayout.getChildCount();
            int heartCountTemp = heartCount;
            for (int i = 0; i < heartCountTemp - alreadyHaveHeart; i++) {
                ImageView heart = new ImageView(LevelThreeActivity.this);
                heart.setImageDrawable(getResources().getDrawable(R.drawable.heart));
                heart.setAnimation(animation);
                LinearHeartLayout.addView(heart);
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        countDownTimer = new CountDownTimer((gameSecond * 1000), 1000) {

            int time = gameSecond;

            @Override
            public void onFinish() {
                mainHandler.sendEmptyMessageAtTime(TIMEISUP, 0);
            }

            @Override
            public void onPerIntervalTask() {
                timerLabel.setText(String.valueOf(--time));
                timeBar.setProgress(++timeProgress);
            }
        };


        //送出開始倒數Message
        mainHandler.sendEmptyMessage(COUNTDOWNTIME);
    }

    //UiHandler
    private Handler mainHandler = new LevelOneActivity.CustomHandler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(final Message msg) {

            switch (msg.what) {
                case REFRESH_TEXTVIEW:

                    final int pos = msg.getData().getInt("posView");
                    final int task = msg.getData().getInt("task");
                    imageViewsTemp[pos]
                            .setImageDrawable(getResources().getDrawable(task == 1
                                    ? R.drawable.gopher01 : task == 2
                                    ? R.drawable.icon_bomb : task == 3
                                    ? R.drawable.gopher02 : task == 4
                                    ? R.drawable.gopher03 : R.drawable.gold));

                    this.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imageViewsTemp[pos].setImageDrawable(getResources().getDrawable(R.drawable.hole_1));
                            taskCollection.remove((Integer) pos);
                        }
                    }, 1200/*目標消失的間隔*/);
                    break;

                case TIMEISUP:
                    countDownTimer.cancel();
                    mainHandler.removeCallbacksAndMessages(null);
                    sendTask.interrupt();

                    Log.i(TAG, "Game is Ended = " + new Date().toString());
                    if (!isGameOver)   //遊戲秒數歸零 正常結束
                        ThisLevelIsComplete();
                    else              //GameOver
                        ThisGameOver();
                    break;

                case COUNTDOWNTIME:
                    new CountDownTimer(3000, 1000) {

                        int[] numberarray = {
                                R.drawable.number_three,
                                R.drawable.number_two,
                                R.drawable.number_one
                        };

                        @Override
                        public void onFinish() {
                            countingTimeView.setVisibility(View.INVISIBLE);
                            mainHandler.sendEmptyMessage(REFRESH_TEXTVIEW);
                            countDownTimer.start();
                            sendTask.start();
                            Log.i(TAG, "Game is Started = " + new Date().toString());

                            //啟用暫停按鈕
                            pauseButton.setEnabled(true);
                        }

                        @Override
                        public void onPerIntervalTask() {
                            countingTimeView.setBackground(getResources().getDrawable(numberarray[tempSecond++]));
                        }
                    }.start();
                    break;

                //更新分數
                case REFRESH_SCORELABEL:
                    Score_Label.setText(String.valueOf(ThisGameScore));
                    break;

                //遊戲暫停
                case GAMEISPAUSE:
                    mainHandler.removeMessages(REFRESH_TEXTVIEW);
                    break;
            }
        }
    };

    private void spannable(View view) {


        TextView textView = (TextView) view.findViewById(R.id.accuratePercent);

        //計算準確率 (擊中地鼠次數)/總點擊次數
        String string =
                String.format(
                        getString(R.string.accuratePercent),
                        (int) ((clickCorrectGopher / (float) clickTotal) * 100));

        Matcher matcher = Pattern.compile("\\d+").matcher(string);

        int index = 0;

        if (matcher.find())
            index = TextUtils.indexOf(string, matcher.group());

        Spannable spannable = new SpannableString(string);

        //設定字體顏色
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.accuratePercent)), index, index + matcher.group().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //設定字體大小
        spannable.setSpan(new RelativeSizeSpan(1.5f), index, index + matcher.group().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannable);

    }

    public void ThisLevelIsComplete() {
        if (!((AppCompatActivity) levelThreeContext).isFinishing()) {
            AlertDialog.Builder isTimeUp = new AlertDialog.Builder(LevelThreeActivity.this);

            final boolean isBest = LeaderBoardClass.isBestScore(ThisGameScore, 3);

            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.custom_checklayout, null);
            isTimeUp.setView(view);
            ((TextView) view.findViewById(R.id.txtCheck1)).setText(String.valueOf(ThisGameScore));

            spannable(view);

            //隱藏愛心圖示和(+1)字串
            view.findViewById(R.id.imgHeart).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.txtAward).setVisibility(View.INVISIBLE);


            if (isBest & ThisGameScore > 0) {
                FlickerTextView = (TextView) view.findViewById(R.id.txtCheck2);
                FlickerTextView.setVisibility(View.VISIBLE);
                FlickerTextView.setText(String.format(getString(R.string.bestscore), 1));
                Timer task = new Timer();
                task.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (flickerchange) {
                                    flickerchange = false;
                                    FlickerTextView.setTextColor(Color.TRANSPARENT);
                                } else {
                                    flickerchange = true;
                                    FlickerTextView.setTextColor(getResources().getColor(R.color.afterflicker));
                                }
                            }
                        });
                    }
                }, 0, 800/*閃爍間隔*/);
            }

            ImageButton btnCheck = (ImageButton) view.findViewById(R.id.btnNext);
            btnCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //記錄這場分數
                    Bundle data = new Bundle();
                    Bundle FormLV2Bundle = getIntent().getBundleExtra(IntentKey);
                    ArrayList<Integer> ScoreArrayList = FormLV2Bundle.getIntegerArrayList(ScoreArrayListKey);

                    //播放音效
                    soundPool.play(soundCollection.get(6), 1f, 1f, 10, 0, 1);

                    //放入第3關分數
                    ScoreArrayList.add(Integer.valueOf(Score_Label.getText().toString()));

                    data.putIntegerArrayList(ScoreArrayListKey, ScoreArrayList);
                    data.putInt(HeartCountKey, heartCount);

                    startActivity(new Intent().putExtra(IntentKey, data).setClass(LevelThreeActivity.this, FinishGameActivity.class));

                    LevelThreeActivity.this.finish();
                }
            });

            isTimeUp.setCancelable(false);
            isTimeUp.create().show();

            //播放音效
            soundPool.play(soundCollection.get(4), 1f, 1f, 10, 0, 1);
        }
    }

    public void ThisGameOver() {
        View gameOverView = getLayoutInflater().inflate(R.layout.custom_gameoverlayout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        gameOverView.findViewById(R.id.btnGameOver_BackToIndex).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(LevelThreeActivity.this, IndexPage.class));
                LevelThreeActivity.this.finish();
            }
        });

        gameOverView.findViewById(R.id.btnGameOver_RestGame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(LevelThreeActivity.this, LevelOneActivity.class));
                LevelThreeActivity.this.finish();
            }
        });
        builder.setCancelable(false);
        builder.setView(gameOverView);
        builder.create().show();

        //播放音效
        soundPool.play(soundCollection.get(3), 1f, 1f, 10, 0, 1);
    }

    private class GopherChildListenerOnTouch implements View.OnTouchListener {

        @Override
        public boolean onTouch(final View v, MotionEvent event) {

            if (isPause)
                return false;

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                //touch 地鼠圖片
                if (((ImageView) v).getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.gopher01).getConstantState())) {
                    ThisGameScore++;
                    ((ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.gopher_hit));

                    //擊中正確目標
                    clickCorrectGopher++;

                    mainHandler.sendEmptyMessage(REFRESH_SCORELABEL);


                    mainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.hole_1));
                        }
                    }, 200);

                    //播放音效
                    soundPool.play(soundCollection.get(1), 1f, 1f, 5, 0, 1);
                }


                //touch 炸彈圖片
                if (((ImageView) v).getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.icon_bomb).getConstantState())) {
                    ((ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.afterbomb));
                    heartCount--;
                    showHeartMessage.setView(getLayoutInflater().inflate(R.layout.custom_heartmessage, null));
                    showHeartMessage.show();

                    //移除愛心
                    ImageView temp = (ImageView) LinearHeartLayout.getChildAt(LinearHeartLayout.getChildCount() - 1);
                    temp.setAnimation(AnimationUtils.loadAnimation(LevelThreeActivity.this, R.anim.heart_hide));
                    LinearHeartLayout.removeView(temp);
                    if (heartCount == 0) {
                        isGameOver = true;
                        mainHandler.sendEmptyMessage(TIMEISUP);
                    }


                    mainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.hole_1));
                        }
                    }, 200);

                    //播放音效
                    soundPool.play(soundCollection.get(2), 1f, 1f, 5, 0, 1.5f);
                }

                //+3
                if (((ImageView) v).getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.gopher02).getConstantState())) {
                    ((ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.gopher_hit));
                    ThisGameScore += 3;

                    //擊中正確目標
                    clickCorrectGopher++;

                    mainHandler.sendEmptyMessage(REFRESH_SCORELABEL);


                    mainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.hole_1));
                        }
                    }, 200);

                    //播放音效
                    soundPool.play(soundCollection.get(1), 1f, 1f, 5, 0, 1);
                }

                //+5
                if (((ImageView) v).getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.gold).getConstantState())) {
                    ((ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.gopher_hit));
                    ThisGameScore += 5;

                    //擊中正確目標
                    clickCorrectGopher++;

                    mainHandler.sendEmptyMessage(REFRESH_SCORELABEL);


                    mainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.hole_1));
                        }
                    }, 200);

                    //播放音效
                    soundPool.play(soundCollection.get(1), 1f, 1f, 5, 0, 1);
                }

                //-2
                if (((ImageView) v).getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.gopher03).getConstantState())) {
                    ((ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.gopher_hit));
                    ThisGameScore -= 2;

                    mainHandler.sendEmptyMessage(REFRESH_SCORELABEL);


                    mainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.hole_1));
                        }
                    }, 200);

                    //播放音效
                    soundPool.play(soundCollection.get(5), 1f, 1f, 5, 0, 1);
                }

                //總次數+1
                clickTotal++;

            }
            return false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mainHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
        countDownTimer.cancel();
        sendTask.interrupt();
    }

    @Override
    protected void onDestroy() {
        getIntent().getExtras().clear();
        soundPool.release();
        soundPool = null;
        super.onDestroy();
    }
}