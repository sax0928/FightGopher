package project.fightgopher.adapter;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

public class RankObject implements ParentListItem {

    private String tittle;
    private List list;
    private String userScore;
    private String username;

    public RankObject(String tittle, String username, String userscore, List list) {
        this.tittle = tittle;
        this.list = list;
        this.userScore = userscore;
        this.username = username;
    }


    @Override
    public List<?> getChildItemList() {
        return list;
    }

    public String getTittle() {
        return tittle;
    }

    public String getScore() {
        return userScore;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

}