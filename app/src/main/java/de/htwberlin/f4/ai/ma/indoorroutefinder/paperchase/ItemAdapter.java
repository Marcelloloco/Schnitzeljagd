package de.htwberlin.f4.ai.ma.indoorroutefinder.paperchase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

import de.htwberlin.f4.ai.ma.indoorroutefinder.R;
import de.htwberlin.f4.ai.ma.indoorroutefinder.paperchase.models.Clue;

/**
 * Created by Yannik on 21.11.2017.
 */

public class ItemAdapter extends DragItemAdapter<Clue, ItemAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;
    private RecyclerViewClickListener itemListener;

    ItemAdapter(ArrayList<Clue> list, int layoutId, int grabHandleId, boolean dragOnLongPress, RecyclerViewClickListener itemListener) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        this.itemListener = itemListener;
        setItemList(list);
    }
    @Override
    public long getUniqueItemId(int position) {
        return getItemList().get(position).hashCode();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
//        if(position == 0){
//            holder.itemView.setBackgroundColor(Color.GRAY);
//        }else if(position == mItemList.size() -1){
//            holder.itemView.setBackgroundColor(Color.LTGRAY);
//        }
//        else{
//            holder.itemView.setBackgroundColor(Color.WHITE);
//        }
        String name = mItemList.get(position).getLoc().getId();
        String clue = mItemList.get(position).getClueText();
        holder.name.setText(name);
        holder.clueText.setText(clue);
        holder.itemView.setTag(mItemList.get(position));
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView name;
        TextView clueText;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            name = (TextView) itemView.findViewById(R.id.clue_list_item_name);
            clueText = (TextView) itemView.findViewById(R.id.clue_list_item_cluetext);
        }


        @Override
        public void onItemClicked(View view) {
            itemListener.recyclerViewListClicked(view, this.getLayoutPosition());
        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Sorry - wrong place to drag", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
