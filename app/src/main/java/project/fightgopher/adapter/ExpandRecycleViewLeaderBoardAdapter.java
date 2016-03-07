package project.fightgopher.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;
import java.util.List;

import project.fightgopher.holder.LeaderBoardChildHolder;
import project.fightgopher.holder.LeaderBoardParentHolder;
import project.fightgopher.R;

public class ExpandRecycleViewLeaderBoardAdapter extends ExpandableRecyclerAdapter<LeaderBoardParentHolder, LeaderBoardChildHolder> {

    private LayoutInflater mLayoutInflater;
    private Context context;

    public ExpandRecycleViewLeaderBoardAdapter(Context context, @NonNull List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public LeaderBoardParentHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        return new LeaderBoardParentHolder(mLayoutInflater.inflate(R.layout.holder_parentview, parentViewGroup, false));
    }

    @Override
    public LeaderBoardChildHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        return new LeaderBoardChildHolder(mLayoutInflater.inflate(R.layout.holder_childview, childViewGroup, false));
    }

    @Override
    public void onBindParentViewHolder(LeaderBoardParentHolder parentViewHolder, int position, ParentListItem parentListItem) {
        RankObject rankObject = (RankObject) parentListItem;
        parentViewHolder.setTittleAndScore(rankObject.getTittle(), rankObject.getUsername(), rankObject.getScore());
    }

    @Override
    public void onBindChildViewHolder(LeaderBoardChildHolder childViewHolder, int position, Object childListItem) {
        ArrayList list = (ArrayList) childListItem;
        childViewHolder.setChildHolderData(context,list);
    }
}

