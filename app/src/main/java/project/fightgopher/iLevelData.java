package project.fightgopher;


public interface iLevelData {


    // 分數 per heart
    int perHeartScore = 3;

    //heart key
    String HeartCountKey = "HeartCountString";

    //Intent
    String IntentKey = "levelone_score";

    //ScoreArrayListKey
    String ScoreArrayListKey = "ScoreArrayListKey";


    void ThisGameOver();

    void ThisLevelIsComplete();
}
