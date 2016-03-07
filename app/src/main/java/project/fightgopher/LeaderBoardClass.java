package project.fightgopher;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


class PlayerObject {

    private String PlayerName;
    private int PlayerScore;
    private int PlayerLV1Score;
    private int PlayerLV2Score;
    private int PlayerLV3Score;
    private String PlayerDate;


    public String getPlayerDate() {
        return PlayerDate;
    }

    public PlayerObject(String playerName, int playerScore, int playerLV1Score, int playerLV2Score, int playerLV3Score, String playerDate) {
        PlayerName = playerName;
        PlayerScore = playerScore;
        PlayerLV1Score = playerLV1Score;
        PlayerLV2Score = playerLV2Score;
        PlayerLV3Score = playerLV3Score;
        PlayerDate = playerDate;
    }

    public int getPlayerScore() {
        return PlayerScore;
    }

    public int getPlayerLV1Score() {
        return PlayerLV1Score;
    }

    public int getPlayerLV2Score() {
        return PlayerLV2Score;
    }

    public int getPlayerLV3Score() {
        return PlayerLV3Score;
    }

    public String getPlayerName() {
        return PlayerName;
    }

}

public class LeaderBoardClass {


    //儲存排行榜.xml 檔名
    public final static String LEADERBOARD_FILENAME = "leaderboard";

    private final static String DATESTRING = "yyyy/MM/dd HH:mm:ss";

    //key值
    private static String LevelOneBestScore = "LevelOneBestScore";
    private static String LevelTwoBestScore = "LevelTwoBestScore";
    private static String LevelThreeBestScore = "LevelThreeBestScore";
    public static String ScoreNamePattern = "Score_";
    public static String DateNamePattern = "Date_";
    public static String Level1PerPlayerScore = "Level1PlayerScore_";
    public static String Level2PerPlayerScore = "Level2PlayerScore_";
    public static String Level3PerPlayerScore = "Level3PlayerScore_";
    public static String versionPattern = "version";

    //儲存keys字串列
    private static String[] rankList;
    private static String[] BestScorekeys = {LevelOneBestScore, LevelTwoBestScore, LevelThreeBestScore};

    //排名數目
    private static int count;

    //context
    private Context context;
    private static SharedPreferences mPre;
    private static SharedPreferences.Editor edit;
    private static LeaderBoardClass mRankList = null;

    public static String getLevelOneBestScore() {
        return LevelOneBestScore;
    }

    public static String getLevelTwoBestScore() {
        return LevelTwoBestScore;
    }

    public static String getLevelThreeBestScore() {
        return LevelThreeBestScore;
    }

    private LeaderBoardClass(Context context, SharedPreferences sharedPreferences) {
        if (!sharedPreferences.equals(context.getSharedPreferences(LEADERBOARD_FILENAME, Context.MODE_PRIVATE)))
            throw new IllegalArgumentException("Argument should be " + LEADERBOARD_FILENAME);

        //init context
        this.context = context;

        rankList = context.getResources().getStringArray(R.array.keys);
        count = rankList.length;

        mPre = context.getSharedPreferences(LEADERBOARD_FILENAME, Context.MODE_PRIVATE);
        edit = mPre.edit();
    }


    public static LeaderBoardClass getLeaderBoardClassInstance(Context context, SharedPreferences sharedPreferences) {
        if (mRankList == null)
            mRankList = new LeaderBoardClass(context, sharedPreferences);

        //建立leaderboard.xml
        if (context.getSharedPreferences(LEADERBOARD_FILENAME, Context.MODE_PRIVATE).getInt(versionPattern, -1) < 0)
            LeaderBoardClass.initLeaderBoard();

        return mRankList;
    }

    //初始化LeaderBoard
    private static void initLeaderBoard() {

        edit.putInt(versionPattern, 1);

        //最佳分數
        edit.putInt(LevelOneBestScore, 0);
        edit.putInt(LevelTwoBestScore, 0);
        edit.putInt(LevelThreeBestScore, 0);

        for (String temp : rankList) {
            edit.putString(temp, "");
            edit.putString(DateNamePattern + temp, "");
            edit.putInt(ScoreNamePattern + temp, 0);
        }

        edit.apply();
    }

    public static int checkScoreIfHigherRank(int gamescore) {
        if (gamescore == 0)
            return -1;
        for (int i = 0; i < count; i++) {
            if (gamescore >= mPre.getInt(ScoreNamePattern + rankList[i], -1))
                return i + 1;//返回名次
        }
        //代表得分沒有比排行榜上分數高，無更新
        return -1;
    }


    //儲存紀錄
    public static boolean savePrefShared(String username, int gamescore, int[] perlevelscore) {
        SimpleDateFormat simp = new SimpleDateFormat(DATESTRING, Locale.TAIWAN);
        String nowDate = simp.format(new Date());

        ArrayList<PlayerObject> playerObjects = new ArrayList<>();

        int ShouldBeInsertIndex = 0;
        int index = 0;
        boolean lock = false;//避免更新兩次

        //取出所有舊資料包裝成ＰｌａｙｅｒＯｂｊｅｃｔ
        for (String temp : rankList) {

            if (mPre.getInt(ScoreNamePattern + temp, 0) <= gamescore & !lock) {
                ShouldBeInsertIndex = index;
                lock = true;
            }

            playerObjects.add(new PlayerObject(mPre.getString(temp, "")
                    , mPre.getInt(ScoreNamePattern + temp, 0)
                    , mPre.getInt(Level1PerPlayerScore + temp, 0)
                    , mPre.getInt(Level2PerPlayerScore + temp, 0)
                    , mPre.getInt(Level3PerPlayerScore + temp, 0)
                    , mPre.getString(DateNamePattern + temp, "")
            ));

            index++;
        }

        PlayerObject NewRecord = new PlayerObject(username, gamescore, perlevelscore[0], perlevelscore[1], perlevelscore[2], nowDate);

        //插入新紀錄
        playerObjects.add(ShouldBeInsertIndex, NewRecord);

        for (int i = 0; i < count; i++) {
            edit.putString(rankList[i], playerObjects.get(i).getPlayerName());
            edit.putInt(ScoreNamePattern + rankList[i], playerObjects.get(i).getPlayerScore());
            edit.putInt(Level1PerPlayerScore + rankList[i], playerObjects.get(i).getPlayerLV1Score());
            edit.putInt(Level2PerPlayerScore + rankList[i], playerObjects.get(i).getPlayerLV2Score());
            edit.putInt(Level3PerPlayerScore + rankList[i], playerObjects.get(i).getPlayerLV3Score());
            edit.putString(DateNamePattern + rankList[i], playerObjects.get(i).getPlayerDate());
        }
        edit.apply();

        return true;
    }

    public static boolean removeAllData() {

        Map<String, ?> temp = mPre.getAll();

        Iterator<String> iterator = temp.keySet().iterator();

        while(iterator.hasNext()) {

            edit.remove(iterator.next());

        }
        edit.apply();
        return true;
    }

    /*

    */
    public static int getBestScore(int level) {
        return mPre.getInt(BestScorekeys[level - 1], 0);
    }

    public static boolean isBestScore(int thislevelscore, int level) {

        int i = level - 1;

        if (mPre.getInt(BestScorekeys[i], 0) <= thislevelscore)
            return true;

        return false;
    }

    public static void UpdateBestScore(int score, int level) {
        edit.putInt(BestScorekeys[level - 1], score);
        edit.apply();
    }
}
