package com.sj.contactlistview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sj.contactlistview.util.PinyinUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author SJ
 */
public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_TOP = 0;
    private static final int TYPE_START = 1;
    private static final int TYPE_ITEM = 2;
    private static final int TYPE_BOTTOM = 3;

    private int topCount = 4;
    private int bottomCount = 1;

    private List<String> dataList = new ArrayList<>();
    private List<String> firstWordList = new ArrayList<>();
    private List<String> starList = new ArrayList<>();

    private OnClickListener listener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_TOP) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_top, parent, false);
            return new TopViewHolder(view);
        } else if (viewType == TYPE_BOTTOM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_bottom, parent, false);
            return new BottomViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TopViewHolder) {
            ((TopViewHolder) holder).textView.setText("新的朋友balabala");
            //处理底部分割线
            
            if (position == topCount - 1) {
                ((TopViewHolder) holder).bottomLine.setVisibility(View.GONE);
            } else if (((TopViewHolder) holder).bottomLine.getVisibility() != View.VISIBLE) {
                ((TopViewHolder) holder).bottomLine.setVisibility(View.VISIBLE);
            }
        } else if (holder instanceof ItemViewHolder) {
            bindViewHolder((ItemViewHolder) holder, position - topCount);
        } else if (holder instanceof BottomViewHolder) {
            ((BottomViewHolder) holder).textView.setText(String.format(Locale.getDefault(), "%d位联系人", dataList.size()));
        }
    }

    private void bindViewHolder(final ItemViewHolder holder, final int position) {
        holder.textView.setText(dataList.get(position));
        if (firstWordList.indexOf(firstWordList.get(position)) == position) {
            holder.headTextView.setText(firstWordList.get(position));
            if (holder.headTextView.getVisibility() != View.VISIBLE) {
                holder.headTextView.setVisibility(View.VISIBLE);
            }
            if (holder.headTopLine.getVisibility() != View.VISIBLE) {
                holder.headTopLine.setVisibility(View.VISIBLE);
            }
            if (holder.headBottomLine.getVisibility() != View.VISIBLE) {
                holder.headBottomLine.setVisibility(View.VISIBLE);
            }
        } else {
            holder.headTextView.setVisibility(View.GONE);
            holder.headBottomLine.setVisibility(View.GONE);
            holder.headTopLine.setVisibility(View.GONE);
        }

        //处理子项底部分割线
        if (firstWordList.lastIndexOf(firstWordList.get(position)) == position) {
            holder.bottomLine.setVisibility(View.GONE);
        } else {
            if (holder.bottomLine.getVisibility() != View.VISIBLE) {
                holder.bottomLine.setVisibility(View.VISIBLE);
            }
        }

        //事件监听
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v, position);
            }
        });
        holder.constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClick(v, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return topCount + starList.size() + dataList.size() + bottomCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < topCount) {
            return TYPE_TOP;
        } else if (position < topCount + starList.size()) {
            return TYPE_START;
        } else if (position == topCount + starList.size() + dataList.size() + bottomCount - 1) {
            return TYPE_BOTTOM;
        } else {
            return TYPE_ITEM;
        }
    }

    void setData(List<String> data) {
        this.dataList = data;
        for (String name : data) {
            String firstWord = PinyinUtils.getSurnameFirstLetter(name);
            if (firstWord != null) {
                firstWordList.add(firstWord.toUpperCase());
            }
        }

        //伪造‘星标朋友’数据

    }

    int getFirstWordListPosition(String word) {
        if (word.equals(IndexList.words[0])) {
            return 0;
        }
        if (firstWordList.indexOf(word) >= 0) {
            return firstWordList.indexOf(word) + topCount + starList.size();
        }
        return -1;
    }

    class TopViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imageView;
        private View bottomLine;

        TopViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_top_tv);
            imageView = itemView.findViewById(R.id.item_top_iv);
            bottomLine = itemView.findViewById(R.id.item_top_bottom);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView textView, headTextView;
        private ImageView imageView;
        private View bottomLine, headBottomLine, headTopLine;
        private ConstraintLayout constraintLayout;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_tv);
            headTextView = itemView.findViewById(R.id.item_head_tv);
            imageView = itemView.findViewById(R.id.item_iv);
            bottomLine = itemView.findViewById(R.id.item_bottom);
            headBottomLine = itemView.findViewById(R.id.item_head_bottom);
            headTopLine = itemView.findViewById(R.id.item_head_top);
            constraintLayout = itemView.findViewById(R.id.item);
        }
    }

    class BottomViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        BottomViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_bottom_tv);
        }
    }

    void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {

        /**
         * .
         *
         * @param view     v
         * @param position position
         */
        void onClick(View view, int position);

        /**
         * .
         *
         * @param view     v
         * @param position position
         */
        void onLongClick(View view, int position);

    }
}
